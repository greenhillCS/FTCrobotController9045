package org.firstinspires.ftc.teamcode.Teacher;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Teacher.LinePID;

/**
 * LineFollowerTeleOp.java
 *
 * REV Control Hub TeleOp that autonomously follows a colored line using a
 * color sensor and a PID controller, then stops when the line ends.
 *
 * ── Hardware map names (configure in the Driver Station app) ──────────────────
 *   "left_drive"   → left  DcMotor
 *   "right_drive"  → right DcMotor
 *   "color_sensor" → REV Color Sensor v3 (or compatible)
 *
 * ── Quick-start tuning guide ──────────────────────────────────────────────────
 *   1. Place the robot centered on the line; run the opmode and read the
 *      telemetry "Raw sensor" value.  Set ON_LINE_VALUE to that number.
 *   2. Place the robot on bare floor; read "Raw sensor".
 *      Set OFF_LINE_VALUE to that number.
 *   3. LINE_LOST_THRESHOLD is the midpoint between the two — adjust if needed.
 *   4. Increase KP slowly until the robot oscillates, then back off ~30 %.
 *   5. Add a small KD to damp oscillations.  Keep KI at 0 until step 4-5 settle.
 * ─────────────────────────────────────────────────────────────────────────────
 */
@TeleOp(name = "Line Follower", group = "Competition")
public class LineFollowerTeleOp extends LinearOpMode {

    // ── Sensor calibration ────────────────────────────────────────────────────
    /**
     * The color-channel reading when the sensor is squarely over the line.
     * Tune this first (see guide above).  Uses the RED channel by default;
     * change getRedChannel() → getBlueChannel() / getGreenChannel() below
     * to match your line color.
     */
    private static final double ON_LINE_VALUE       = 3000.00;

    /**
     * Sensor reading when the robot is completely off the line (bare floor).
     * Used to decide "line lost."
     */
    private static final double OFF_LINE_VALUE      = 1600.0;

    /**
     * Sensor reading below which we declare the line has ended.
     * Default: midpoint between floor and line.  Move toward OFF_LINE_VALUE
     * if you want stricter detection, toward ON_LINE_VALUE if too sensitive.
     */
    private static final double LINE_LOST_THRESHOLD = (ON_LINE_VALUE + OFF_LINE_VALUE) / 2.0;

    /**
     * How many consecutive loop cycles the sensor must read below
     * LINE_LOST_THRESHOLD before we decide the line has truly ended
     * (debounce to ignore momentary gaps or sensor noise).
     */
    private static final int LINE_LOST_DEBOUNCE_CYCLES = 15;

    // ── Drive parameters ─────────────────────────────────────────────────────
    /** Forward speed of both motors when centred on the line  [0.0 – 1.0]. */
    private static final double BASE_POWER = 0.2;

    // ── PID gains ────────────────────────────────────────────────────────────
    private static final double KP = 0.0001;   // Start here; tune upward
    private static final double KI = 0.0;      // Add only after P+D are settled
    private static final double KD = 0.00001;  // Damping; increase if oscillating

    // ── Hardware ─────────────────────────────────────────────────────────────
    private DcMotor     leftFrontDrive;
    private DcMotor     rightFrontDrive;

    private DcMotor     leftBackDrive;
    private DcMotor     rightBackDrive;
    private ColorSensor colorSensor;

    // ── Controller ───────────────────────────────────────────────────────────
    private LinePID pid;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void runOpMode() {

        // ── Hardware init ─────────────────────────────────────────────────────
        leftFrontDrive   = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFrontDrive  = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftBackDrive   = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBackDrive  = hardwareMap.get(DcMotor.class, "rightBackDrive");
        colorSensor = hardwareMap.get(ColorSensor.class, "color_sensor");

        // Reverse whichever motor is physically mirrored on your chassis.
        // Flip this if the robot drives backward.
        leftFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotorSimple.Direction.FORWARD);


        // Brake on zero power so the robot holds position when stopped.
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Run without encoders — the PID handles steering, not position.
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFrontDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // ── PID init ──────────────────────────────────────────────────────────
        pid = new LinePID(KP, KI, KD, ON_LINE_VALUE);
        pid.setOutputLimit(BASE_POWER);   // Correction can be at most BASE_POWER

        // ── Telemetry preview ─────────────────────────────────────────────────
        telemetry.setNumDecimalPlaces(2,8);
        telemetry.addData("Status", "Initialized — waiting for start");
        telemetry.addData("ON_LINE_VALUE",       ON_LINE_VALUE);
        telemetry.addData("LINE_LOST_THRESHOLD", LINE_LOST_THRESHOLD);
        telemetry.update();

        waitForStart();

        // ── Place robot on the line, then press START ─────────────────────────
        pid.reset();

        int lineLostCount = 0;   // Debounce counter

        // ── Main control loop ─────────────────────────────────────────────────
        while (opModeIsActive()) {

            // 1. Read the sensor (change channel to match your line color)
//            double sensorValue = colorSensor.red();
             double sensorValue = colorSensor.blue();   // Blue line
            // double sensorValue = colorSensor.green();  // Green line

            // 2. Detect line-end with debounce
            if (sensorValue < LINE_LOST_THRESHOLD) {
                lineLostCount++;
            } else {
                lineLostCount = 0;   // Reset on any good reading
            }


            if (lineLostCount >= LINE_LOST_DEBOUNCE_CYCLES) {
                // Line has ended — stop and exit
                stopMotors();
                telemetry.addData("Status", "LINE ENDED — stopped");
                telemetry.addData("Final sensor value", sensorValue);
//                telemetry.update();
//                break;
            }

            // 3. Compute PID correction
            //    positive correction → steer right (robot drifted left)
            //    negative correction → steer left  (robot drifted right)
            double correction = pid.compute(sensorValue);

            // 4. Mix correction into differential drive
            // Flip power polarity based on side of line.  Currently set to left side of line
            double leftPower  = BASE_POWER * correction + BASE_POWER;
            double rightPower = BASE_POWER * -correction + BASE_POWER;

            // 5. Clamp to valid motor range
            leftPower  = clamp(leftPower,  -1.0, 1.0);
            rightPower = clamp(rightPower, -1.0, 1.0);

//            // 6. Apply power
            if (gamepad1.left_bumper){
                telemetry.addData("Driving", true);
                leftFrontDrive.setPower(leftPower);
                rightFrontDrive.setPower(rightPower);
                leftBackDrive.setPower(leftPower);
                rightBackDrive.setPower(rightPower);
            }else{
                telemetry.addData("Driving", false);
                stopMotors();
            }

            if (gamepad1.dpadRightWasPressed()){
                pid.setGains(pid.getKP() * 1.1, pid.getKD(), pid.getKI());
            }
            if (gamepad1.dpadLeftWasPressed()){
                pid.setGains(pid.getKP() * 0.9, pid.getKD(), pid.getKI());
            }
            telemetry.addData("Kp", pid.getKP());

            if (gamepad1.dpadDownWasPressed()){
                pid.setTarget(pid.getTarget() * 0.9);
            }
            if (gamepad1.dpadUpWasPressed()){
                pid.setTarget(pid.getTarget() * 1.1);
            }
            telemetry.addData("target", pid.getTarget());

            // 7. Telemetry (slows the loop slightly; remove for competition)
            telemetry.addData("Status",       "Following line");
            telemetry.addData("Sensor raw",   sensorValue);
            telemetry.addData("Error",        pid.getTarget() - sensorValue);
            telemetry.addData("Correction",   String.format("%.4f", correction));
            telemetry.addData("Left power",   String.format("%.3f", leftPower));
            telemetry.addData("Right power",  String.format("%.3f", rightPower));
            telemetry.addData("Lost count",   lineLostCount + "/" + LINE_LOST_DEBOUNCE_CYCLES);
            telemetry.update();
        }

        // Ensure motors are off when the opmode ends for any reason
        stopMotors();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void stopMotors() {
        leftFrontDrive.setPower(0);
        rightFrontDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
