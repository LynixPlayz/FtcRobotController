package org.firstinspires.ftc.teamcode.teamcode.Parts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Arm extends Part {
    private final int armHomePosition = 20;

    public String debugString = "";
    private boolean manualMode = false;
    public DcMotorEx arm = null;
    public HardwareMap hardwareMap;

    @Override
    public void init()
    {
        arm = hardwareMap.get(DcMotorEx.class, "arm");

        arm.setDirection(DcMotorEx.Direction.FORWARD);
        arm.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        arm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        arm.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        arm.setPower(0.0);
        arm.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void loop(Gamepad gamepad1) {
        debugString = getDebugString();
    }

    public String getDebugString()
    {
        String returnString = "";

        returnString += "rightArmTarget " + arm.getTargetPosition() + "\n";
        returnString += "rightArmMotor " + arm.getCurrentPosition() + "\n";
        returnString += "rightArmPower " + arm.getPower() + "\n";

        return returnString;
    }

    public void moveToHomePos()
    {
        arm.setTargetPosition(armHomePosition);
        arm.setPower(0.5);
        arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    public void armTriggersGamepad(Gamepad gamepad1)
    {
        double manualArmPower;
        manualArmPower = gamepad1.right_trigger - gamepad1.left_trigger;
        double armManualDeadband = 0.05;
        if (Math.abs(manualArmPower) > armManualDeadband) {
            if (!manualMode) {
                //armLeft.setPower(0.0);
                arm.setPower(0.0);
                //armLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                arm.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                manualMode = true;
            }
            //armLeft.setPower(manualArmPower);
            arm.setPower(manualArmPower);
            arm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        }
    }

    public void lockArm()
    {
        //armLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        arm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        //armLeft.setPower(0);
        arm.setPower(0);
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void scoringPosition()
    {
        int armScorePosition = -600;

        arm.setTargetPosition(armScorePosition);
        arm.setPower(0.5);
        arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }


}
