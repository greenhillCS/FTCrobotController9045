package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class wormGearNew {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor worm_Gear;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;

    public wormGearNew(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        worm_Gear = hardwareMap.get(DcMotor.class, "wormGear");
    }

    public void update(){
        worm_Gear.setPower(gamepad.right_stick_y);
    }
}
