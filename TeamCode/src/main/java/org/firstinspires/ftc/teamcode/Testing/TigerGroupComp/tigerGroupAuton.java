package org.firstinspires.ftc.teamcode.Testing.TigerGroupComp;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;


@Autonomous(name = "tiger competition robot auton")
public class tigerGroupAuton extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotorEx shooter;
    private Servo gate;
    private double shooterSpeed;
    private double testServo;
    private double shootertpr = 103.8;
    private double targetRPM = 1050;
    private Limelight3A limelight;
    private final double TURN_KP = 0.03;
    private final double DRIVE_POWER    = 0.5;
    private final long   DRIVE_FWD_MS   = 2000;  // drive forward to shooting position
    private final long   DRIVE_BACK_MS  = 2500;  // drive back to reload wall (match DRIVE_FWD_MS)
    private final long   SPINUP_MS      = 1500;  // flywheel spin-up time before first shot
    private final long   RESPINUP_MS    = 800;   // spin-up after returning (already warm)
    private final long   GATE_OPEN_MS   = 400;   // how long gate stays open per ball
    private final long   GATE_CLOSE_MS  = 600;   // delay between shots (ball loads into shooter)
    private final long   RELOAD_WAIT_MS = 6000;  // time parked at wall for human player to load

    // ── Servo positions (match your teleop) ───────────────────────────
    private final double GATE_OPEN   = 0.3;
    private final double GATE_CLOSED = 0.05;

    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        gate = hardwareMap.get(Servo.class, "gate");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();
        if (!opModeIsActive()) return;
        telemetry.addData("Phase", "Driving to shoot position");
        telemetry.update();
        driveBackward(DRIVE_POWER);
        sleep(DRIVE_FWD_MS);
        stopDrive();

        // ── PHASE 2: Spin up shooter ───────────────────────────────────
        double targetVelocity = (targetRPM * shootertpr) / 60.0;
        shooter.setVelocity(targetVelocity);
        telemetry.addData("Phase", "Spinning up shooter");
        telemetry.update();
        sleep(SPINUP_MS);

        // ── PHASE 3: Shoot 3 balls ─────────────────────────────────────
        for (int i = 1; i <= 3 && opModeIsActive(); i++) {
            logShot(i, 3);
            gate.setPosition(GATE_OPEN);
            sleep(GATE_OPEN_MS);
            gate.setPosition(GATE_CLOSED);
            sleep(GATE_CLOSE_MS);
        }

        // ── PHASE 4: Drive BACK to reload wall ────────────────────────
        telemetry.addData("Phase", "Returning to reload wall");
        telemetry.update();
        // Keep shooter spinning so it stays warm during transit
        driveBackward(DRIVE_POWER);
        sleep(DRIVE_BACK_MS-350);
        stopDrive();

        // ── PHASE 5: Wait for human player to load 4th ball ───────────
        telemetry.addData("Phase", "WAITING — human player load ball now!");
        telemetry.update();
        sleep(RELOAD_WAIT_MS);

        // ── PHASE 6: Drive forward back to shooting position ──────────
        telemetry.addData("Phase", "Driving back to shoot position");
        telemetry.update();
        driveForward(DRIVE_POWER);
        sleep(DRIVE_FWD_MS);
        stopDrive();

        // Brief re-spinup (flywheel was kept on but may have dipped)
        sleep(RESPINUP_MS);

        // ── PHASE 7: Shoot 4th ball ────────────────────────────────────
        for (int i = 1; i <= 3 && opModeIsActive(); i++) {
            logShot(i+3, 6);
            gate.setPosition(GATE_OPEN);
            sleep(GATE_OPEN_MS);
            gate.setPosition(GATE_CLOSED);
            sleep(GATE_CLOSE_MS);
        }
        sleep(GATE_OPEN_MS);
        gate.setPosition(GATE_CLOSED);
        driveBackward(DRIVE_POWER);
        sleep(DRIVE_BACK_MS);

        // ── PHASE 8: Stop everything ───────────────────────────────────
        shooter.setVelocity(0);
        gate.setPosition(GATE_CLOSED);
        stopDrive();

        telemetry.addData("Phase", "DONE — 4 balls scored");
        telemetry.update();
    }

    private void driveForward(double power) {
        frontLeft.setPower(-power);
        backLeft.setPower(-power);
        frontRight.setPower(power);
        backRight.setPower(power);
    }

    /** Drive straight backward */
    private void driveBackward(double power) {
        frontLeft.setPower(power);
        backLeft.setPower(power);
        frontRight.setPower(-power);
        backRight.setPower(-power);
    }

    private void stopDrive() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /** Log current shot number and live shooter RPM to telemetry */
    private void logShot(int current, int total) {
        double tps        = shooter.getVelocity();
        double currentRPM = (tps / shootertpr) * 60.0;
        telemetry.addData("Phase", String.format("Shooting ball %d / %d", current, total));
        telemetry.addData("Shooter RPM", String.format("%.0f / %.0f", currentRPM, targetRPM));
        telemetry.update();
    }


}
