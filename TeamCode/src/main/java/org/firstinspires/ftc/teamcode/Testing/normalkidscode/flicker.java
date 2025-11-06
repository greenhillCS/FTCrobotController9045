package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class flicker extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private Servo flicker;


    @Override
    public void init() {
        flicker = hardwareMap.get(Servo.class, "flicker");
        flicker.setPosition(0.5);

    }

    @Override
    public void loop() {
        if (gamepad1.y) {
            flicker.setPosition(1);
        }
        if (gamepad1.x) {
            flicker.setPosition(0);
        }
    }
}
