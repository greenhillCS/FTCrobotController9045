package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

public class shooter extends OpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor motorLeft;
    private DcMotor motorRight;



    @Override
    public void init() {
        motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
        motorRight = hardwareMap.get(DcMotor.class, "motorRight");
        motorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }


    @Override
    public void loop() {
        if (gamepad1.a)  {
            motorLeft.setPower(1);
            motorRight.setPower(1);
        }
        motorLeft.setPower(0);
        motorRight.setPower(0);

    }
}
