package org.firstinspires.ftc.teamcode;

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
    private final double wristDownPosition = 0.0;

    private final int armHomePosition = 0;
    private final int armIntakePosition = 10;
    private final int armScorePosition = 400;
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

        waitForStart();

        while(opModeIsActive())
        {
            processJoystickInputToMovement();
            processGamepadHomePos();
            processMoveArmTriggers();
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
        returnString += "dpad_up " + gamepad2.dpad_up + "\n";
        returnString += "dpad_down " + gamepad2.dpad_down + "\n";
        returnString += "dpad_left " + gamepad2.dpad_left + "\n";
        returnString += "dpad_right " + gamepad2.dpad_right + "\n";

        returnString += "a " + gamepad2.a + "\n";
        returnString += "b " + gamepad2.b + "\n";
        returnString += "x " + gamepad2.x + "\n";
        returnString += "y " + gamepad2.y + "\n";

        returnString += "gamepadXRight " + gamepad2.right_stick_x + "\n";
        returnString += "gamepadYRight " + gamepad2.right_stick_y + "\n";
        returnString += "gamepadXLeft " + gamepad2.left_stick_x + "\n";
        returnString += "gamepadYLeft " + gamepad2.left_stick_y + "\n";

        returnString += "leftBumper " + gamepad2.left_bumper + "\n";
        returnString += "rightBumper " + gamepad2.right_bumper + "\n";

        returnString += "leftTrigger " + gamepad2.left_trigger + "\n";
        returnString += "rightTrigger " + gamepad2.right_trigger + "";

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
        returnString += "rightDriveBack " + backRight.getCurrentPosition() + "";

        return returnString;
    }

    public void processJoystickInputToMovement()
    {
        double y = -gamepad2.left_stick_y;
        double x = gamepad2.left_stick_x;

        double turn = gamepad2.right_stick_x;

        //math stuff I dont understand :P
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(turn), 1);

        frontLeft.setPower( (y + x + turn) / denominator);
        backLeft.setPower( (y - x + turn) / denominator);
        frontRight.setPower( (y - x - turn) / denominator);
        backRight.setPower( ( y + x - turn) / denominator);

    }

    public void processGamepadHomePos()
    {
        if(gamepad2.x)
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
        manualArmPower = gamepad2.right_trigger - gamepad2.left_trigger;
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

    public void processGamepadScoringPos()
    {
        if (gamepad2.y) {
            armLeft.setTargetPosition(armScorePosition);
            armRight.setTargetPosition(armScorePosition);
            armLeft.setPower(1.0);
            armRight.setPower(1.0);
            armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            wrist.setPosition(wristUpPosition);
        }
    }

    public void processGamepadGripperMovement()
    {
        if (gamepad2.left_bumper || gamepad2.right_bumper) {
            gripper.setPosition(gripperOpenPosition);
        }
        else {
            gripper.setPosition(gripperClosedPosition);
        }
    }
}
