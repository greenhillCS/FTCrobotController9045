package org.firstinspires.ftc.teamcode.Tools;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {
    enum STATE {
        IN,
        OUT,
        STOP
    }
    private STATE mode = STATE.STOP;
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
    public void in(){
        intake.setPower(maxPower);
    }
    public void stop(){
        intake.setPower(0);
    }
    public void update(){
        //Use dpad_up to take in artifacts continuously
        //Use dpad_down to expel artifacts continuously
        //Use the leftmost button (square or x) to stop the intake

        switch (mode){
            case IN:
                if(gamepad.dpad_down){
                    mode = STATE.OUT;
                }else if(gamepad.y){
                    mode = STATE.STOP;
                }
                intake.setPower(maxPower);
                break;
            case OUT:
                if(gamepad.dpad_up){
                    mode = STATE.IN;
                }else if(gamepad.y){
                    mode = STATE.STOP;
                }
                intake.setPower(-maxPower);
                break;
            case STOP:
                if(gamepad.dpad_up){
                    mode = STATE.IN;
                }else if(gamepad.dpad_down){
                    mode = STATE.OUT;
                }
                intake.setPower(0);
                break;
        }
    }
}
