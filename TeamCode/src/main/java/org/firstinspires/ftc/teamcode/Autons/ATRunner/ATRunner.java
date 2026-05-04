package org.firstinspires.ftc.teamcode.Autons.ATRunner;

import static java.lang.Math.abs;
import static java.lang.Math.cos;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.LimeLight.LimeLightDriveToAprilTag_Joe;

import java.util.ArrayList;
import java.util.List;

public class ATRunner {
    //Define variables here
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;
    ArrayList<Point> posList;
    private int currentPoint = 0;
    DcMotorEx turret;
    Limelight3A limelight;
    final double degreesPerTick = (1.0 / 384.5) * (28.0 / 145.0) * (360.0 / 1.0);
    private double robotRange = 0;
    private double robotYaw = 0;
    private double robotHeading;
    private double robotX = 0;
    private double robotY = 0;
    private double robotA = 0;
    private double turretAngle = 0;
    private double turretRadius = 0;
    private double camX = 0;
    private double camY = 0;

    public ATRunner(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        turret = hardwareMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        posList = new ArrayList<>();

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0); // Switch to pipeline number
    }
    public void setTurretPos(double x, double y, double r){
        camX = Math.max(x, 0.0001);
        camY = Math.max(y, 0.0001);
        turretRadius = Math.max(r, 0.0001);
    }
    public void addPoint(double range, double yaw, double heading, int id){
        posList.add(new Point(range, yaw, heading, id));
    }
    public int getCurrentPoint(){
        return currentPoint;
    }
    private void robotPosCalc(double Y, double H, double r, Pose3D pose){
        //Yes, I could've used law of sines, I didn't feel like it
        //This creates a triangle with the point at the camera on the turret(B), the center of turret rotation(C), and the april tag(A)
        //Yes, the turret is supposedly inline with the center of rotation, lets just go with that I am accounting for error and any edge cases

        //The origin of angles point straight in front of them or straight towards the top of the field when using field coordinates
        //angles are positive going clockwise and negative counterclockwise

        double a = turretRadius;
        double c = r;

        double B = 180 - H;
        double b = Math.sqrt(Math.pow(a, 2) + Math.pow(c, 2) - 2 * a * c * Math.cos(Math.toRadians(B)));

        double A = Math.toDegrees(Math.acos((Math.pow(a, 2) - Math.pow(b, 2) - Math.pow(c, 2)) / (-2 * b * c)));
        double C = Math.toDegrees(Math.acos((Math.pow(c, 2) - Math.pow(a, 2) - Math.pow(b, 2)) / (-2 * a * b)));

        //Once the position of the camera is aquired, all that is left is to transform this point polarly to the center of the robot.
        //the points of the triangle then become the april tag(E), the center of turret rotation(F), and the center of the robot(G)


        double e = Math.sqrt(Math.pow(camX, 2) + Math.pow(camY, 2));
        double g = b;
        double F = (90 - Math.toDegrees(Math.asin(camY))) + (180 - turretAngle - C);
        double f = Math.sqrt(Math.pow(e, 2) + Math.pow(g, 2) - 2 * e * g * Math.cos(Math.toRadians(F)));

        double E = Math.toDegrees(Math.acos((Math.pow(e, 2) - Math.pow(f, 2) - Math.pow(g, 2)) / (-2 * f * g)));
        double G = Math.toDegrees(Math.acos((Math.pow(g, 2) - Math.pow(e, 2) - Math.pow(f, 2)) / (-2 * e * f)));
        telemetry.addData("A", A);
        telemetry.addData("a", a);
        telemetry.addData("B", B);
        telemetry.addData("b", b);
        telemetry.addData("C", C);
        telemetry.addData("c", c);
        telemetry.addData("E", E);
        telemetry.addData("e", e);
        telemetry.addData("F", F);
        telemetry.addData("f", f);
        telemetry.addData("G", G);
        telemetry.addData("g", g);



        //Polar coordinates of the robot as seen from the april tag
        robotHeading =  90 - Math.toDegrees(Math.asin(camY)) + G;
        robotRange = f;
        robotYaw = E + A + Y;
        telemetry.addData("Robot Polar Coordinates", "");
        telemetry.addData("Heading", robotHeading);
        telemetry.addData("Yaw", robotYaw);
        telemetry.addData("Range", robotRange);

        //Calculate the cartesian position of the april tag as seen from the field
        double rX = pose.getPosition().x - (turretRadius * Math.cos(Math.toRadians(90 - turretAngle))) - camX;
        double rY = pose.getPosition().y - (turretRadius * Math.sin(Math.toRadians(90 - turretAngle))) - camY;
        double rA = pose.getOrientation().getYaw(AngleUnit.DEGREES) - turretAngle;
        telemetry.addData("Robot Cartesian Coordinates", "");
        telemetry.addData("X", rX);
        telemetry.addData("Y", rY);
        telemetry.addData("Angle", rA);

        //Calculate the cartesian position of the april tag as seen from the field
        double tagAngle = 90 - (rA + robotHeading);
        double aX = rX + Math.cos(Math.toRadians(tagAngle));
        double aY = rY + Math.sin(Math.toRadians(tagAngle));
        double aA = 270 - tagAngle;
        telemetry.addData("April Tag Cartesian Coordinates", "");
        telemetry.addData("X", aX);
        telemetry.addData("Y", aY);
        telemetry.addData("Angle", aA);
    }

    public boolean update(){
        turretAngle = (double) turret.getCurrentPosition() * -degreesPerTick;
        telemetry.addData("turret", turretAngle);
        telemetry.addData("turret 1", (double) turret.getCurrentPosition() * -degreesPerTick);

        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            double ty = result.getTy(); // How far up or down the target is (degrees)
            double ta = result.getTa(); // How big the target looks (0%-100% of the image)
            double yawError = 0;
            double headingError = 0;
            double rangeError = 0;
            double variance = 100;
            double range = 0;
            double heading = 0;
            double yaw = 0;
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            for (LLResultTypes.FiducialResult fiducial : fiducials) {
                int id = fiducial.getFiducialId(); // The ID number of the fiducial
                double x = fiducial.getTargetXDegrees(); // Where it is (left-right)
                double y = fiducial.getTargetYDegrees(); // Where it is (up-down)
                Pose3D pose = fiducial.getTargetPoseCameraSpace();
                Pose3D p = fiducial.getRobotPoseFieldSpace();
                Point cPoint = posList.get(currentPoint);
                if (cPoint.getId() == id) {
                    range = pose.getPosition().z * 39.3701;
                    yaw = pose.getOrientation().getPitch(AngleUnit.DEGREES) * -1;
                    heading = tx;



//                    robotPosCalc(yaw, heading, range, p);



                    yawError = yaw - cPoint.getYaw();
                    headingError = heading - cPoint.getHeading();
                    rangeError = range - cPoint.getRange();
                    variance = abs(yawError) + abs(headingError) + abs(rangeError);

                    if (variance < 2.0) {
                        currentPoint++;
                        if (currentPoint >= posList.size()) {
                            return true;
                        }
                    }
                } else {
                    telemetry.addData("wrong tag", "looking for Tag");
                    range = 0;
                    heading = 0;
                    yaw = 0;
                    headingError = 10;
                    rangeError = 0;
                    yawError = 0;
                }

                telemetry.addData("Camera Polar Coordinates", "");
                telemetry.addData("Variance", variance);
                telemetry.addData("Current Point", currentPoint);
                telemetry.addData("Range", range);
                telemetry.addData("Heading", heading);
                telemetry.addData("Yaw", yaw);
                telemetry.addData("Range Error", rangeError);
                telemetry.addData("Heading Error", headingError);
                telemetry.addData("Yaw Error", yawError);
            }
        }
        return false;
    }
}
