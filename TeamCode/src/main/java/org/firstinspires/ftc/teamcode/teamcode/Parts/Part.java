package org.firstinspires.ftc.teamcode.teamcode.Parts;

import com.qualcomm.robotcore.hardware.Gamepad;

public abstract class Part {
    public void init(){}
    public void loop(){}

    public abstract void loop(Gamepad gamepad1);
}
