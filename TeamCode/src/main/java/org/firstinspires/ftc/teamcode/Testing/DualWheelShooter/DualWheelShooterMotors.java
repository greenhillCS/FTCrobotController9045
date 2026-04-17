package org.firstinspires.ftc.teamcode.Testing.DualWheelShooter;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;


import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DualWheelShooterMotors {
    //Define variables here
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;
    private DcMotor leftWheel;
    private DcMotor rightWheel;


    public DualWheelShooterMotors(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        leftWheel = hardwareMap.get(DcMotor.class, "leftWheelie");
        rightWheel = hardwareMap.get(DcMotor.class, "rightWheel");

        leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftWheel.setDirection(DcMotor.Direction.FORWARD);
        rightWheel.setDirection(DcMotor.Direction.REVERSE);


    }

    public void update(){
        //Logic that goes in the loop goes here

            // Gamepad
        if (gamepad.x) {
            leftWheel.setPower(1);
            rightWheel.setPower(1);
        }
        else {
            leftWheel.setPower(0);
            rightWheel.setPower(0);
        }

    }
}

