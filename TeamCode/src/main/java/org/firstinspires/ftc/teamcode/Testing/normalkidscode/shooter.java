package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

public class shooter extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor shooterLeft;
    private DcMotor shooterRight;



    @Override
    public void init() {
        shooterLeft = hardwareMap.get(DcMotor.class, "shooterLeft");
        shooterRight = hardwareMap.get(DcMotor.class, "shooterRight");
        shooterLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }


    @Override
    public void loop() {
        if (gamepad1.a)  {
            shooterLeft.setPower(1);
            shooterRight.setPower(1);
        }
        shooterLeft.setPower(0);
        shooterRight.setPower(0);

    }
}
