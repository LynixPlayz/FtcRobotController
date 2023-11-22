package org.firstinspires.ftc.teamcode.teamcode.Parts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Movement extends Part {
    public DcMotorEx frontLeft = null;
    public DcMotorEx backLeft = null;
    public DcMotorEx frontRight = null;
    public DcMotorEx backRight = null;

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
    public void loop()
    {
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

}
