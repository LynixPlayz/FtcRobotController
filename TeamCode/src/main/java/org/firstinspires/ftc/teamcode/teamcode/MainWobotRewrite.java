package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="MianWobotRewrite", group="Iterative Opmode")
public class MainWobotRewrite extends LinearOpMode {

    public DcMotor frontLeft = null;
    public DcMotor backLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backRight = null;
    public DcMotor armLeft = null;
    public DcMotor armRight = null;

    private Servo gripper = null;
    private Servo wrist = null;

    private boolean manualMode = false;
    private double armSetpoint = 0.0;

    private final double armManualDeadband = 0.03;

    private final double gripperClosedPosition = 1.0;
    private final double gripperOpenPosition = 0;
    private final double wristUpPosition = 1.0;
    private final double wristDownPosition = -1.0;

    private final int armHomePosition = 20;
    private final int armIntakePosition = 30;
    private final int armScorePosition = -600;
    private final int armShutdownThreshold = 5;

    private boolean debug = true;
    private boolean debugPositions = true;


    @Override
    public void runOpMode() throws InterruptedException {

        frontLeft = hardwareMap.get(DcMotor.class, "leftDriveFront");
        frontRight = hardwareMap.get(DcMotor.class, "rightDriveFront");

        backLeft = hardwareMap.get(DcMotor.class, "leftDriveBack");
        backRight = hardwareMap.get(DcMotor.class, "rightDriveBack");


        armLeft  = hardwareMap.get(DcMotor.class, "armLeft");
        armRight = hardwareMap.get(DcMotor.class, "armRight");
        gripper = hardwareMap.get(Servo.class, "gripper");
        wrist = hardwareMap.get(Servo.class, "wrist");

        armLeft.setDirection(DcMotor.Direction.FORWARD);
        armRight.setDirection(DcMotor.Direction.REVERSE);
        armLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armLeft.setPower(0.0);
        armRight.setPower(0.0);

        waitForStart();

        armLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLeft.setTargetPosition(armHomePosition);
        armRight.setTargetPosition(armHomePosition);
        armLeft.setPower(1.0);
        armRight.setPower(1.0);
        armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while(opModeIsActive())
        {
            processMoveArmTriggers();
            processJoystickInputToMovement();
            processGamepadHomePos();
            processGamepadGripperMovement();
            wristDpadMovement();
            lockArmProcessGamepad();

                processGamepadScoringPos();

                telemetryUpdate();
            }
    }

    public void telemetryUpdate()
    {
        if (debug) {
            telemetry.addData("Gamepad Inputs", getGamepadDebugInfo() + "\n\n" + getMotorsDebugInfo());
            telemetry.update();
        }
    }

    public String getGamepadDebugInfo()
    {
        String returnString = "";
        returnString += "dpad_up " + gamepad1.dpad_up + "\n";
        returnString += "dpad_down " + gamepad1.dpad_down + "\n";
        returnString += "dpad_left " + gamepad1.dpad_left + "\n";
        returnString += "dpad_right " + gamepad1.dpad_right + "\n";

        returnString += "a " + gamepad1.a + "\n";
        returnString += "b " + gamepad1.b + "\n";
        returnString += "x " + gamepad1.x + "\n";
        returnString += "y " + gamepad1.y + "\n";

        returnString += "gamepadXRight " + gamepad1.right_stick_x + "\n";
        returnString += "gamepadYRight " + gamepad1.right_stick_y + "\n";
        returnString += "gamepadXLeft " + gamepad1.left_stick_x + "\n";
        returnString += "gamepadYLeft " + gamepad1.left_stick_y + "\n";

        returnString += "leftBumper " + gamepad1.left_bumper + "\n";
        returnString += "rightBumper " + gamepad1.right_bumper + "\n";

        returnString += "leftTrigger " + gamepad1.left_trigger + "\n";
        returnString += "rightTrigger " + gamepad1.right_trigger + "";

        return returnString;
    }

    public String getMotorsDebugInfo()
    {
        String returnString = "";

        returnString += "leftArmMotor " + armLeft.getCurrentPosition() + "\n";
        returnString += "rightArmMotor " + armRight.getCurrentPosition() + "\n";

        returnString += "leftDriveFront " + frontLeft.getCurrentPosition() + "\n";
        returnString += "rightDriveBack " + backLeft.getCurrentPosition() + "\n";
        returnString += "leftDriveFront " + frontRight.getCurrentPosition() + "\n";
        returnString += "rightDriveBack " + backRight.getCurrentPosition() + "\n";
        returnString += "leftDriveFront " + frontRight.getCurrentPosition() + "\n";
        returnString += "leftArmTarget " + armLeft.getTargetPosition() + "\n";
        returnString += "rightArmTarget " + armRight.getTargetPosition() + "\n";
        returnString += "wrist " + wrist.getPosition() + "";


        return returnString;
    }

    public void processJoystickInputToMovement()
    {
        double frontLeftCalibration = 1.14;
        double backLeftCalibration = 1.21;
        double frontRightCalibration = 1.09;
        double backRightCalibration = 1.15;
        /*double y = -gamepad2.left_stick_y;
        double x = gamepad2.left_stick_x;

        double turn = gamepad2.right_stick_x;

        //math stuff I dont understand :P
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(turn), 1);

        frontLeft.setPower( ((y + x + turn) / denominator) / 2.6 );
        backLeft.setPower( ((y - x + turn) / denominator ) / 1.626);
        frontRight.setPower( ((y - x - turn) / denominator) / 2.52);
        backRight.setPower( (( y + x - turn) / denominator) / 2.594);*/

        // Strafing on Right Stick using the X-axis
        // Move the stick right and left for the robot to slide right and left
        frontLeft.setPower(gamepad1.right_stick_x);
        backLeft.setPower(-gamepad1.right_stick_x);
        frontRight.setPower(gamepad1.right_stick_x);
        backRight.setPower(-gamepad1.right_stick_x);
        // Turning on Left Joystick using X-Axis
        frontLeft.setPower(gamepad1.left_stick_x);
        backLeft.setPower(gamepad1.left_stick_x);
        frontRight.setPower(gamepad1.left_stick_x);
        backRight.setPower(gamepad1.left_stick_x);
        // Forwards and Back on Left Stick using the Y-axis
        frontLeft.setPower(-gamepad1.left_stick_y);
        backLeft.setPower(-gamepad1.left_stick_y);
        frontRight.setPower(gamepad1.left_stick_y);
        backRight.setPower(gamepad1.left_stick_y);

    }

    public void processGamepadHomePos()
    {
        if(gamepad1.x)
        {
            armLeft.setTargetPosition(armHomePosition);
            armRight.setTargetPosition(armHomePosition);
            armLeft.setPower(1.0);
            armRight.setPower(1.0);
            armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wrist.setPosition(wristUpPosition);
        }
    }

    public void processMoveArmTriggers()
    {
        double manualArmPower;
        manualArmPower = gamepad1.right_trigger - gamepad1.left_trigger;
        if (Math.abs(manualArmPower) > armManualDeadband) {
            if (!manualMode) {
                armLeft.setPower(0.0);
                armRight.setPower(0.0);
                armLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                armRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                manualMode = true;
            }
            armLeft.setPower(manualArmPower);
            armRight.setPower(manualArmPower);
        }
    }

    public void lockArmProcessGamepad()
    {
        if(gamepad1.a)
        {
            armLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            armRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            armLeft.setPower(0);
            armRight.setPower(0);
        }
    }

    public void processGamepadScoringPos()
    {
        if (gamepad1.y) {
            armLeft.setTargetPosition(armScorePosition);
            armRight.setTargetPosition(armScorePosition);
            armLeft.setPower(0.5);
            armRight.setPower(0.5);
            armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wrist.setPosition(wristDownPosition);
        }
    }

    public void wristDpadMovement()
    {
        if(gamepad1.dpad_up)
        {
            wrist.setPosition(wristUpPosition);
        } else if (gamepad1.dpad_down) {
            wrist.setPosition(wristDownPosition);
        }
    }

    public void processGamepadGripperMovement()
    {
        if (gamepad1.left_bumper || gamepad1.right_bumper) {
            gripper.setPosition(gripperOpenPosition);
        }
        else {
            gripper.setPosition(gripperClosedPosition);
        }
    }
}
