package org.firstinspires.ftc.teamcode.Testing.LimeLight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;


@TeleOp(name = "limeLightTest industry baby")
public class tigerLimeLightTest extends LinearOpMode {

    private Limelight3A limelight;
    private DcMotor turretMotor;
    private double error = 0;
    private double speed = 0.5;
    private double fov = 54.5;
    private double searchPower = 1;
    private boolean searching = false;
    private DigitalChannel magnet;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException
    {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        turretMotor = hardwareMap.get(DcMotor.class, "turret");
        magnet = hardwareMap.get(DigitalChannel.class, "magnet");
        magnet.setMode(DigitalChannel.Mode.INPUT);


        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(0);

        /*
         * Starts polling for data.
         */
        limelight.start();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if (result != null) {
                if (result.isValid()) {
                    Pose3D botpose = result.getBotpose();
                    telemetry.addData("tx", result.getTx());
                    telemetry.addData("ty", result.getTy());
                    telemetry.addData("Botpose", botpose.toString());
                }
                //54.5 degree fov
                error = (fov/2)-result.getTx();
                // if the qr code is left of center, so tx is less than 340, need to turn left
                if(magnet.getState() && runtime.seconds() >= 0.1) {
                    runtime.reset();
                    searchPower *= -1;
                    if(searching){
                        searching = false;
                        turretMotor.setPower(0);
                    }

                }
                if(searching){
                    turretMotor.setPower(searchPower);
                }else {
                    double power = speed * (error / fov);
                    turretMotor.setPower(power);
                    searchPower = Math.abs(power)/power;
                }
                //right now the speed scale is set to 0.5


                //add magnetic swith functionality

            } else{
                if(magnet.getState()) {
                    searchPower *= -1;
                }
                turretMotor.setPower(searchPower);
            }
        }
    }
}
