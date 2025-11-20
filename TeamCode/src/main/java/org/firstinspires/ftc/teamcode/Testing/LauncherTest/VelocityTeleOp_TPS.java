package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "VelocityTeleOp_TPS")
public class VelocityTeleOp_TPS extends OpMode {

    // Motor / encoder
    public static final double TICKS_PER_REV = 145.1;

    // Physics constants
    public static final double DIST_METERS = 1.2192;
    public static final double GRAVITY = 9.81;
    public static final double LAUNCH_ANGLE_DEG = 35;

    private DcMotorEx launchMotor;

    public static double computeVel(double distanceMeters) {
        double theta = Math.toRadians(LAUNCH_ANGLE_DEG);
        return Math.sqrt((distanceMeters * GRAVITY) / Math.sin(2  theta));
    }

    // TODO: tune to flywheel radius and gear ratio (1:1)
    public static double mpsToTPS(double mps) {
        /*
            // wheel rev/s needed for correct linear velocity
            double wheelRevPerSec = mps / (2 * Math.PI * FLYWHEEL_RADIUS_M);

            // motor rev/s after gear ratio
            double motorRevPerSec = wheelRevPerSec * GEAR_RATIO;

            // convert to ticks per second for setVelocity()
            return motorRevPerSec * TICKS_PER_REV;
         */
        double revPerSec = mps;
        return revPerSec * TICKS_PER_REV;
    }

    @Override
    public void init() {
        launchMotor = hardwareMap.get(DcMotorEx.class, "launch");
        launchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {

        double requiredMps = computeVel(DIST_METERS);
        double requiredTPS = mpsToTPS(requiredMps);

        launchMotor.setVelocity(requiredTPS);

        telemetry.addData("Fixed Distance (tiles)", 2);
        telemetry.addData("Required m/s", requiredMps);
        telemetry.addData("Target TPS", requiredTPS);
        telemetry.addData("Actual TPS", launchMotor.getVelocity());
        telemetry.update();
    }
}
