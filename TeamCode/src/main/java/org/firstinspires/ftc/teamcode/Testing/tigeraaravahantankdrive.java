package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "chiefer")
public class tigeraaravahantankdrive extends OpMode {

    DcMotor leftDrive;
    DcMotor rightDrive;
    Boolean povFlag = false;

    @Override
    public void init() {
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");
    }

    @Override
    public void loop() {
        if (gamepad1.a){
            povFlag = true;
        }
        if (gamepad1.b){
            povFlag = false;
        }

        if (!povFlag){
            leftDrive.setPower(gamepad1.left_stick_y);
            rightDrive.setPower(-gamepad1.right_stick_y);
        }

        if (povFlag) {
            leftDrive.setPower(gamepad1.left_stick_y);
            rightDrive.setPower(-gamepad1.left_stick_y);
            leftDrive.setPower(gamepad1.right_stick_x);
            rightDrive.setPower(gamepad1.right_stick_x);
        }
    }
}
