package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class worm_Gear extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor worm_Gear;


    @Override
    public void init() {
        worm_Gear = hardwareMap.get(DcMotor.class, "worm_Gear")
    }

    @Override
    public void loop() {
        worm_Gear.setPower(gamepad1.right_stick_y);

    }
}
