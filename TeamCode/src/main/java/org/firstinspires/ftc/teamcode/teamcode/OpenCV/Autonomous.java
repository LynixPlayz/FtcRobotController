package org.firstinspires.ftc.teamcode.teamcode.OpenCV;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.teamcode.Autonomous.Calbration;
import org.firstinspires.ftc.teamcode.teamcode.OpenCV.TeamElementSubsystem;
import org.firstinspires.ftc.robotcore.external.android.AndroidSoundPool;


@com.qualcomm.robotcore.eventloop.opmode.TeleOp(group = "StateTest")
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

        waitForStart();
        driveForward(-48, 1);
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
