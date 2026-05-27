package org.firstinspires.ftc.teamcode.Teacher;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * LineFollowerMecanumTeleOp.java
 *
 * REV Control Hub TeleOp — mecanum chassis, differential (tank-style) steering,
 * colored line following with guided calibration wizard.
 *
 * ── Calibration flow (runs automatically before Start) ───────────────────────
 *   Phase 1 — FLOOR:  Hold sensor over bare floor,       press A to capture.
 *   Phase 2 — LINE:   Hold sensor over center of line,   press A to capture.
 *   Phase 3 — EDGE:   Position sensor on the edge you want to ride, press A.
 *   Phase 4 — VERIFY: Robot nudges; press A if it turned toward the line,
 *                     B if it turned the wrong way (auto-flips and re-tests).
 *   Then press Start on the Driver Station to begin following.
 *
 * ── Hardware map names ────────────────────────────────────────────────────────
 *   "front_left"   "front_right"   "rear_left"   "rear_right"   "color_sensor"
 *
 * ── Motor direction convention ────────────────────────────────────────────────
 *   Left-side motors REVERSED so positive power = forward on both sides.
 *   If the robot drives backward, swap FORWARD/REVERSE on the right-side motors.
 * ─────────────────────────────────────────────────────────────────────────────
 */
@TeleOp(name = "Line Follower (Mecanum)", group = "Competition")
public class LineFollowerMecanumTeleOp extends LinearOpMode {

    // ── Drive parameters ──────────────────────────────────────────────────────
    /** Forward speed when centred on the edge  [0.0 – 1.0]. Lower = safer to tune. */
    private static final double BASE_POWER = 0.35;

    /** Duration of the direction-check nudge in milliseconds. */
    private static final int NUDGE_MS = 600;

    /**
     * Consecutive loop cycles below lostThreshold before the robot stops.
     * Increase if stopping on small gaps; decrease for faster end-of-line response.
     */
    private static final int LINE_LOST_DEBOUNCE_CYCLES = 20;

    // ── PID gains ─────────────────────────────────────────────────────────────
    // Tune AFTER the calibration wizard confirms correct direction.
    //   1. Raise KP until robot oscillates (weaves), then back off ~30 %.
    //   2. Raise KD to damp remaining oscillation.
    //   3. Add a tiny KI only if there is a persistent left or right drift.
    private static double KP = 0.0003;
    private static double KI = 0.0000001;
    private static double KD = 0.0000001;

    // ── Calibrated values — filled in by runCalibration() ────────────────────
    private double floorValue = 180;      // Raw sensor reading over bare floor
    private double lineValue = 520;       // Raw sensor reading over line center
    private double edgeTarget = (floorValue+lineValue)/2;      // PID setpoint: midpoint of floor and line
    private double lostThreshold=floorValue;   // Below this → line considered lost
    private double correctionSign = 1.0;  // +1.0 or -1.0 to match chosen edge

    // ── Hardware ──────────────────────────────────────────────────────────────
    private DcMotor     frontLeft, frontRight, rearLeft, rearRight;
    private ColorSensor colorSensor;

    // ── PID controller ────────────────────────────────────────────────────────
    private LinePID pid;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void runOpMode() {

        initHardware();
//        runCalibration();

        // Build PID now that we have the calibrated edge target
        pid = new LinePID(KP, KI, KD, edgeTarget);
        pid.setOutputLimit(BASE_POWER);  // Correction is always ≤ BASE_POWER

        telemetry.setNumDecimalPlaces(8,8);
        telemetry.addData("══════════════════", "READY");
        telemetry.addData("Press START",      "to begin line following");
        telemetry.addData("Edge target",      String.format("%.0f", edgeTarget));
        telemetry.addData("Lost threshold",   String.format("%.0f", lostThreshold));
        telemetry.addData("Correction sign",  correctionSign > 0
                ? "+1  (sensor rides RIGHT edge)"
                : "-1  (sensor rides LEFT edge)");
        telemetry.update();

        waitForStart();
        pid.reset();

        int lineLostCount = 0;

        // ── Main control loop ─────────────────────────────────────────────────
        while (opModeIsActive()) {

            double sensorValue = readSensor();

            // ── Line-end debounce ─────────────────────────────────────────────
            if (sensorValue < lostThreshold) {
                lineLostCount++;
            } else {
                lineLostCount = 0;
            }

            if (lineLostCount >= LINE_LOST_DEBOUNCE_CYCLES) {
//                stopMotors();
                telemetry.addData("Status",       "LINE ENDED — stopped");
//                telemetry.addData("Sensor value", String.format("%.0f", sensorValue));
//                telemetry.update();
//                break;
            }

            // ── PID steering ──────────────────────────────────────────────────
            // correctionSign flips the output to match whichever edge was chosen.
            // Positive final correction → right side faster → robot turns left.
            // Negative final correction → left  side faster → robot turns right.
            double correction = pid.compute(sensorValue) * correctionSign;

            double leftPower  = clamp(BASE_POWER - correction, -1.0, 1.0);
            double rightPower = clamp(BASE_POWER + correction, -1.0, 1.0);

            telemetry.addData("P", KP);
            telemetry.addData("I", KI);
            telemetry.addData("D", KD);
            telemetry.addData("Tune", "\nu/d P, l/r D, bump I\n");

            if (gamepad1.right_trigger > 0.5){
                // Front and rear on each side get identical power — no strafe.
                frontLeft.setPower(leftPower);
                rearLeft.setPower(leftPower);
                frontRight.setPower(rightPower);
                rearRight.setPower(rightPower);
            }else{
                stopMotors();
            }

            if (gamepad1.rightBumperWasPressed()){
                KI *= 1.1;
            }
            if (gamepad1.leftBumperWasPressed()){
                KI *= 0.9;
            }
            if (gamepad1.dpadUpWasPressed()){
                KP *= 1.1;
            }
            if (gamepad1.dpadDownWasPressed()){
                KP *= 0.9;
            }
            if (gamepad1.dpadRightWasPressed()){
                KD *= 1.1;
            }
            if (gamepad1.dpadLeftWasPressed()){
                KD *= 0.9;
            }



            // ── Telemetry ─────────────────────────────────────────────────────
//            telemetry.addData("Status",      "Following line");
            telemetry.addData("Sensor",      String.format("%.0f  (target %.0f)", sensorValue, edgeTarget));
            telemetry.addData("Error",       String.format("%.0f", edgeTarget - sensorValue));
            telemetry.addData("Correction",  String.format("%.4f", correction));
            telemetry.addData("L / R power", String.format("%.3f  /  %.3f", leftPower, rightPower));
//            telemetry.addData("Lost count",  lineLostCount + " / " + LINE_LOST_DEBOUNCE_CYCLES);
            telemetry.update();
        }

        stopMotors();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CALIBRATION WIZARD
    // ══════════════════════════════════════════════════════════════════════════

    private void runCalibration() {

        // ── Phase 1: Floor reading ────────────────────────────────────────────
        showCalibPrompt("1 / 4  FLOOR",
                "Hold sensor over BARE FLOOR (no line underneath)",
                "Press  A  to capture");

        waitForButtonRelease();
        while (!gamepad1.a && !isStopRequested()) {
            showCalibPrompt("1 / 4  FLOOR",
                    "Hold sensor over BARE FLOOR",
                    "Press  A  to capture");
            telemetry.addData("Live sensor", String.format("%.0f", readSensor()));
            telemetry.update();
        }
        floorValue = readSensor();
        waitForButtonRelease();
        showCaptured("Floor", floorValue);
        sleep(700);

        // ── Phase 2: Line reading ─────────────────────────────────────────────
        showCalibPrompt("2 / 4  LINE CENTER",
                "Hold sensor over the CENTER of the line",
                "Press  A  to capture");

        waitForButtonRelease();
        while (!gamepad1.a && !isStopRequested()) {
            showCalibPrompt("2 / 4  LINE CENTER",
                    "Hold sensor over CENTER OF LINE",
                    "Press  A  to capture");
            telemetry.addData("Live sensor", String.format("%.0f", readSensor()));
            telemetry.update();
        }
        lineValue = readSensor();
        waitForButtonRelease();

        // Sanity check
        if (lineValue <= floorValue) {
            telemetry.addLine("⚠  WARNING: line reading is NOT higher than floor!");
            telemetry.addData("Line value",  String.format("%.0f", lineValue));
            telemetry.addData("Floor value", String.format("%.0f", floorValue));
            telemetry.addLine("Check: correct color channel? Sensor over the line?");
            telemetry.addLine("A = continue anyway    B = restart wizard");
            telemetry.update();
            waitForButtonRelease();
            while (!gamepad1.a && !gamepad1.b && !isStopRequested()) { idle(); }
            if (gamepad1.b) { waitForButtonRelease(); runCalibration(); return; }
            waitForButtonRelease();
        }

        // Derived thresholds
        edgeTarget    = (lineValue + floorValue) / 2.0;
        // lostThreshold sits 40 % of the way up from floor to edgeTarget,
        // giving a comfortable margin above pure floor noise.
        lostThreshold = floorValue + (edgeTarget - floorValue) * 0.40;

        showCaptured("Line", lineValue);
        telemetry.addData("Edge target (PID setpoint)", String.format("%.0f", edgeTarget));
        telemetry.addData("Lost threshold",              String.format("%.0f", lostThreshold));
        telemetry.update();
        sleep(1000);

        // ── Phase 3: Edge selection ───────────────────────────────────────────
        showCalibPrompt("3 / 4  CHOOSE EDGE",
                "Place the sensor on the EDGE of the line you want to ride.\n"
                + "Live reading should be close to " + String.format("%.0f", edgeTarget) + ".",
                "Press  A  when positioned");

        waitForButtonRelease();
        while (!gamepad1.a && !isStopRequested()) {
            double live = readSensor();
            double dist = Math.abs(live - edgeTarget);
            String quality = dist < 200 ? "✓  Good — on the edge"
                           : dist < 600 ? "~  Move closer to edge"
                                        : "✗  Too far — not on edge";
            showCalibPrompt("3 / 4  CHOOSE EDGE",
                    "Position sensor on the EDGE of the line",
                    "Press  A  when positioned");
            telemetry.addData("Live sensor", String.format("%.0f", live));
            telemetry.addData("Target",      String.format("%.0f", edgeTarget));
            telemetry.addData("Quality",     quality);
            telemetry.update();
        }
        waitForButtonRelease();
        sleep(400);

        // ── Phase 4: Direction verification ───────────────────────────────────
        correctionSign = 1.0;  // Tentative: sensor on right edge
        runNudgeTest();

        telemetry.addLine("4 / 4  DIRECTION CHECK");
        telemetry.addLine("Did the robot turn TOWARD the line?");
        telemetry.addLine("A = YES, correct      B = NO, flip it");
        telemetry.update();

        waitForButtonRelease();
        while (!gamepad1.a && !gamepad1.b && !isStopRequested()) { idle(); }

        if (gamepad1.b) {
            correctionSign = -1.0;
            waitForButtonRelease();

            telemetry.addLine("4 / 4  FLIPPED — running nudge test again");
            telemetry.addLine("Press  A  to run nudge test");
            telemetry.update();
            waitForButtonRelease();
            while (!gamepad1.a && !isStopRequested()) { idle(); }
            waitForButtonRelease();

            runNudgeTest();

            telemetry.addLine("Direction flipped and saved.  Press  A  to continue.");
            telemetry.update();
            waitForButtonRelease();
            while (!gamepad1.a && !isStopRequested()) { idle(); }
            waitForButtonRelease();
        } else {
            waitForButtonRelease();
        }
    }

    /**
     * Nudge the robot as if the sensor just dropped below the edge target
     * (simulates drifting off the line onto the floor side).
     * The robot should turn back toward the line.
     */
    private void runNudgeTest() {
        double testCorrection = 0.18 * correctionSign;
        frontLeft.setPower(clamp(BASE_POWER - testCorrection, -1.0, 1.0));
        rearLeft.setPower(clamp(BASE_POWER - testCorrection, -1.0, 1.0));
        frontRight.setPower(clamp(BASE_POWER + testCorrection, -1.0, 1.0));
        rearRight.setPower(clamp(BASE_POWER + testCorrection, -1.0, 1.0));
        sleep(NUDGE_MS);
        stopMotors();
        sleep(350);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HARDWARE INIT
    // ══════════════════════════════════════════════════════════════════════════

    private void initHardware() {

        frontLeft   = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        frontRight  = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        rearLeft   = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rearRight  = hardwareMap.get(DcMotor.class, "rightBackDrive");
//        frontLeft   = hardwareMap.get(DcMotor.class, "front_left");
//        frontRight  = hardwareMap.get(DcMotor.class, "front_right");
//        rearLeft    = hardwareMap.get(DcMotor.class, "rear_left");
//        rearRight   = hardwareMap.get(DcMotor.class, "rear_right");
        colorSensor = hardwareMap.get(ColorSensor.class, "color_sensor");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        rearRight.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor m : new DcMotor[]{frontLeft, frontRight, rearLeft, rearRight}) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Read the color channel that matches your line.
     * Change to .red() or .green() for other line colors.
     */
    private double readSensor() {
        return colorSensor.blue()/10.0;
    }

    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        rearLeft.setPower(0);
        rearRight.setPower(0);
    }

    private void showCalibPrompt(String phase, String instructions, String action) {
        telemetry.addLine("══ CALIBRATION: " + phase + " ══");
        telemetry.addLine(instructions);
        telemetry.addLine(action);
    }

    private void showCaptured(String label, double value) {
        telemetry.addData("✓ Captured — " + label, String.format("%.0f", value));
        telemetry.update();
    }

    /** Block until A and B are both released to prevent button bleed-through. */
    private void waitForButtonRelease() {
        while ((gamepad1.a || gamepad1.b) && !isStopRequested()) { idle(); }
        sleep(60);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
