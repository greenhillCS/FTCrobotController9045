package org.firstinspires.ftc.teamcode.Testing.Sorter;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class SorterRotateToolClass {

    private ElapsedTime runtime = new ElapsedTime();
    private Servo sorterServo;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;


    public SorterRotateToolClass(HardwareMap h, Telemetry t, Gamepad g) {
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        sorterServo = hardwareMap.get(Servo.class, "sorterServo");
        sorterServo.setPosition(0.5);

    }

    public void update() {
        if (gamepad.y) {
            sorterServo.setPosition(1);
        }
        if (gamepad.x) {
            sorterServo.setPosition(0);
        }
    }
}
