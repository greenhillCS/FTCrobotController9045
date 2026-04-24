package org.firstinspires.ftc.teamcode.Autons.ATRunner;

import static java.lang.Math.abs;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.LimeLight.LimeLightDriveToAprilTag_Joe;

import java.util.ArrayList;
import java.util.List;

public class ATRunnerBasic {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private ArrayList<Point> points;
    private Point point;
    public int pointIndex;
    private Limelight3A limelight;
    private DcMotor frontLeft   = null;  //  Used to control the left front drive wheel
    private DcMotor frontRight  = null;  //  Used to control the right front drive wheel
    private DcMotor backLeft    = null;  //  Used to control the left back drive wheel
    private DcMotor backRight   = null;  //  Used to control the right back drive wheel
    private double  drive;        // Desired forward power/speed (-1 to +1)
    private double  strafe;        // Desired strafe power/speed (-1 to +1)
    private double  turn;        // Desired turning power/speed (-1 to +1)
    private double DESIRED_DISTANCE = 30.0; //  this is how close the camera should get to the target (inches)
    private double DESIRED_YAW = 0;
    private double DESIRED_HEADING = 0;

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    private final double SPEED_GAIN  =  0.04  ;   //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    private final double STRAFE_GAIN =  0.05 ;   //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
    private final double TURN_GAIN   =  0.03  ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    private final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    private final double MAX_AUTO_STRAFE= 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    private final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)
    private double speed = 1;
    private ElapsedTime runtime;
    public enum STATE {
        MOVING,
        WAITING,
        DONE
    }
    public STATE state = STATE.MOVING;
    public ATRunnerBasic (HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        frontLeft  = hardwareMap.get(DcMotor.class, "rightBack");
        frontRight = hardwareMap.get(DcMotor.class, "leftBack");
        backLeft  = hardwareMap.get(DcMotor.class, "rightFront");
        backRight = hardwareMap.get(DcMotor.class, "leftFront");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0); // Switch to pipeline number

        pointIndex = 0;

        points = new ArrayList<>();

        drive = 0; // Desired forward power/speed (-1 to +1)
        strafe = 0; // Desired strafe power/speed (-1 to +1)
        turn = 0; // Desired turning power/speed (-1 to +1)

        runtime = new ElapsedTime();
    }
    public void init(){
        runtime.reset();
    }
    public void addPoint(double range, double yaw, double heading, int id, double time, double speed){
        points.add(new Point(range, yaw, heading, id, time, speed));
    }
    public int getPointIndex(){
        return pointIndex;
    }
    private void moveRobot(double x, double y, double yaw) {
        speed = point.getSpeed();

        // Calculate wheel powers.
        double leftFrontPower    =  x -y -yaw;
        double rightFrontPower   =  x +y +yaw;
        double leftBackPower     =  x +y -yaw;
        double rightBackPower    =  x -y +yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(abs(leftFrontPower), abs(rightFrontPower));
        max = Math.max(max, abs(leftBackPower));
        max = Math.max(max, abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send powers to the wheels.
        frontLeft.setPower(leftFrontPower * speed);
        frontRight.setPower(rightFrontPower * speed);
        backLeft.setPower(leftBackPower * speed);
        backRight.setPower(rightBackPower * speed);
    }
    public void stopMotors(){
        backRight.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        frontLeft.setPower(0);
    }
    public void update(){

        switch(state) {
            case MOVING:
                LLResult result = limelight.getLatestResult();
                if (result != null && result.isValid()) {
                    double tx = result.getTx(); // How far left or right the target is (degrees)
                    double ty = result.getTy(); // How far up or down the target is (degrees)
                    double ta = result.getTa(); // How big the target looks (0%-100% of the image)
                    double yawError = 100;
                    double headingError = 100;
                    double rangeError = 100;
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
                        point = points.get(pointIndex);
                        if (point.getId() == id) {
                            range = pose.getPosition().z * 39.3701;
                            yaw = pose.getOrientation().getPitch(AngleUnit.DEGREES) * -1;
                            heading = tx;
                            DESIRED_HEADING = point.getHeading();
                            DESIRED_DISTANCE = point.getRange();
                            DESIRED_YAW = point.getYaw();

                            yawError = yaw - DESIRED_YAW;
                            headingError = heading - DESIRED_HEADING;
                            rangeError = range - DESIRED_DISTANCE;
                            variance = abs(yawError) + abs(headingError) + abs(rangeError);

                            if (abs(yawError) <= 4 && abs(headingError) <= 2 && abs(rangeError) <= 2) {
                                runtime.reset();
                                state = STATE.WAITING;
                                break;
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


                        telemetry.addData("Variance", variance);
                        telemetry.addData("Current WP", point.get());
                        telemetry.addData("Range", range);
                        telemetry.addData("Heading", heading);
                        telemetry.addData("Yaw", yaw);
                        telemetry.addData("Range Error", rangeError);
                        telemetry.addData("Heading Error", headingError);
                        telemetry.addData("Yaw Error", yawError);

                    }
                    // Use the speed and turn "gains" to calculate how we want the robot to move.
                    drive = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                    turn = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                    strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

                } else {
                    drive = 0.0;
                    strafe = 0.0;
                    turn = 0.15;
                    telemetry.addData("Limelight", "No Targets");
                }

                // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
                if (gamepad.left_bumper) {
                    // drive using manual POV Joystick mode.  Slow things down to make the robot more controlable.
                    drive = -gamepad.left_stick_y / 2.0;  // Reduce drive rate to 50%.
                    strafe = -gamepad.left_stick_x / 2.0;  // Reduce strafe rate to 50%.
                    turn = gamepad.right_stick_x / 3.0;  // Reduce turn rate to 33%.
                    telemetry.addData("Manual", "Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);

                } else {
                    telemetry.addData("Auto", "Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
                }

                // Apply desired axes motions to the drivetrain.
                moveRobot(drive, strafe, turn);
                break;
            case WAITING:

                stopMotors();

                if(runtime.seconds() >= point.getTime()) {
                    pointIndex++;
                    state = STATE.MOVING;

                    if (pointIndex >= points.size()) {
                        state = STATE.DONE;
                        break;
                    }

                    point = points.get(pointIndex);
                    break;
                }
                break;
            case DONE:
                stopMotors();
        }
        telemetry.addData("State", state);
        telemetry.addData("Point Index", pointIndex);
        telemetry.addData("Current WP", point.get());
    }
}
