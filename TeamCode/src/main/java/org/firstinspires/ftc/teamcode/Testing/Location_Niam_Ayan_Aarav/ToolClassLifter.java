package org.firstinspires.ftc.teamcode.Testing.Location_Niam_Ayan_Aarav;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ToolClassLifter {


    public class LifterToolClass {
        HardwareMap hardwareMap;
        Telemetry telemetry;
        Gamepad gamepad;
        DcMotor wormLifter;

        double speed = 0.5;

        public LifterToolClass(HardwareMap h, Telemetry t, Gamepad g) {
            hardwareMap = h;
            telemetry = t;
            gamepad = g;


            wormLifter = hardwareMap.get(DcMotor.class, "rightLifter");
            wormLifter.setDirection(DcMotorSimple.Direction.FORWARD);
            telemetry.addData("Lifter", "Initialized");
        }

        public void update() {
            wormLifter.setPower(speed * (gamepad.right_trigger - gamepad.left_trigger));
        }
    }
}