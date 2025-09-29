package org.firstinspires.ftc.teamcode.Testing.LifterTest;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LifterToolClass {
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;
    DcMotor lifterRight;
    DcMotor lifterLeft;
    double speed = 0.5;
    public LifterToolClass(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        lifterLeft = hardwareMap.get(DcMotor.class, "leftLifter");
        lifterLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        lifterRight = hardwareMap.get(DcMotor.class, "rightLifter");
        lifterRight.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetry.addData("Lifter", "Initialized");
    }
    public void update(){
        lifterRight.setPower(speed * (gamepad.right_trigger-gamepad.left_trigger));
        lifterLeft.setPower(speed * (gamepad.right_trigger-gamepad.left_trigger));
    }
}
