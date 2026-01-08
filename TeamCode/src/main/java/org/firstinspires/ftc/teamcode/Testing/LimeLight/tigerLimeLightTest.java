package org.firstinspires.ftc.teamcode.Testing.LimeLight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;


@TeleOp(name = "limeLightTest industry baby")
public class tigerLimeLightTest extends LinearOpMode {

    private Limelight3A limelight;
    public DcMotor turretMotor;
    public double error = 0;
    public double speed = 0.5;
    public double fov = 54.5;

    @Override
    public void runOpMode() throws InterruptedException
    {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        turretMotor = hardwareMap.get(DcMotor.class, "turret");


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
                turretMotor.setPower(speed*(error/fov));
                //right now the speed scale is set to 0.5


                //add magnetic swith functionality

            }
        }
    }
}
