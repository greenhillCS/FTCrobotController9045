package org.firstinspires.ftc.teamcode.Testing.LauncherTest;

import java.util.ArrayList;
import java.util.List;

public class ArtifactTrajectory {

    // ----------------------------
    // Constants
    // ----------------------------
    private static final double rho_air = 1.225;   // air density (kg/m^3)
    private static final double g = 9.81;          // gravity (m/s^2)
    private static final double C_D = 0.50;        // drag coefficient
    private static final double alpha_CL = 0.2;    // Magnus lift factor
    private static final double dt = 0.001;        // timestep

    // Ball (FTC Artifact)
    private static final double m = 0.0748;        // kg
    private static final double r_b = 0.1270 / 2;  // m
    private static final double k = 0.4;           // inertia factor for solid sphere
    private static final double A = Math.PI * r_b * r_b;

    // Wheel
    private static final double R_w = 0.048;       // m
    private static final double m_w = 0.106;       // kg

    // ----------------------------
    // State variables (computed from impulse model)
    // ----------------------------
    private final double omega_w0;  // wheel angular velocity (rad/s)
    private final double V_b;       // ball exit speed (m/s)
    private final double omega_b;   // ball spin (rad/s)

    // ----------------------------
    // Constructor
    // ----------------------------
    public ArtifactTrajectory(double omega_w0) {
        this.omega_w0 = omega_w0;

        // Compute exit velocity and spin using impulse model
        double I_b = k * m * r_b * r_b;
        double I_w = 0.5 * m_w * R_w * R_w;

        double denom = (1.0 / m) + (r_b * r_b / I_b) + (R_w * R_w / I_w);
        double J = (omega_w0 * R_w) / denom;

        this.V_b = J / m;
        this.omega_b = (J * r_b) / I_b;
    }

    // ----------------------------
    // Simulate trajectory at given angle
    // ----------------------------
    private List<double[]> simulate(double angleDeg) {
        double theta = Math.toRadians(angleDeg);
        double Vx = V_b * Math.cos(theta);
        double Vy = V_b * Math.sin(theta);

        double x = 0.0;
        double y = 0.17272; // launch height (~0.17 m)

        List<double[]> points = new ArrayList<>();
        points.add(new double[]{x, y});

        while (y >= 0 && x < 10) { // stop when ball hits ground or goes too far
            double V = Math.sqrt(Vx * Vx + Vy * Vy);

            double ax = 0.0;
            double ay = -g;

            if (V > 0) {
                // Drag
                double F_drag = 0.5 * rho_air * V * V * A * C_D;
                double ax_drag = -(F_drag / m) * (Vx / V);
                double ay_drag = -(F_drag / m) * (Vy / V);

                // Magnus
                double S = omega_b * r_b / V;
                double C_L = alpha_CL * S;
                double F_magnus = 0.5 * rho_air * V * V * A * C_L;
                double ax_magnus = (F_magnus / m) * (-Vy / V);
                double ay_magnus = (F_magnus / m) * (Vx / V);

                ax = ax_drag + ax_magnus;
                ay = ay_drag + ay_magnus - g;
            }

            // Update velocity
            Vx += ax * dt;
            Vy += ay * dt;

            // Update position
            x += Vx * dt;
            y += Vy * dt;

            points.add(new double[]{x, y});
        }

        return points;
    }

    // ----------------------------
    // Interpolate height at given distance
    // ----------------------------
    private Double getHeightAtDistance(double angleDeg, double xTarget) {
        List<double[]> traj = simulate(angleDeg);

        for (int i = 0; i < traj.size() - 1; i++) {
            double[] p1 = traj.get(i);
            double[] p2 = traj.get(i + 1);

            double x1 = p1[0], y1 = p1[1];
            double x2 = p2[0], y2 = p2[1];

            if ((x1 <= xTarget && x2 >= xTarget) || (x2 <= xTarget && x1 >= xTarget)) {
                double frac = (xTarget - x1) / (x2 - x1);
                return y1 + frac * (y2 - y1);
            }
        }

        return null; // never reached that x
    }

    // ----------------------------
    // Solve for angle to hit target
    // ----------------------------
    public Double solveForAngle(double xTarget, double yTarget) {
        double low = 10;   // deg
        double high = 80;  // deg

        for (int iter = 0; iter < 40; iter++) { // binary search
            double mid = 0.5 * (low + high);
            Double h = getHeightAtDistance(mid, xTarget);

            if (h == null) {
                high = mid; // didn’t reach
                continue;
            }

            if (h > yTarget) {
                high = mid; // angle too high
            } else {
                low = mid; // angle too low
            }
        }

        return 0.5 * (low + high);
    }

    // ----------------------------
    // Accessors
    // ----------------------------
    public double getExitSpeed() {
        return V_b;
    }

    public double getBallSpin() {
        return omega_b;
    }

    // ----------------------------
    // Example usage
    // ----------------------------
    public static void main(String[] args) {
        double omega_w0 = 628; // rad/s (~3820 rpm)
        ArtifactTrajectory traj = new ArtifactTrajectory(omega_w0);

        double xTarget = 4.5; // m
        double yTarget = 1.5; // m

        Double angle = traj.solveForAngle(xTarget, yTarget);

        System.out.printf("Exit speed: %.2f m/s%n", traj.getExitSpeed());
        System.out.printf("Ball spin: %.2f rad/s%n", traj.getBallSpin());
        System.out.printf("Required launch angle to hit (%.2f, %.2f) = %.2f°%n",
                xTarget, yTarget, angle);
    }
}
