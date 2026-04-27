package org.firstinspires.ftc.teamcode.Testing.TigerGroupComp;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


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
    private double targetRPM = 1100;
    private Limelight3A limelight;
    private final double TURN_KP = 0.03;
    private ElapsedTime timer = new ElapsedTime();
    private int gateCount = 0;
    private boolean gateOpen = false;
    private boolean running = false;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        gate = hardwareMap.get(Servo.class, "gate");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    @Override
    public void loop() {
        double tps = shooter.getVelocity();
        double currentRPM = (tps / shootertpr) * 60;
        telemetry.addData("israel save us", "confirmed");
        telemetry.addData("rpm shooter", currentRPM);
        telemetry.addData("target rpm", targetRPM);
        telemetry.addData("gate", testServo);
        telemetry.update();

        if (gamepad1.right_bumper) {
            targetRPM++;
        }
        if (gamepad1.left_bumper) {
            targetRPM--;
        }

        shooter.setVelocity((targetRPM * shootertpr) / 60);

        if (gamepad1.right_trigger > 0.5 && !running) {
            gateCount = 0;
            gateOpen = false;
            running = true;
            gate.setPosition(0.3);
            timer.reset();
        }

        if (running) {
            if (!gateOpen && timer.milliseconds() >= 400) {
                // Open phase done → close
                gate.setPosition(0.05);
                gateOpen = true;
                timer.reset();

            } else if (gateOpen && timer.milliseconds() >= 600) {
                // Close phase done → next cycle
                gateCount++;
                gateOpen = false;

                if (gateCount < 3) {
                    gate.setPosition(0.3);
                    timer.reset();
                } else {
                    running = false; // All 3 cycles done
                }
            }
        }

        if (gamepad1.a) {
            gate.setPosition(0.3);
        }
        if (gamepad1.y) {
            gate.setPosition(0.05);
        }

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        frontLeft.setPower(-0.8 * ((y + x + rx) / denominator));
        backLeft.setPower(-0.8 * ((y - x + rx) / denominator));
        frontRight.setPower(0.8 * (y - x - rx) / denominator);
        backRight.setPower(0.8 * (y + x - rx) / denominator);
    }
}
