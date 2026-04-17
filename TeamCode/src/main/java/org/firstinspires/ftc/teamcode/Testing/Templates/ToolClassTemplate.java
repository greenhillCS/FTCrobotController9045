package org.firstinspires.ftc.teamcode.Testing.Templates;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ToolClassTemplate {
    //Define variables here
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;

    public ToolClassTemplate(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
    }
    public void update(){
        //Logic that goes in the loop goes here
    }
}
