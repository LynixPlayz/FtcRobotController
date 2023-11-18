package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="MianWobotRewriteTeSTING ", group="Iterative Opmode")
public class MainWobotRewriteTESTING extends LinearOpMode {

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


        armLeft = hardwareMap.get(DcMotor.class, "armLeft");
        armRight = hardwareMap.get(DcMotor.class, "armRight");
        gripper = hardwareMap.get(Servo.class, "gripper");
        wrist = hardwareMap.get(Servo.class, "wrist");

        armLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armLeft.setTargetPosition(armHomePosition);
        armRight.setTargetPosition(armHomePosition);
        armLeft.setPower(1.0);
        armRight.setPower(1.0);
        armLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        waitForStart();


        double movePerSecond = 0;
        while (opModeIsActive()) {
            String motorName = "None";
            double startingPos;
            double endingPos;
            double timeElapsed;
            if (gamepad1.a) {
                startingPos = backLeft.getCurrentPosition();
                backLeft.setPower(5.0);
                Thread.sleep(500);
                timeElapsed = 0.5D;
                endingPos = backLeft.getCurrentPosition();
                movePerSecond = (endingPos - startingPos) / timeElapsed;
            }
            else
            {
                backLeft.setPower(0);
            }
            if (gamepad1.b) {
                startingPos = frontLeft.getCurrentPosition();
                frontLeft.setPower(5.0);
                Thread.sleep(500);
                timeElapsed = 0.5D;
                frontLeft.setPower(0);
                endingPos = frontLeft.getCurrentPosition();
                movePerSecond = (endingPos - startingPos) / timeElapsed;
            }
            if (gamepad1.x) {
                startingPos = frontRight.getCurrentPosition();
                frontRight.setPower(5.0);
                Thread.sleep(500);
                timeElapsed = 0.5D;
                frontRight.setPower(0);
                endingPos = frontRight.getCurrentPosition();
                movePerSecond = (endingPos - startingPos) / timeElapsed;
            }
            if (gamepad1.y) {
                startingPos = backRight.getCurrentPosition();
                backRight.setPower(5.0);
                Thread.sleep(500);
                timeElapsed = 0.5D;
                backRight.setPower(0);
                endingPos = backRight.getCurrentPosition();
                movePerSecond = (endingPos - startingPos) / timeElapsed;
            }


            telemetry.addData("movePerSecond", motorName + ": " + roundDecimal(movePerSecond / 2200, 2));
            telemetry.update();
        }
    }

    public double roundDecimal(double number, int decimalPlace)
    {
        return Math.abs(number * Math.pow(10, decimalPlace))  / Math.pow(10, decimalPlace);
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
        returnString += "rightDriveBack " + backRight.getCurrentPosition() + "";

        return returnString;
    }

    public void processJoystickInputToMovement()
    {
        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        double turn = gamepad1.right_stick_x;

        //math stuff I dont understand :P
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(turn), 1);

        frontLeft.setPower( (y + x + turn) / denominator);
        backLeft.setPower( (y - x + turn) / denominator);
        frontRight.setPower( (y - x - turn) / denominator);
        backRight.setPower( ( y + x - turn) / denominator);

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

    public void processGamepadScoringPos()
    {
        if (gamepad1.y) {
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
        if (gamepad1.left_bumper || gamepad1.right_bumper) {
            gripper.setPosition(gripperOpenPosition);
        }
        else {
            gripper.setPosition(gripperClosedPosition);
        }
    }
}
