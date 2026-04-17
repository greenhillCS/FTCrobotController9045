package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "tiger arm test")
public class tigerarmtest extends OpMode {
    Servo base;
    Servo turret;
    @Override
    public void init() {
        base = hardwareMap.get(Servo.class, "base");
        turret = hardwareMap.get(Servo.class, "turret");
        base.setPosition(0.5);
        turret.setPosition(0.5);
    }

    @Override
    public void loop() {
        base.setPosition(base.getPosition()+gamepad1.left_stick_x*0.005);
        turret.setPosition(turret.getPosition()+gamepad1.right_stick_y*0.005);
    }
}
