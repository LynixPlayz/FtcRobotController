package org.firstinspires.ftc.teamcode.teamcode.Parts;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.teamcode.MainWobotRewrite;

public class Movement extends Part {
    public HardwareMap hardwareMap;

    private IMU imu         = null;      // Control/Expansion Hub IMU

    private double          headingError  = 0;

    // These variable are declared here (as class members) so they can be updated in various methods,
    // but still be displayed by sendTelemetry()
    private double  targetHeading = 0;
    private double  driveSpeed    = 0;
    private double  turnSpeed     = 0;
    private double  leftSpeed     = 0;
    private double  rightSpeed    = 0;
    private double  backLeftSpeed     = 0;
    private double  backRightSpeed    = 0;
    private int     leftTarget    = 0;
    private int     rightTarget   = 0;
    private int     backLeftTarget    = 0;
    private int     backRightTarget   = 0;

    public String debugInfo = "";


    @Override
    public void init()
    {

        // Retrieve the IMU from the hardware map
        imu = hardwareMap.get(IMU.class, "imu");
        imu.resetYaw();
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
        return returnString;
    }

    public void processJoystickInputToMovement(Gamepad gamepad1)
    {
        /*double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        // This button choice was made so that it is hard to hit on accident,
        // it can be freely changed based on preference.
        // The equivalent button is start on Xbox-style controllers.
        if (gamepad1.options) {
            imu.resetYaw();
        }

        double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the movement direction counter to the bot's rotation
        double rotX = y * Math.cos(-botHeading) - x * Math.sin(-botHeading);
        double rotY = y * Math.sin(-botHeading) + x * Math.cos(-botHeading);

        rotX = rotX * 1.1;  // Counteract imperfect strafing

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);*/
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        drive.setWeightedDrivePower(
                new Pose2d(
                        -gamepad1.left_stick_y,
                        -gamepad1.left_stick_x,
                        -gamepad1.right_stick_x
                )
        );

        drive.update();
    }


    public void debugDpad(Gamepad gamepad1)
    {
        /* final variables, no change needed
        if(gamepad1.dpad_left)
        {
            COUNTS_PER_MOTOR_REV -= 10;
        }
        if(gamepad1.dpad_right)
        {
            COUNTS_PER_MOTOR_REV += 10;
        }*/
    }
}
