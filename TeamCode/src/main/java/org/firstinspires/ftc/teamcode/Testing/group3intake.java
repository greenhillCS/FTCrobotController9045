package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class group3intake extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotor intakeMotor = hardwareMap.get(DcMotor.class, "intake");

        waitForStart();
        while (opModeIsActive()){
            intakeMotor.setPower(1.0);
            if (gamepad1.a){
                intakeMotor.setPower(0);
            }
        }
    }
}
