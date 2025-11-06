package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class twowheelshooter extends OpMode {

    private DcMotor leftShooter;
    private DcMotor rightShooter;
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        leftShooter =  hardwareMap.get(DcMotor.class, "leftShooter");
        rightShooter =  hardwareMap.get(DcMotor.class, "rightShooter");

        leftShooter.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {
        telemetry.addData("Left RPM: ", leftShooter.getPower()*6000);
        telemetry.addData("Left Power: ", leftShooter.getPower());
        telemetry.addData("Right RPM: ", rightShooter.getPower()*6000);
        telemetry.addData("Right Power: ", rightShooter.getPower());
        leftShooter.setPower(0);
        rightShooter.setPower(0);
        if (gamepad1.a) {
            leftShooter.setPower(1);
            rightShooter.setPower(1);
        }
        leftShooter.setPower(gamepad1.right_trigger);
        rightShooter.setPower(gamepad1.right_trigger);
    }
}
