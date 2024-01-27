package org.firstinspires.ftc.teamcode.teamcode.OpenCV;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.teamcode.Autonomous.Calbration;
import org.firstinspires.ftc.teamcode.teamcode.OpenCV.TeamElementSubsystem;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;


@com.qualcomm.robotcore.eventloop.opmode.Autonomous(group = "StateTest")
    public class Autonomous extends LinearOpMode {

    static final String SPIKE_CENTER = "center";
    static final String SPIKE_LEFT = "left";
    static final String SPIKE_RIGHT = "right";

    static final String SIDE_FAR = "far";
    static final String SIDE_CLOSE = "close";

    static final String ALLIANCE_RED = "red";
    static final String ALLIANCE_BLUE = "blue";

    public DcMotor frontLeft = null;
    public DcMotor backLeft = null;
    public DcMotor frontRight = null;
    public DcMotor backRight = null;

    public Servo gripper = null;
    public Servo wrist = null;

    AndroidSoundPool androidSoundPool = new AndroidSoundPool();

    Calbration c = new Calbration();



    enum State {
        SPIKE,
        BACKDROP,
        ALIGN,
        PLACE,
        PARK,
        IDLE
    }

    State currentState = State.IDLE;

    int armSlidePos = -1;

    private TeamElementSubsystem teamElementDetection=null;

    public void driveForward(int inches, float speed)
    {
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + c.FLPerInch * inches);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + c.BLPerInch * inches);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + c.FRPerInch * inches);
        backRight.setTargetPosition(backRight.getCurrentPosition() + c.BRPerInch * inches);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(1);
        backLeft.setPower(1);
        frontRight.setPower(1);
        backRight.setPower(1);
        /**/
    }

    @Override
    public void runOpMode() throws InterruptedException {

        frontLeft = hardwareMap.get(DcMotor.class, "leftDriveFront");
        frontRight = hardwareMap.get(DcMotor.class, "rightDriveFront");
        backLeft = hardwareMap.get(DcMotor.class, "leftDriveBack");
        backRight = hardwareMap.get(DcMotor.class, "rightDriveBack");

        gripper = hardwareMap.get(Servo.class, "gripper");
        wrist = hardwareMap.get(Servo.class, "wrist");

        teamElementDetection = new TeamElementSubsystem(hardwareMap);

        String[] selectedArray = autoSelector();

        String alliance = selectedArray[0];

        String spike = selectedArray[1];
        String side = selectedArray[2];

        double startY = 0, startX = 0, startH = 0;

        if (side.equals(SIDE_FAR) && alliance.equals(ALLIANCE_RED)) {
            startX = -35;
            startY = -64;
        } else if (side.equals(SIDE_CLOSE) && alliance.equals(ALLIANCE_RED)) {
            startX = 12;
            startY = -64;
        } else if (side.equals(SIDE_FAR) && alliance.equals(ALLIANCE_BLUE)) {
            startX = -35;
            startY = 64;
        } else if (side.equals(SIDE_CLOSE) && alliance.equals(ALLIANCE_BLUE)) {
            startX = 12;
            startY = 64;
        }

        if (alliance.equals(ALLIANCE_RED)) {
            startH = 180;
        } else {
            startH = 0;
        }


        double sideOffset = 0;
        double specialSpikeOffset = 0;
        double sideSpecialSpikeOffset = 0;
        if (side.equals(SIDE_CLOSE)) {
            sideOffset = 47;
        } else if (side.equals(SIDE_FAR)) {
            specialSpikeOffset = 23.5;
            sideSpecialSpikeOffset = 47;
        }

        int allianceFlip = 1;
        if (alliance.equals(ALLIANCE_BLUE)) {
            allianceFlip = -1;
        }



        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Trajectory traj = drive.trajectoryBuilder(new Pose2d())
                .forward(5)
                .splineTo(new Vector2d(3.5, 0), 0)
                .splineTo(new Vector2d(24, -2), 0)
                .build();
        Trajectory trajNormal = drive.trajectoryBuilder(new Pose2d())
                .forward(5)
                .splineTo(new Vector2d(5, -2), 0)
                .splineTo(new Vector2d(27, -2), 0)
                .build();
        Trajectory trajMoveBack = drive.trajectoryBuilder(trajNormal.end().plus(new Pose2d(0, 03.5, Math.toRadians(-83))), false)
                .back(2)
                .build();
        Trajectory trajMoveBack2 = drive.trajectoryBuilder(trajNormal.end().plus(new Pose2d(-2, 03.5, 0)), false)
                .splineTo(trajNormal.end().vec().minus(new Vector2d(16, 5)), 0)
                .splineTo(trajNormal.end().vec().minus(new Vector2d(16, 30)), 0)
                .splineTo(trajNormal.end().vec().minus(new Vector2d(12, 30)), 0)
                .build();
        Trajectory trajMoveBack2Center = drive.trajectoryBuilder(traj.end().plus(new Pose2d(-2, 03.5, 0)), false)
                .splineTo(trajNormal.end().vec().minus(new Vector2d(16, 5)), 0)
                .splineTo(trajNormal.end().vec().minus(new Vector2d(16, 30)), 0)
                .splineTo(trajNormal.end().vec().minus(new Vector2d(12, 30)), 0)
                .build();
        Trajectory trajMoveBack2Blue = drive.trajectoryBuilder(trajNormal.end().plus(new Pose2d(-2, 03.5, 0)), false)
                .splineTo(trajNormal.end().vec().plus(new Vector2d(-16, 5)), 0)
                .splineTo(trajNormal.end().vec().plus(new Vector2d(-16, 30)), 0)
                .splineTo(trajNormal.end().vec().plus(new Vector2d(12, 30)), 0)
                .build();
        Trajectory trajMoveBack2CenterBlue = drive.trajectoryBuilder(traj.end().plus(new Pose2d(-2, 03.5, 0)), false)
                .splineTo(trajNormal.end().vec().plus(new Vector2d(-16, 5)), 0)
                .splineTo(trajNormal.end().vec().plus(new Vector2d(-16, 30)), 0)
                .splineTo(trajNormal.end().vec().plus(new Vector2d(12, 30)), 0)
                .build();



        waitForStart();
        //gripper.setPosition(1.0);
        if(isStopRequested()) return;

        gripper.setPosition(0.0);
        sleep(750);
        //wrist.setPosition(1);
        sleep(750);
        if(spike.equals(SPIKE_CENTER))drive.followTrajectory(traj);
        else {drive.followTrajectory(trajNormal);}
        if(spike.equals(SPIKE_LEFT))drive.turn(Math.toRadians(83));
        if(spike.equals(SPIKE_RIGHT)) {
            drive.turn(Math.toRadians(-83));
            drive.followTrajectory(trajMoveBack);
        }
        if(spike.equals(SPIKE_CENTER))
        {
            drive.turn(Math.toRadians(-12));
        }
        wrist.setPosition(0);
        sleep(1500);
        gripper.setPosition(0.5);
        sleep(500);
        wrist.setPosition(1);
        if(side.equals(SIDE_CLOSE) && alliance.equals(ALLIANCE_RED)) {
            sleep(1000);
            if (spike.equals(SPIKE_LEFT)) {
                drive.turn(Math.toRadians(-83));
                sleep(500);
                drive.followTrajectory(trajMoveBack2);
            }
            if (spike.equals(SPIKE_RIGHT)) {
                drive.turn(Math.toRadians(83));
                sleep(500);
                drive.followTrajectory(trajMoveBack2);
            }
            if (spike.equals(SPIKE_CENTER)) {
                drive.turn(Math.toRadians(12));
                sleep(500);
                drive.followTrajectory(trajMoveBack2Center);
            }
        } else if (side.equals(SIDE_CLOSE) && alliance.equals(ALLIANCE_BLUE)) {
            if (spike.equals(SPIKE_LEFT)) {
                drive.turn(Math.toRadians(-83));
                sleep(500);
                drive.followTrajectory(trajMoveBack2Blue);
            }
            if (spike.equals(SPIKE_RIGHT)) {
                drive.turn(Math.toRadians(83));
                sleep(500);
                drive.followTrajectory(trajMoveBack2Blue);
            }
            if (spike.equals(SPIKE_CENTER)) {
                drive.turn(Math.toRadians(12));
                sleep(500);
                drive.followTrajectory(trajMoveBack2CenterBlue);
            }
        }
        androidSoundPool.close();

        while(opModeIsActive())
        {
            telemetry.addData("active", "active");
            telemetry.addData("FLTarget:", frontLeft.getTargetPosition());
            telemetry.addData("BLTarget:", backLeft.getTargetPosition());
            telemetry.addData("FRTarget:", frontRight.getTargetPosition());
            telemetry.addData("BRTarget:", backRight.getTargetPosition());
            telemetry.addData("FLPos:", frontLeft.getCurrentPosition());
            telemetry.addData("BLPos:", backLeft.getCurrentPosition());
            telemetry.addData("FRPos:", frontRight.getCurrentPosition());
            telemetry.addData("BRPos:", backRight.getCurrentPosition());
            telemetry.update();
        }
    }

    public String[] autoSelector(){
        // Auto Selector
        String alliance = ALLIANCE_BLUE;
        String spike = SPIKE_LEFT;
        String side = SIDE_CLOSE;
        androidSoundPool.initialize(SoundPlayer.getInstance());


        while (!opModeIsActive() && !isStopRequested()) {

            teamElementDetection.setAlliance(alliance);
            int element_zone = teamElementDetection.elementDetection(telemetry);
            telemetry.addData("getMaxDistance", teamElementDetection.getMaxDistance());


            if (gamepad1.x) {
                alliance = ALLIANCE_BLUE;
            } else if (gamepad1.b) {
                alliance = ALLIANCE_RED;
            }
            telemetry.addData("Select Alliance (Gamepad1 X = Blue, Gamepad1 B = Red)", "");
            telemetry.addData("Current Alliance Selected : ", alliance.toUpperCase());
            telemetry.addData("", "");

            if (element_zone == 1) {
                spike = SPIKE_LEFT;
            } else if (element_zone == 3) {
                spike = SPIKE_RIGHT;
            } else {
                spike = SPIKE_CENTER;
            }

            if (gamepad1.y) {
                side = SIDE_FAR;
            } else if (gamepad1.a) {
                side = SIDE_CLOSE;
            }

            if (gamepad1.start) {
                if (spike.equals("left")) {
                    androidSoundPool.play("left.mp3");
                } else if (spike.equals("right")) {
                    androidSoundPool.play("right.mp3");
                } else {
                    androidSoundPool.play("center.mp3");
                }
                sleep(800);
            }
            telemetry.addData("Select Side (Gamepad1 Y = Far, Gamepad1 A = Close)", "");
            telemetry.addData("Current Side Selected : ", side.toUpperCase());
            telemetry.addData("Spike (Prop)", spike);
            telemetry.addData("Left Color", teamElementDetection.splitAveragePipeline.distance1);
            telemetry.addData("Center Color", teamElementDetection.splitAveragePipeline.distance2);
            telemetry.addData("Right Color", teamElementDetection.splitAveragePipeline.distance3);
            telemetry.addData("", "");

            telemetry.update();
        }

        return new String[] {alliance, spike, side};

    }

}
