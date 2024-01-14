package org.firstinspires.ftc.teamcode.teamcode.Autonomous;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@Autonomous(name = "RoadrunnerTesting")
public class RoadrunnerTesting extends LinearOpMode {
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Trajectory traj = drive.trajectoryBuilder(new Pose2d())
                .forward(5)
                .splineTo(new Vector2d(5, -2), 0)
                .splineTo(new Vector2d(24, -2), 0)
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectory(traj);
        drive.turn(Math.toRadians(90));
    }
}
