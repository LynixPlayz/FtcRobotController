package org.firstinspires.ftc.teamcode.teamcode.Parts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.Servo;

public class Wrist extends Part{
    public Servo SELF = null;
    public String debugString = "";

    public final double wristUpPosition = 1.0;
    public final double wristDownPosition = -1.0;

    @Override
    public void init() {
        SELF = hardwareMap.get(Servo.class, "wrist");
    }

    @Override
    public void loop() {
        debugString = getDebugString();
    }

    public String getDebugString()
    {
        String returnString = "";
        returnString += "wrist " + SELF.getPosition() + "";
        return returnString;
    }

    public void gamepadMovement()
    {
        if(gamepad1.dpad_up)
        {
            SELF.setPosition(wristUpPosition);
        } else if (gamepad1.dpad_down) {
            SELF.setPosition(wristDownPosition);
        }
    }
}
