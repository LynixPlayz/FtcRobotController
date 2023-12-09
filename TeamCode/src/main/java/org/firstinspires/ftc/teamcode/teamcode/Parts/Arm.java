package org.firstinspires.ftc.teamcode.teamcode.Parts;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.teamcode.Autonomous.MoveDirection;

public class Arm extends Part {
    private final int armHomePosition = 20;

    public String debugString = "";
    private boolean manualMode = false;
    public DcMotorEx arm = null;
    public HardwareMap hardwareMap;

    //SET DURING RUNTIME
    double armLastTickPower = 0;
    double armLastTickPosition = 0;
    boolean isBraking;

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
        returnString += ("test " + ((armLastTickPosition - arm.getCurrentPosition()) / Math.abs(armLastTickPosition - arm.getCurrentPosition())));

        return returnString;
    }

    public void moveToHomePos()
    {
        arm.setTargetPosition(armHomePosition);
        arm.setPower(0.5);
        arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
    }

    /*public void armTriggersGamepad(Gamepad gamepad1)
    {
        boolean ignoreBraking = false;
        double manualArmPower;
        manualArmPower = gamepad1.right_trigger - gamepad1.left_trigger;
        double armManualDeadband = 0.15;
        if (Math.abs(manualArmPower) > armManualDeadband) {
            if (!manualMode) {
                //armLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                arm.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                manualMode = true;
            }
            //armLeft.setPower(manualArmPower);
            arm.setPower(manualArmPower * 0.6);
            arm.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        }
        if(Math.abs(manualArmPower) != 0) {ignoreBraking = true;}
        armLastTickPosition = arm.getCurrentPosition();
        armLastTickPower = arm.getPower();
        double armPositionChange = arm.getCurrentPosition() - armLastTickPosition;
        MoveDirection fallDirection = MoveDirection.LEFT;
        if(armLastTickPosition != arm.getCurrentPosition())
        {
            if(armPositionChange > 0){
                fallDirection = MoveDirection.FORWARD;
            } else if (armPositionChange < 0) {
                fallDirection = MoveDirection.BACK;
            }
        }
        if(fallDirection != MoveDirection.LEFT && !ignoreBraking)
        {
            arm.setPower(-(armPositionChange / 10));
        }
    }*/

    public void armTriggersGamepad(Gamepad gamepad1)
    {
        double manualArmDeadband = 0.1;
        boolean didSetPosition = false;
        int positionMax = -30;
        int positionMin = -540;
        //boolean isPositionNegative = false;
        //arm.setTargetPosition(arm.getTargetPosition());
        //if (arm.getCurrentPosition() > arm.getTargetPosition() - 1.5 && arm.getCurrentPosition() < arm.getTargetPosition() + 1.5) {
            if(gamepad1.left_trigger > manualArmDeadband) {
                arm.setTargetPosition(arm.getTargetPosition() + 4);
                didSetPosition = true;
            }
            if (gamepad1.right_trigger > manualArmDeadband) {
                arm.setTargetPosition(arm.getTargetPosition() - 4);
                didSetPosition = true;
            }
        //}
        if(arm.getTargetPosition() > positionMax)
        {
            arm.setTargetPosition(positionMax);
            didSetPosition = true;
        }
        if(arm.getTargetPosition() < positionMin)
        {
            arm.setTargetPosition(positionMin);
            didSetPosition = true;
        }
        if(didSetPosition) arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        arm.setPower(1.6);
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
