package org.firstinspires.ftc.teamcode.Tools;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TejDesignTools {
    //Define variables here
    enum STATE{
        IN,
        OUT,
        STOP
    }
    STATE state = STATE.STOP;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;
    DcMotor intake;
    DcMotorEx launcher;
    double maxLauncherPower = 1;
    double idleLauncherPower = 0.5;

    boolean launcherPressed = false;
    boolean previousButtonState = false;
    DcMotor turret;
    double maxTurretPower = 0.5;
    Servo hood;
    double hoodPosition = 0;
    Servo gate;
    double farTps = 900;
    double medTps = 750;
    double shortTps = 700;
    double tps = shortTps;

    public TejDesignTools(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        intake = hardwareMap.get(DcMotor.class,"intake");// Port 0
        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        launcher = hardwareMap.get(DcMotorEx.class,"launcher");// Port 2
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        turret = hardwareMap.get(DcMotor.class, "turret");// Port 1
//        turret.setDirection(DcMotorSimple.Direction.REVERSE);

        hood = hardwareMap.get(Servo.class, "hood");// Port 0
        hood.setPosition(hoodPosition);

        gate = hardwareMap.get(Servo.class, "gate");// Port 1
        closeGate();
    }
    private void updateIntake(){
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
    private void updateLauncher() {

        boolean currentButtonState = gamepad.right_bumper;

        if (currentButtonState && !previousButtonState) {

            launcherPressed = !launcherPressed;

        }

        previousButtonState = currentButtonState;

        if(gamepad.triangle){
            tps = farTps;
        }else if(gamepad.circle){
            tps = medTps;
        }else if(gamepad.cross){
            tps = shortTps;
        }


        if (launcherPressed) {
            launcher.setVelocity(tps);
        }
    }
    private void updateTurret(){
        turret.setPower(gamepad.left_stick_x * maxTurretPower);
    }
    private void updateHood(){
        hoodPosition += gamepad.left_stick_y/100;
        hood.setPosition(hoodPosition);

        telemetry.addData("Hood", "Position " + hoodPosition);
    }
    private void updateGate(){
        if(gamepad.right_bumper){
            openGate();
            telemetry.addData("Gate", "Launching");
        }else {
            closeGate();
            telemetry.addData("Gate", "Loading");
        }
    }
    public void openGate(){
        gate.setPosition(0);
    }
    public void closeGate(){
        gate.setPosition(1);
    }
    public void update(){
        //Logic that goes in the loop goes here
        updateIntake();

//        updateTurret();
//        updateHood();

        updateLauncher();
//        updateGate();
    }
}
