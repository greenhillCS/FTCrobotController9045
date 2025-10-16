package org.firstinspires.ftc.teamcode.Tools;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private DcMotor intake;
    private double maxPower = 1.0;
    public Intake(HardwareMap h, Telemetry t, Gamepad g) {
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        intake = hardwareMap.get(DcMotor.class,"intake"); //Port 1
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        telemetry.addData("Intake", "Initialized");
    }
    public void update(){
        intake.setPower(maxPower * (gamepad.right_trigger-gamepad.left_trigger));
    }
}
