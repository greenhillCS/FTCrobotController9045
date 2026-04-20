package org.firstinspires.ftc.teamcode.Tools.ATRunner;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.v.*;
import org.firstinspires.ftc.teamcode.Testing.AlianceColor.AlianceColorSyncTool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LauncherToolClass {
    //Define variables here
    STATE state = STATE.CLOSED;
    double tps = 0;
    private double distance = 0;
    private Map<String, Integer> ids;
    Limelight3A limelight;
    DcMotorEx launcher;
    DcMotor gate;
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;
    double testVelo;
    ElapsedTime runtime;
    public boolean launch = false;

    private double inToTPS(double in){
        return 185.16793*Math.pow(in, 0.351101);
    }
    public LauncherToolClass(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        testVelo = 0;

        launcher = hardwareMap.get(DcMotorEx.class,"launcher");// Port 2
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        gate = hardwareMap.get(DcMotor.class, "gate");
        gateClose();

        ids = new HashMap<>();
        ids.put("Red", 24);
        ids.put("Blue", 20);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0); // Switch to pipeline number

        runtime = new ElapsedTime();
    }

    public void update(){
        switch (state){
            case CLOSED:

                if(gamepad.rightBumperWasPressed()){
                    state = STATE.STOP;
                    break;
                }else if((gamepad.b || gamepad.a || launch) && runtime.seconds() >= 0.5){
                    runtime.reset();
                    state = STATE.OPEN;
                    break;
                }

                gateClose();

                LLResult result = limelight.getLatestResult();

                if (result != null && result.isValid()) {
                    List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
                    for (LLResultTypes.FiducialResult fiducial : fiducials) {
                        if(AlianceColorSyncTool.getSelectedColor().equals("None") || fiducial.getFiducialId() == ids.get(AlianceColorSyncTool.getSelectedColor())) {
                            Pose3D pose = fiducial.getTargetPoseCameraSpace();
                            distance = pose.getPosition().z * 39.3701;
                        }
                    }
                }

                tps = inToTPS(distance);

                launcher.setVelocity((int) tps);

                break;

            case OPEN:

                if(!gamepad.b && runtime.seconds() >= 0.75){
                    runtime.reset();
                    state = STATE.CLOSED;
                }

                gateOpen();

                launcher.setVelocity(tps);
                break;

            case STOP:

                if(gamepad.rightBumperWasPressed()){
                    state = STATE.CLOSED;
                }

                launcher.setVelocity(0);

                launch = false;

                break;
        }
        telemetry.addData("Launcher", state);
    }
    public void gateOpen(){gate.setPower(0.6);}
    public void gateClose(){gate.setPower(0);}
    public void setState(STATE s){
        state = s;
    }
    public STATE getState(){
        return state;
    }
    public void testUpdate(){

        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            for (LLResultTypes.FiducialResult fiducial : fiducials) {
                if(AlianceColorSyncTool.getSelectedColor().equals("None") || fiducial.getFiducialId() == ids.get(AlianceColorSyncTool.getSelectedColor())) {
                    Pose3D pose = fiducial.getTargetPoseCameraSpace();
                    distance = pose.getPosition().z * 39.3701;
                }
            }
        }

        if(gamepad.rightBumperWasPressed()){
            testVelo += 100;
        }else if(gamepad.leftBumperWasPressed()){
            testVelo -= 100;
        }else if(gamepad.dpadUpWasPressed()){
            testVelo += 10;
        }else if(gamepad.dpadDownWasPressed()){
            testVelo -= 10;
        }

        launcher.setVelocity(testVelo);

        telemetry.addData("Controls", "");
        telemetry.addData("+100", "Right Bumper");
        telemetry.addData("-100", "Left Bumper");
        telemetry.addData("+10", "Dpad Up");
        telemetry.addData("-10", "Dpad Down");
        telemetry.addData("", "-------------------- :");
        telemetry.addData("Distance (Inches)", distance);
        telemetry.addData("Velocity (Ticks per Second)", testVelo);
    }
}
