package org.firstinspires.ftc.teamcode.Tools;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ManualTools {
    private DcMotor worm_Gear;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private Servo flicker;
    private DcMotor shooterLeft;
    private DcMotor shooterRight;


    public ManualTools(HardwareMap h, Telemetry t, Gamepad g) {
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        
        worm_Gear = hardwareMap.get(DcMotor.class, "worm_Gear");
        
        flicker = hardwareMap.get(Servo.class, "flicker");
        flicker.setPosition(0.5);
        
        shooterLeft = hardwareMap.get(DcMotor.class, "shooterLeft");
        shooterRight = hardwareMap.get(DcMotor.class, "shooterRight");
        shooterLeft.setDirection(DcMotorSimple.Direction.REVERSE);

    }
    public void update(){
        worm_Gear.setPower(gamepad.right_stick_y);
        
        if (gamepad.y) {
            flicker.setPosition(1);
        }
        else {
            flicker.setPosition(0);
        }
        
        
        if (gamepad.x)  {
            shooterLeft.setPower(1);
            shooterRight.setPower(1);
        }
        else {
            shooterLeft.setPower(0);
            shooterRight.setPower(0);
        }
        
        
        
    }
    
}
