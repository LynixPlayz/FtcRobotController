package org.firstinspires.ftc.teamcode.teamcode.Parts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.Servo;

public class Gripper extends Part{
    private Servo gripper = null;

    @Override
    public void init() {
        gripper = hardwareMap.get(Servo.class, "gripper");
    }

    public void gripperMovementGamepad()
    {
        if (gamepad1.left_bumper || gamepad1.right_bumper) {
            double gripperOpenPosition = 0;
            gripper.setPosition(gripperOpenPosition);
        }
        else {
            double gripperClosedPosition = 1.0;
            gripper.setPosition(gripperClosedPosition);
        }
    }
}
