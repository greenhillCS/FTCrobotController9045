package org.firstinspires.ftc.teamcode.Testing.LauncherTest;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class KiranLaucherTest1 {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private DcMotor launcher;
    public KiranLaucherTest1(HardwareMap h, Telemetry t, Gamepad g) {
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        launcher = hardwareMap.get(DcMotor.class,"rightMotor");
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);
    }
    public void update(){
        launcher.setPower(1);


    }
}
