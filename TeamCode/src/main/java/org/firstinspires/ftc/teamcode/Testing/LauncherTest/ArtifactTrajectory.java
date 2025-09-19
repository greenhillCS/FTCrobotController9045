package org.firstinspires.ftc.teamcode.Testing.LauncherTest;

import java.util.ArrayList;
import java.util.List;

public class ArtifactTrajectory {

    // Physical constants
    private static final double g = 9.81;        // gravity (m/s^2)
    private static final double m = 0.624;       // mass of ball (kg)
    private static final double r = 0.12;        // radius (m)
    private static final double A = Math.PI * r * r; // cross-sectional area (m^2)
    private static final double rho = 1.225;     // air density (kg/m^3)
    private static final double Cd = 0.47;       // drag coefficient
    private static final double dt = 0.001;      // timestep (s)
    private static final double C = 0.5 * rho * Cd * A; // drag constant

    // Magnus coefficient
    private static double magnusCoefficient(double omega) {
        return 0.5 * rho * A * r * omega;
    }

    // Simulate trajectory and return (x, y) points
    private static List<double[]> simulate(double v0, double angleDeg, double omega) {
        double theta = Math.toRadians(angleDeg);
        double vx = v0 * Math.cos(theta);
        double vy = v0 * Math.sin(theta);

        double x = 0, y = 0;
        double t = 0;
        double Cm = magnusCoefficient(omega);

        List<double[]> points = new ArrayList<>();

        while (y >= 0 && t < 5) { // stop when ball hits ground or after 5s
            points.add(new double[]{x, y});

            double v = Math.sqrt(vx * vx + vy * vy);

            // Drag forces
            double Fdx = -C * v * vx;
            double Fdy = -C * v * vy;

            // Magnus forces (perpendicular to velocity in 2D)
            double Fmx = Cm * vy;
            double Fmy = -Cm * vx;

            // Accelerations
            double ax = (Fdx + Fmx) / m;
            double ay = (Fdy + Fmy) / m - g;

            // Update velocity
            vx += ax * dt;
            vy += ay * dt;

            // Update position
            x += vx * dt;
            y += vy * dt;

            t += dt;
        }

        return points;
    }

    // Find horizontal distance at which trajectory crosses target height
    public static double getDistanceAtHeight(double v0, double angleDeg, double omega, double targetHeight) {
        List<double[]> trajectory = simulate(v0, angleDeg, omega);

        double lastX = -1;

        for (int i = 0; i < trajectory.size() - 1; i++) {
            double[] p1 = trajectory.get(i);
            double[] p2 = trajectory.get(i + 1);

            double y1 = p1[1], y2 = p2[1];
            if ((y1 - targetHeight) * (y2 - targetHeight) <= 0) {
                // Linear interpolation for crossing
                double x1 = p1[0], x2 = p2[0];
                double frac = (targetHeight - y1) / (y2 - y1);
                double xCross = x1 + frac * (x2 - x1);

                if (xCross > lastX) {
                    lastX = xCross; // keep furthest crossing
                }
            }
        }

        return lastX; // -1 if never reaches height
    }

    // Example usage
    //    public static void main(String[] args) {
    //        double v0 = 8.0;          // initial speed (m/s)
    //        double angle = 52.0;      // launch angle (deg)
    //        double omega = 30.0;      // spin rate (rad/s)
    //        double hoopHeight = 1.5;  // target height (m)
    //
    //        double distance = getDistanceAtHeight(v0, angle, omega, hoopHeight);
    //
    //        if (distance >= 0) {
    //            System.out.printf("Ball reaches %.2f m height at x = %.2f m%n", hoopHeight, distance);
    //        } else {
    //            System.out.printf("Ball never reaches %.2f m%n", hoopHeight);
    //        }
    //    }
}
