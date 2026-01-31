package org.firstinspires.ftc.teamcode.Testing.LimeLight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Testing.AlianceColor.AlianceColorSyncTool;
import org.firstinspires.ftc.teamcode.Tools.Intake;

import java.util.List;


public class TurretLimelight{
    //Define variables here
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;
    public enum STATE {
        SCANNING,
        FOUND,
        SWITCH
    }
    STATE state = STATE.SCANNING;
    private Limelight3A limelight;
    public DcMotor turretMotor;
    public double error = 1;
    public double speed = 1;
    public double fov = 54.5;
    private double searchPower = 0.25;
    private double manualPower = 0.5;
    private double dWait = 0.2;
    private boolean searching = false;
    private DigitalChannel magnet;
    private int id = 0;
    private int invalidNum = 0;
    private ElapsedTime runtime = new ElapsedTime();
    boolean correct = true;
    public TurretLimelight(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        turretMotor = hardwareMap.get(DcMotor.class, "turret");
        magnet = hardwareMap.get(DigitalChannel.class, "magnet");
        magnet.setMode(DigitalChannel.Mode.INPUT);


        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(0);

        /*
         * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
         */
        limelight.start();

        telemetry.addData(">", "Robot Ready.  Press Play.");
    }
    public void updateID(){
        switch (AlianceColorSyncTool.getSelectedColor()){
            case "Blue":
                id = 20;
                break;
            case "Red":
                id = 24;
                break;
            case "None":
                break;
        }
    }
    public void update(){
        if(-0.05 > gamepad.left_stick_x || gamepad.left_stick_x > 0.05){
            telemetry.addData("STATE", "manual");
            turretMotor.setPower(Math.abs(manualPower)*-gamepad.left_stick_x);
        }else {
            LLStatus status = limelight.getStatus();
            telemetry.addData("Name", "%s",
                    status.getName());
            telemetry.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
                    status.getTemp(), status.getCpu(), (int) status.getFps());
            telemetry.addData("Pipeline", "Index: %d, Type: %s",
                    status.getPipelineIndex(), status.getPipelineType());

            LLResult result = limelight.getLatestResult();
            // Access april tag results


            if (result.isValid() && id != 0) {
                correct = false;
                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                for (LLResultTypes.FiducialResult fr : fiducialResults) {
                    telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees());
                    if (fr.getFiducialId() == id) {
                        correct = true;
                    }
                }
            }


            switch (state) {
                case FOUND:

                    telemetry.addData("STATE", "Found");
                    // Access general information
                    if (!result.isValid() || !correct) {
                        state = STATE.SCANNING;
                        break;
                    }

                    Pose3D botpose = result.getBotpose();
                    Position pos = botpose.getPosition();
                    telemetry.addData("Position", "X: " + pos.x + " Y: " + pos.y);

                    //54.5 degree fov
                    error = result.getTx();

                    if (!magnet.getState() && runtime.seconds() > dWait) {
                        runtime.reset();
                        searchPower *= -1;
                        state = STATE.SWITCH;
                        break;
                    }

                    double power = -(error / fov);
                    searchPower = (Math.abs(power) / power) * Math.abs(searchPower);
                    if (-5 < error && error < 5) {
                        power = 0;
                    }
                    turretMotor.setPower(power);
                    //right now the speed scale is set to 0.5


                    //add magnetic swith functionality

                    break;
                case SWITCH:
                    telemetry.addData("STATE", "Searching");
                    if (!magnet.getState() && runtime.seconds() > dWait) {
                        state = STATE.SCANNING;
                        break;
                    }
                    turretMotor.setPower(searchPower);
                    break;
                case SCANNING:
                    telemetry.addData("STATE", "Scanning");
                    if (result.isValid() && correct) {
                        state = STATE.FOUND;
                        break;
                    } else if (!magnet.getState() && runtime.seconds() > dWait) {
                        runtime.reset();
                        searchPower *= -1;
                    }
                    turretMotor.setPower(searchPower);
                    break;
            }

            telemetry.addData("Power", searchPower);
            telemetry.addData("magnet", magnet.getState());
            telemetry.addData("isValid", result.isValid());
            telemetry.addData("correct", correct);
            telemetry.addData("runtime", runtime.seconds());
        }
    }
}
