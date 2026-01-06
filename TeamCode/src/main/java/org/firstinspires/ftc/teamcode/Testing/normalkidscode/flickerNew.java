package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class flickerNew {

    private ElapsedTime runtime = new ElapsedTime();
    private Servo flicker;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;


    public flickerNew(HardwareMap h, Telemetry t, Gamepad g) {
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        flicker = hardwareMap.get(Servo.class, "flicker");
        flicker.setPosition(0.5);

    }

    public void update() {
        if (gamepad.y) {
            flicker.setPosition(1);
        }
        if (gamepad.x) {
            flicker.setPosition(0);
        }
    }
}
