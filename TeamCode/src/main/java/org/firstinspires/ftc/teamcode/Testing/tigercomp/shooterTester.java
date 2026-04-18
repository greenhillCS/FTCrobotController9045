package org.firstinspires.ftc.teamcode.Testing.tigercomp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "tiger shooter tester 4141")
public class shooterTester extends OpMode {
    private DcMotor shooter;

    @Override
    public void init() {
        shooter = hardwareMap.get(DcMotor.class, "shooter");
    }

    @Override
    public void loop() {
        if (gamepad1.a){
            shooter.setPower(1);
        }
        if (gamepad1.b){
            shooter.setPower(0);
        }
        if (gamepad1.x){
            shooter.setPower(-1);
        }
    }
}
