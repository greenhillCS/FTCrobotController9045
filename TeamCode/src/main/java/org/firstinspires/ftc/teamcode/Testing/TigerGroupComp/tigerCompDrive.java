package org.firstinspires.ftc.teamcode.Testing.TigerGroupComp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name = "tiger competition robot teleop")
public class tigerCompDrive extends OpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotorEx shooter;
    private Servo gate;
    private double shooterSpeed;
    private double testServo;
    private double shootertpr = 103.8;
    private double targetRPM = 1134;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        gate = hardwareMap.get(Servo.class, "gate");
    }

    @Override
    public void loop() {
        double tps = shooter.getVelocity();
        double currentRPM = (tps/shootertpr) * 60;
        telemetry.addData("rpm shooter", currentRPM);
        telemetry.addData("target rpm", (shooter.getVelocity()*shootertpr)/60);
        telemetry.addData("gate", testServo);
        telemetry.update();

        if (gamepad1.right_bumper){
            targetRPM++;
        }
        if(gamepad1.left_bumper){
            targetRPM--;
        }

        shooter.setVelocity((targetRPM * shootertpr) / 60);

        if (gamepad1.a) {
            gate.setPosition(0.3);
        }
        if (gamepad1.y) {
            gate.setPosition(0.05);
        }

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x * 1.1;
        double rx = gamepad1.right_stick_x;
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        frontLeft.setPower((y + x + rx) / denominator);
        backLeft.setPower((y - x + rx) / denominator);
        frontRight.setPower((y - x - rx) / denominator);
        backRight.setPower((y + x - rx) / denominator);
    }
}
