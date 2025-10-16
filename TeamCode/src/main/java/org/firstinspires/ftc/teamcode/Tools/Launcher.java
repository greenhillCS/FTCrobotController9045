package org.firstinspires.ftc.teamcode.Tools;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private DcMotor launcher;
    private Servo gate;
    private double maxPower = 1.0;
    public Launcher(HardwareMap h, Telemetry t, Gamepad g) {
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        launcher = hardwareMap.get(DcMotor.class,"launcher"); //Port 0
        gate = hardwareMap.get(Servo.class,"gate"); //Port 0
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);
        telemetry.addData("Launcher", "Initialized");
    }
    public void out(){
        launcher.setPower(maxPower);
    }
    public void stop(){
        launcher.setPower(0);
    }
    public void open(){
        gate.setPosition(1);
    }
    public void close(){
        gate.setPosition(0);
    }
    public void update(){
        gate.setPosition(Boolean.compare(gamepad.y, false));
        launcher.setPower(maxPower * (Boolean.compare(gamepad.right_bumper, true)-Boolean.compare(gamepad.left_bumper, true)));
    }
}
