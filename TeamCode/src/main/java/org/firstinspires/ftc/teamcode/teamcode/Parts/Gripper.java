package org.firstinspires.ftc.teamcode.teamcode.Parts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Gripper extends Part{
    private Servo gripper = null;
    public HardwareMap hardwareMap;

    @Override
    public void init() {
        gripper = hardwareMap.get(Servo.class, "gripper");
    }

    @Override
    public void loop(Gamepad gamepad1) {

    }

    public void gripperMovementGamepad(Gamepad gamepad1)
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
