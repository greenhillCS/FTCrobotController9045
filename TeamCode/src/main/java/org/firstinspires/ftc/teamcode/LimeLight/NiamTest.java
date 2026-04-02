package org.firstinspires.ftc.teamcode.LimeLight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/*
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 */
//TODO:Uncomment one of the following and rename group and name as needed.
@TeleOp(name="Random Limelight Thingy", group="zzzzz")
//@Autonomous(name="Change the name of your Auton", group="zzzzz")

public class NiamTest extends OpMode {
    // Declare OpMode members.
    double DESIRED_HEADING = 0;

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    final double SPEED_GAIN = 0.02;   //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN = 0.015;   //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
    final double TURN_GAIN = 0.01;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)
    private DcMotor frontLeft = null;  //  Used to control the left front drive wheel
    private DcMotor frontRight = null;  //  Used to control the right front drive wheel
    private DcMotor backLeft = null;  //  Used to control the left back drive wheel
    private DcMotor backRight = null;  //  Used to control the right back drive wheel
    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private ElapsedTime runtime = new ElapsedTime();
    boolean targetFound = false;    // Set to true when an AprilTag target is detected
    int counter = 0;
    double drive = 0;        // Desired forward power/speed (-1 to +1)
    double strafe = 0;        // Desired strafe power/speed (-1 to +1)
    double turn = 0;        // Desired turning power/speed (-1 to +1)
    public AprilTagProcessor aprilTag;
    public VisionPortal visionPortal;
    double[] ballDistance = { 68.0, 28.6, 64, 34.5, 63.6, 67, 69.0, 74, 68.0};
    double[] ballHeading = { -4.6, -15.3, -7, -9.17, 5.02, 2, 4.6, -0.5, -4.6};
    double[] ballYaw = { 55.0, 53.0, 36.4, 33.7, -34.6, -8, -55.0, -17.0, 55.0};
    int[] ballID = { 20, 20, 24, 24, 20, 20, 24, 24, 20};
    private Limelight3A limelight;

    LLResultTypes.FiducialResult fiducial;

    public boolean update(double DESIRED_DISTANCE, double DESIRED_YAW, double DESIRED_HEADING, int DESIRED_TAG_ID) {
        LLResult result = limelight.getLatestResult();
        boolean atTarget = false;
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // How far left or right the target is (degrees)
            double ty = result.getTy(); // How far up or down the target is (degrees)
            double ta = result.getTa(); // How big the target looks (0%-100% of the image)
            double yawError = 0;
            double headingError = 0;
            double rangeError = 0;
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

            for (LLResultTypes.FiducialResult fiducial : fiducials) {
                int id = fiducial.getFiducialId(); // The ID number of the fiducial
                double x = fiducial.getTargetXDegrees(); // Where it is (left-right)
                double y = fiducial.getTargetYDegrees(); // Where it is (up-down)
                Pose3D pose = fiducial.getTargetPoseCameraSpace();
                double range = pose.getPosition().z * 39.3701;
                double yaw = pose.getOrientation().getPitch(AngleUnit.DEGREES) * -1;
                double heading = tx;
                yawError = yaw - DESIRED_YAW;
                headingError = heading - DESIRED_HEADING;
                rangeError = range - DESIRED_DISTANCE;

                drive = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                turn = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

                telemetry.addData("Range", range);
                telemetry.addData("Heading", heading);
                telemetry.addData("Yaw", yaw);

                telemetry.addData("Range Error", rangeError);
                telemetry.addData("Heading Error", headingError);
                telemetry.addData("Yaw Error", yawError);
                if (fiducial.getFiducialId() != DESIRED_TAG_ID) {
                    drive = 0.0;
                    strafe = 0.0;
                    turn = 0.2;
                }
            }
            // Use the speed and turn "gains" to calculate how we want the robot to move.
            if ((Math.abs(yawError) > 3) && (Math.abs(rangeError) > 3)){

            } else {
                return true;
            }

        } else {
            drive = 0.0;
            strafe = 0.0;
            turn = 0.2;
            telemetry.addData("Limelight", "No Targets");
        }
        targetFound = false;
        AprilTagDetection desiredTag = null;

//        // Step through the list of detected tags and look for a matching tag
//        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//        for (AprilTagDetection detection : currentDetections) {
//            // Look to see if we have size info on this tag.
//            if (detection.metadata != null) {
//                //  Check to see if we want to track towards this tag.
//                if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID)) {
//                    // Yes, we want to use this tag.
//                    targetFound = true;
//                    desiredTag = detection;
//                    break;  // don't look any further.
//                } else {
//                    // This tag is in the library, but we do not want to track it right now.
//                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
//                }
//            } else {
//                // This tag is NOT in the library, so we don't have enough information to track to it.
//                telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
//            }
//        }
//
//        // Tell the driver what we see, and what to do.
        if (targetFound) {
            telemetry.addData("\n>", "HOLD Left-Bumper to Drive to Target\n");
            telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
            telemetry.addData("Range", "%5.1f inches", desiredTag.ftcPose.range);
            telemetry.addData("Bearing", "%3.0f degrees", desiredTag.ftcPose.bearing);
            telemetry.addData("Yaw", "%3.0f degrees", desiredTag.ftcPose.yaw);
        } else {
            telemetry.addData("\n>", "Drive using joysticks to find valid target\n");
        }
        moveRobot(drive, strafe, turn);
        return atTarget;
    }

    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double leftFrontPower = x - y - yaw;
        double rightFrontPower = x + y + yaw;
        double leftBackPower = x + y - yaw;
        double rightBackPower = x - y + yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send powers to the wheels.
        frontLeft.setPower(leftFrontPower);
        frontRight.setPower(rightFrontPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);
    }


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "rightBack");
        frontRight = hardwareMap.get(DcMotor.class, "leftBack");
        backLeft = hardwareMap.get(DcMotor.class, "rightFront");
        backRight = hardwareMap.get(DcMotor.class, "leftFront");

        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0); // Switch to pipeline number
        telemetry.addData("Status", "Initializing");
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {

    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        int i = 0;
            while (update(ballDistance[i], ballYaw[i], ballHeading[i], ballID[i])){
                i++;
            }
        runtime.reset();

    }
    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */


    @Override
    public void loop() {
//        if (counter == 0 && update(68.0, 55, -4.6,20)){
//            counter++;
//        }
//        if (counter == 1 && update(36, 48, -11,20)){
//            counter++;
//        }
//        if (counter == 2 && update(64, 26, 2.4, 24)){
//            counter++;
//        }
//        if (counter == 3 && update(34.5, 33.7,-9.17, 24)){
//            counter++;
//        }
//        if (counter == 4 && update(63.6, -34.6, 5.02, 20)){
//            counter++;
//        }
//        if (counter == 5 && update(67, -8, 2, 20)){
//            counter++;
//        }
//        if (counter == 6 && update(69.0, -55, 4.6, 24)){
//            counter++;
//        }
//        if (counter == 7 && update(74, -17.0, -0.5, 24)){
//            counter++;
//        }
//        if (counter == 8 && update(68.0, 55.0, -4.6, 20)){
//            counter++;
//        }
    }


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop () {
    }
}