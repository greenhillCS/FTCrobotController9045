package org.firstinspires.ftc.teamcode.Tools.ATRunner;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.v.*;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class IntakeToolClass {
    //Define variables here
    v.STATE state = v.STATE.STOP;
    DcMotor intake;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;

    public IntakeToolClass(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        intake = hardwareMap.get(DcMotor.class,"intake");// Port 0
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
    }
    public void update(){
        switch (state){
            case IN:
                telemetry.addData("Intake", "In");

                intake.setPower(1);

                if(gamepad.dpad_left){
                    state = STATE.STOP;
                }else if(gamepad.dpad_down){
                    state = STATE.OUT;
                }
                break;
            case OUT:
                telemetry.addData("Intake", "Out");

                intake.setPower(-1);

                if(gamepad.dpad_left){
                    state = STATE.STOP;
                }else if(gamepad.dpad_up){
                    state = STATE.IN;
                }
                break;
            case STOP:
                telemetry.addData("Intake", "Stop");

                intake.setPower(0);

                if(gamepad.dpad_up){
                    state = STATE.IN;
                }else if(gamepad.dpad_down){
                    state = STATE.OUT;
                }
                break;
        }
    }
    public void setState(STATE s){
        state = s;
    }
    public STATE getState(){
        return state;
    }
}
