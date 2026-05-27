package org.firstinspires.ftc.teamcode.Teacher;

/**
 * LinePID.java
 *
 * A discrete PID controller that converts a color sensor error signal into
 * a differential steering correction for a two-motor ground robot.
 *
 * Usage:
 *   1. Instantiate with tuned kP, kI, kD gains.
 *   2. Call setTarget() once with your desired sensor reading (the "on-line" value).
 *   3. In your loop, call compute(currentReading) to get a correction value in [-1, 1].
 *   4. Apply correction:
 *        leftPower  = basePower - correction;
 *        rightPower = basePower + correction;
 *   5. Call reset() any time you want to clear integrator wind-up.
 */
public class LinePID {

    // ── Gains ──────────────────────────────────────────────────────────────────
    private double kP;   // Proportional gain
    private double kI;   // Integral gain
    private double kD;   // Derivative gain

    // ── Setpoint ───────────────────────────────────────────────────────────────
    private double target;   // Desired sensor value (centre of the line)

    // ── Internal state ─────────────────────────────────────────────────────────
    private double integralSum;       // Running sum for the I term
    private double lastError;         // Previous error for the D term
    private long   lastTimestamp;     // nanosecond timestamp of the last compute() call

    // ── Limits ─────────────────────────────────────────────────────────────────
    /** Caps the integral accumulator to prevent wind-up. */
    private double integralLimit = 1.0;

    /** Output is clamped to this range before being returned. */
    private double outputLimit = 1.0;

    // ──────────────────────────────────────────────────────────────────────────
    //  Constructors
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Create a PID with explicit gains.
     *
     * @param kP Proportional gain  (start ~0.02 and tune)
     * @param kI Integral gain      (start 0.0, add only if steady-state error persists)
     * @param kD Derivative gain    (start ~0.001 to damp oscillations)
     */
    public LinePID(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        reset();
    }

    /** Convenience constructor — sets target at creation time. */
    public LinePID(double kP, double kI, double kD, double target) {
        this(kP, kI, kD);
        this.target = target;
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Core API
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Set (or update) the desired sensor value that represents "on the line".
     * Call this once during init, or whenever the target changes.
     */
    public void setTarget(double target) {
        this.target = target;
    }

    /** Returns the current target. */
    public double getTarget() {
        return target;
    }

    /**
     * Run one PID iteration.
     *
     * @param measurement The current color-sensor reading.
     * @return A correction in [-outputLimit, +outputLimit].
     *         Positive  → robot is drifting LEFT of the line  → steer right
     *                      (subtract from left motor, add to right motor)
     *         Negative  → robot is drifting RIGHT of the line → steer left
     */
    public double compute(double measurement) {
        long now = System.nanoTime();
        // dt in seconds; guard against zero on the very first call
        double dt = (lastTimestamp == 0) ? 0.02 : (now - lastTimestamp) * 1e-9;
        lastTimestamp = now;

        double error = target - measurement;

        // ── Proportional ──────────────────────────────────────────────────────
        double pTerm = kP * error;

        // ── Integral (with anti-windup clamp) ─────────────────────────────────
        integralSum += error * dt;
        integralSum  = clamp(integralSum, -integralLimit, integralLimit);
        double iTerm = kI * integralSum;

        // ── Derivative (on measurement, not error, to avoid derivative kick) ──
        double dTerm = 0.0;
        if (dt > 0) {
            dTerm = kD * (error - lastError) / dt;
        }
        lastError = error;

        // ── Sum and clamp output ───────────────────────────────────────────────
        double output = pTerm + iTerm + dTerm;
        return clamp(output, -outputLimit, outputLimit);
    }

    /**
     * Reset integrator and derivative state.
     * Call at the start of a run or after a pause to prevent stale state.
     */
    public void reset() {
        integralSum   = 0.0;
        lastError     = 0.0;
        lastTimestamp = 0;
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Gain setters / getters  (useful for on-the-fly tuning via dashboard)
    // ──────────────────────────────────────────────────────────────────────────

    public void setGains(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public double getKP() { return kP; }
    public double getKI() { return kI; }
    public double getKD() { return kD; }

    /**
     * Change the integral wind-up clamp. Default is 1.0.
     * Reduce this (e.g. 0.3) if you see the robot over-correcting after a
     * long straight section.
     */
    public void setIntegralLimit(double limit) {
        this.integralLimit = Math.abs(limit);
    }

    /**
     * Change the maximum absolute output value. Default is 1.0 (full motor range).
     */
    public void setOutputLimit(double limit) {
        this.outputLimit = Math.abs(limit);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────────────────────────────────

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
