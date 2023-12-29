package org.firstinspires.ftc.teamcode.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ForwardPushTest", group = "Tests")
public class ForwardPushTest extends LinearOpMode {
    public DcMotor frontLeft = null;
    public DcMotor backLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backRight = null;
    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.get(DcMotor.class, "leftDriveFront");
        frontRight = hardwareMap.get(DcMotor.class, "rightDriveFront");
        backLeft = hardwareMap.get(DcMotor.class, "leftDriveBack");
        backRight = hardwareMap.get(DcMotor.class, "rightDriveBack");

        double startFL = frontLeft.getCurrentPosition();
        double startBL = backLeft.getCurrentPosition();
        double startFR = frontRight.getCurrentPosition();
        double startBR = backRight.getCurrentPosition();

        waitForStart();

        while(opModeIsActive())
        {
            telemetry.addData("FL: ", frontLeft.getCurrentPosition() - startFL);
            telemetry.addData("BL: ", backLeft.getCurrentPosition() - startBL);
            telemetry.addData("FR: ", frontRight.getCurrentPosition() - startFR);
            telemetry.addData("BR: ", backRight.getCurrentPosition() - startBR);
            telemetry.update();
        }
    }
}
