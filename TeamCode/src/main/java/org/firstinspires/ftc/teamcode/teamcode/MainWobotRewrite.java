package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Arm;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Gripper;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Movement;
import org.firstinspires.ftc.teamcode.teamcode.Parts.Wrist;

@TeleOp(name="MianWobotRewrite", group="Iterative Opmode")
public class MainWobotRewrite extends LinearOpMode {
    Movement movement = new Movement();
    Arm arm = new Arm();
    Gripper gripper = new Gripper();
    Wrist wrist = new Wrist();


    @Override
    public void runOpMode() {
        movement.hardwareMap = hardwareMap;
        arm.hardwareMap = hardwareMap;
        gripper.hardwareMap = hardwareMap;
        wrist.hardwareMap = hardwareMap;

        movement.init();
        gripper.init();
        arm.init();


        waitForStart();

        while(opModeIsActive())
        {
            movement.loop();
            wrist.loop();
            movement.processJoystickInputToMovement(gamepad1);
            arm.armTriggersGamepad(gamepad1);
            gripper.gripperMovementGamepad(gamepad1);
            wrist.gamepadMovement(gamepad1);
            processGamepadHomePos();
            lockArmProcessGamepad();
            processGamepadScoringPos();
            telemetryUpdate();
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

        return returnString;
    }

    public String getMotorsDebugInfo()
    {
        String returnString = "";

        returnString += movement.debugInfo;
        returnString += arm.debugString;

        return returnString;
    }

    public void processGamepadHomePos()
    {
        if(gamepad1.x)
        {
            arm.moveToHomePos();
            //wrist.SELF.setPosition(wrist.wristUpPosition);
        }
    }

    public void lockArmProcessGamepad()
    {
        if(gamepad1.a)
        {
            arm.lockArm();
        }
    }

    public void processGamepadScoringPos()
    {
        if (gamepad1.y) {
            arm.scoringPosition();
            //wrist.SELF.setPosition(wrist.wristDownPosition);
        }
    }
}
