package org.firstinspires.ftc.teamcode.Testing.JohnnyThings;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

public class LauncherTPS {
    //Define variables here
    //Ticks per rotation 6000 rpm motor: 28
    //Max velocity 6000: (6000/60) * 28 = 2800 TPS
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;

    private Limelight3A limelight;
    private DcMotorEx launcher;
    private double testVelo = 0;
    private double maxVelo = 2800;
    private boolean pressed = false;

    public double error = 0;
    public double speed = 1;
    public double fov = 54.5;

    private double goodTPS = 1125;


    public LauncherTPS(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }
    public void testUpdate(){
        LLStatus status = limelight.getStatus();
        telemetry.addData("Name", "%s",
                status.getName());
        telemetry.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
                status.getTemp(), status.getCpu(),(int)status.getFps());
        telemetry.addData("Pipeline", "Index: %d, Type: %s",
                status.getPipelineIndex(), status.getPipelineType());
        LLResult result = limelight.getLatestResult();

        if (result.isValid()) {
            // Access general information
            Pose3D botpose = result.getBotpose();

            // Access april tag results
//            List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
//            for (LLResultTypes.FiducialResult fr : fiducialResults) {
//                telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetPoseCameraSpace(), fr.getTargetYDegrees());
//            }
            telemetry.addData("Distance", result.getBotposeAvgDist());// in meters


            //54.5 degree fov
            // if the qr code is left of center, so tx is less than 340, need to turn left
            //right now the speed scale is set to 0.5

        } else {
            telemetry.addData("Limelight", "No data available");
        }

        if (gamepad.rightBumperWasPressed()){
            testVelo += 100;
        } else if (gamepad.leftBumperWasPressed()) {
            testVelo -= 100;
        }
//        if(gamepad.right_trigger>0 && !pressed){
//            pressed = true;
//            testVelo += 1000;
//        }else if(gamepad.left_trigger>0 && !pressed){
//            pressed = true;
//            testVelo -= 1000;
//        }else if(gamepad.right_bumper && !pressed){
//            pressed = true;
//            testVelo += 100;
//        }else if(gamepad.left_bumper && !pressed){
//            pressed = true;
//            testVelo -= 100;
//        }else if(pressed) {
//            pressed = false;
//        }

        if(testVelo > maxVelo) {
            testVelo = maxVelo;
        }else if(testVelo < 0){
            testVelo = 0;
        }
        telemetry.addData("Controls", "right trigger: +1000, left trigger: -1000, right bumper: +100, left bumper: -100");
        telemetry.addData("Current Velocity", testVelo);
        launcher.setVelocity(testVelo);
    }
    public void update(){
        //Logic that goes in the loop goes here
    }
}
