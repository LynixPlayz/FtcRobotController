package org.firstinspires.ftc.teamcode.teamcode.Parts;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.teamcode.Autonomous.MoveDirection;

public class Movement extends Part {
    public DcMotorEx frontLeft = null;
    public DcMotorEx backLeft = null;
    public DcMotorEx frontRight = null;
    public DcMotorEx backRight = null;
    public HardwareMap hardwareMap;

    static double COUNTS_PER_MOTOR_REV = 2200;
    static final double DRIVE_GEAR_REDUCTION = 20.0;
    static final double WHEEL_DIAMETER_INCHES = 75 / 25.4;

    public String debugInfo = "";

    @Override
    public void init()
    {
        frontLeft = hardwareMap.get(DcMotorEx.class, "leftDriveFront");
        frontRight = hardwareMap.get(DcMotorEx.class, "rightDriveFront");

        backLeft = hardwareMap.get(DcMotorEx.class, "leftDriveBack");
        backRight = hardwareMap.get(DcMotorEx.class, "rightDriveBack");

        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop(Gamepad gamepad1)
    {
        debugDpad(gamepad1);
        debugInfo = getDebugInfo();
    }

    public String getDebugInfo()
    {
        String returnString = "";
        returnString += "leftDriveFront " + frontLeft.getCurrentPosition() + "\n";
        returnString += "rightDriveBack " + backLeft.getCurrentPosition() + "\n";
        returnString += "leftDriveFront " + frontRight.getCurrentPosition() + "\n";
        returnString += "rightDriveBack " + backRight.getCurrentPosition() + "\n";
        returnString += "leftDriveFront " + frontRight.getCurrentPosition() + "\n";
        returnString += "motor power " + COUNTS_PER_MOTOR_REV;
        return returnString;
    }

    public void processJoystickInputToMovement(Gamepad gamepad1)
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

    public void move(MoveDirection direction, int inches, double speed)
    {
        double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                (WHEEL_DIAMETER_INCHES * 3.1415);
        if(direction == MoveDirection.FORWARD)
        {
            frontLeft.setTargetPosition((int) ((inches * COUNTS_PER_INCH) / 4) * -1);
            frontRight.setTargetPosition((int) ((inches * COUNTS_PER_INCH) / 4));
            backLeft.setTargetPosition((int) ((inches * COUNTS_PER_INCH) / 4));
            backRight.setTargetPosition((int) ((inches * COUNTS_PER_INCH) / 4) * -1);

            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            frontLeft.setPower(speed);
            frontRight.setPower(speed);
            backLeft.setPower(speed);
            backRight.setPower(speed);
        }
    }

    public void debugDpad(Gamepad gamepad1)
    {
        if(gamepad1.dpad_left)
        {
            COUNTS_PER_MOTOR_REV -= 10;
        }
        if(gamepad1.dpad_right)
        {
            COUNTS_PER_MOTOR_REV += 10;
        }
    }
}
