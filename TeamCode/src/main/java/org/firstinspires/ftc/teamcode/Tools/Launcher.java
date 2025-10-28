package org.firstinspires.ftc.teamcode.Tools;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {
    enum STATE {
        IN,
        OUT,
        STOP
    }
    private STATE mode = STATE.STOP;
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
        gate = hardwareMap.get(Servo.class,"gate"); //Port 0 on Control hub
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
        //Use the right button on the bottom of the controller to launch artifacts
        //Use the left button on the bottom of the controller to intake artifacts
        //Use the top button (Y or triangle) to open the gate when it is launching
        //The gate will stay open when taking in artifacts

        switch (mode){
            case IN:
                if(gamepad.left_trigger > 0){
                    mode = STATE.OUT;
                }else if(gamepad.right_trigger == 0){
                    mode = STATE.STOP;
                }

                gate.setPosition(Boolean.compare(gamepad.y, false));
                launcher.setPower(maxPower);
                break;
            case OUT:
                if(gamepad.right_trigger > 0){
                    mode = STATE.IN;
                }else if(gamepad.left_trigger == 0){
                    mode = STATE.STOP;
                }

                open();
                launcher.setPower(-maxPower);
                break;
            case STOP:
                if(gamepad.right_trigger > 0){
                    mode = STATE.IN;
                }else if(gamepad.left_trigger > 0){
                    mode = STATE.OUT;
                }

                gate.setPosition(Boolean.compare(gamepad.y, false));
                launcher.setPower(0);
                break;
        }
    }
}
