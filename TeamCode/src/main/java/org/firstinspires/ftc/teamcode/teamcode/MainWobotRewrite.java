package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="MianWobotRewrite", group="Iterative Opmode")
public class MainWobotRewrite extends LinearOpMode {

    public DcMotorEx frontLeft = null;
    public DcMotorEx backLeft = null;
    public DcMotorEx frontRight = null;
    public DcMotorEx backRight = null;
    //public DcMotorEx armLeft = null;
    public DcMotorEx arm = null;

    private Servo gripper = null;
    private Servo wrist = null;

    private boolean manualMode = false;

    private final double wristUpPosition = 1.0;
    private final double wristDownPosition = -1.0;

    private final int armHomePosition = 20;


    @Override
    public void runOpMode() {

        frontLeft = hardwareMap.get(DcMotorEx.class, "leftDriveFront");
        frontRight = hardwareMap.get(DcMotorEx.class, "rightDriveFront");

        backLeft = hardwareMap.get(DcMotorEx.class, "leftDriveBack");
        backRight = hardwareMap.get(DcMotorEx.class, "rightDriveBack");


        ////armLeft  = hardwareMap.get(DcMotorEx.class, "armLeft");
        arm = hardwareMap.get(DcMotorEx.class, "arm");
        gripper = hardwareMap.get(Servo.class, "gripper");
        wrist = hardwareMap.get(Servo.class, "wrist");

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        ////armLeft.setDirection(DcMotorEx.Direction.FORWARD);
        arm.setDirection(DcMotorEx.Direction.REVERSE);
        ////armLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        ////armLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        arm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        ////armLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        arm.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        ////armLeft.setPower(0.0);
        arm.setPower(0.0);

        waitForStart();

        //armLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        //armLeft.setTargetPosition(armHomePosition);
        arm.setTargetPosition(armHomePosition);
        //armLeft.setVelocity(1000);
        arm.setVelocity(1000);
        //armLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);

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
        telemetry.addData("Gamepad Inputs", getGamepadDebugInfo() + "\n\n" + getMotorsDebugInfo());
        telemetry.update();
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

        //returnString += "leftArmMotor " + armLeft.getCurrentPosition() + "\n";
        returnString += "rightArmMotor " + arm.getCurrentPosition() + "\n";

        returnString += "leftDriveFront " + frontLeft.getCurrentPosition() + "\n";
        returnString += "rightDriveBack " + backLeft.getCurrentPosition() + "\n";
        returnString += "leftDriveFront " + frontRight.getCurrentPosition() + "\n";
        returnString += "rightDriveBack " + backRight.getCurrentPosition() + "\n";
        returnString += "leftDriveFront " + frontRight.getCurrentPosition() + "\n";
        //returnString += "leftArmTarget " + armLeft.getTargetPosition() + "\n";
        returnString += "rightArmTarget " + arm.getTargetPosition() + "\n";
        returnString += "wrist " + wrist.getPosition() + "";


        return returnString;
    }

    public void processJoystickInputToMovement()
    {
        double y = -gamepad1.left_stick_x; // Remember, Y stick value is reversed
        double x = gamepad1.left_stick_y * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        double frontLeftCalib = 1;
        double backLeftCalib = 1;
        double frontRightCalib = 1;
        double backRightCalib = 1;

        frontLeft.setPower(frontLeftPower / frontLeftCalib);
        backLeft.setPower(backLeftPower / backLeftCalib);
        frontRight.setPower(frontRightPower / frontRightCalib);
        backRight.setPower(backRightPower / backRightCalib);

    }

    public void processGamepadHomePos()
    {
        if(gamepad1.x)
        {
            //armLeft.setTargetPosition(armHomePosition);
            arm.setTargetPosition(armHomePosition);
            //armLeft.setVelocity(1000);
            arm.setVelocity(1000);
            //armLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            wrist.setPosition(wristUpPosition);
        }
    }

    public void processMoveArmTriggers()
    {
        double manualArmPower;
        manualArmPower = gamepad1.right_trigger - gamepad1.left_trigger;
        double armManualDeadband = 0.03;
        if (Math.abs(manualArmPower) > armManualDeadband) {
            if (!manualMode) {
                //armLeft.setPower(0.0);
                arm.setPower(0.0);
                //armLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                arm.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                manualMode = true;
            }
            //armLeft.setPower(manualArmPower);
            arm.setPower(manualArmPower);
        }
    }

    public void lockArmProcessGamepad()
    {
        if(gamepad1.a)
        {
            //armLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            arm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            //armLeft.setPower(0);
            arm.setPower(0);
        }
    }

    public void processGamepadScoringPos()
    {
        if (gamepad1.y) {
            int armScorePosition = -600;
            //armLeft.setTargetPosition(armScorePosition);
            arm.setTargetPosition(armScorePosition);
            //armLeft.setVelocity(600);
            arm.setVelocity(600);
            //armLeft.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
            arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
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
            double gripperOpenPosition = 0;
            gripper.setPosition(gripperOpenPosition);
        }
        else {
            double gripperClosedPosition = 1.0;
            gripper.setPosition(gripperClosedPosition);
        }
    }
}
