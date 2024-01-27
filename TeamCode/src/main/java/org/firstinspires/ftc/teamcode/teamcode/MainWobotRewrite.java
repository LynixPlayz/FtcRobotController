package org.firstinspires.ftc.teamcode.teamcode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Arm;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Gripper;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Movement;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Wrist;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name="MianWobotRewrite", group="Iterative Opmode")
public class MainWobotRewrite extends LinearOpMode {
    Movement movement = new Movement();
    Arm arm = new Arm();
    Gripper gripper = new Gripper();
    Wrist wrist = new Wrist();
    private IMU imu         = null;
    public Servo launcher;

    int testRunCount = 0;


    // Adjust these numbers to suit your robot.
    final double DESIRED_DISTANCE = 12.0; //  this is how close the camera should get to the target (inches)

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    final double SPEED_GAIN  =  0.02  ;   //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN =  0.015 ;   //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
    final double TURN_GAIN   =  0.01  ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)

    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int DESIRED_TAG_ID = -1;     // Choose the tag you want to approach or set to -1 for ANY tag.
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;

    public static MainWobotRewrite SELF;

    public static MainWobotRewrite getInstance(){
        return SELF;
    }

    //Starting point for the robot
    @Override
    public void runOpMode() {
        SELF = this;
        movement.hardwareMap = hardwareMap;
        arm.hardwareMap = hardwareMap;
        gripper.hardwareMap = hardwareMap;
        wrist.hardwareMap = hardwareMap;

        movement.init();
        gripper.init();
        arm.init();
        wrist.init();
        launcher = hardwareMap.get(Servo.class, "launcher");

        boolean targetFound     = false;    // Set to true when an AprilTag target is detected
        double  drive           = 0;        // Desired forward power/speed (-1 to +1)
        double  strafe          = 0;        // Desired strafe power/speed (-1 to +1)
        double  turn            = 0;        // Desired turning power/speed (-1 to +1)

        initAprilTag();

        // Retrieve the IMU from the hardware map
        imu = hardwareMap.get(IMU.class, "imu");
        imu.resetYaw();

        SampleMecanumDrive driveMecanum = new SampleMecanumDrive(hardwareMap);
        driveMecanum.setPoseEstimate(new Pose2d(10, 10, Math.toRadians(90)));



        waitForStart();

        while(opModeIsActive())
        {
            movement.loop(gamepad1);

            wrist.loop();
            movement.processJoystickInputToMovement(gamepad1);
            arm.armTriggersGamepad(gamepad1);
            gripper.gripperMovementGamepad(gamepad1);
            wrist.gamepadMovement(gamepad1);
            processGamepadHomePos();
            lockArmProcessGamepad();
            processGamepadScoringPos();
            telemetry.addData("launcher", launcher.getPosition());
            telemetryUpdate();
            if(gamepad1.b){
                launcher.setPosition(1);
            }
            if(gamepad1.back){
                launcher.setPosition(0);
            }
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
        returnString += "arm mode " + arm.arm.getMode();
        returnString += "gripper pos" + gripper.gripper.getPosition();

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

        returnString += "testRunCount " + testRunCount + "\n";

        return returnString;
    }

    public String getMotorsDebugInfo()
    {
        String returnString = "";

        returnString += movement.debugInfo;
        returnString += "\n";
        returnString += arm.getDebugString();

       return returnString;
    }

    public void processGamepadHomePos()
    {
        if(gamepad1.x)
        {
            arm.moveToHomePos();
            wrist.SELF.setPosition(wrist.wristUpPosition);
        }
    }

    public void lockArmProcessGamepad()
    {
        if(gamepad1.a)
        {
            //arm.lockArm();
            arm.armBypass = !arm.armBypass;
            testRunCount++;
        }
    }

    public void processGamepadScoringPos()
    {
        if (gamepad1.y) {
            arm.scoringPosition();
            wrist.SELF.setPosition(wrist.wristDownPosition);
        }
    }

    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }
}
