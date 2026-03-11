/* Copyright (c) 2023 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.LimeLight;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * This OpMode illustrates using a camera to locate and drive towards a specific AprilTag.
 * The code assumes a Holonomic (Mecanum or X Drive) Robot.
 *
 * For an introduction to AprilTags, see the ftc-docs link below:
 * https://ftc-docs.firstinspires.org/en/latest/apriltag/vision_portal/apriltag_intro/apriltag-intro.html
 *
 * When an AprilTag in the TagLibrary is detected, the SDK provides location and orientation of the tag, relative to the camera.
 * This information is provided in the "ftcPose" member of the returned "detection", and is explained in the ftc-docs page linked below.
 * https://ftc-docs.firstinspires.org/apriltag-detection-values
 *
 * The drive goal is to rotate to keep the Tag centered in the camera, while strafing to be directly in front of the tag, and
 * driving towards the tag to achieve the desired distance.
 * To reduce any motion blur (which will interrupt the detection process) the Camera exposure is reduced to a very low value (5mS)
 * You can determine the best Exposure and Gain values by using the ConceptAprilTagOptimizeExposure OpMode in this Samples folder.
 *
 * The code assumes a Robot Configuration with motors named: leftfront_drive and rightfront_drive, leftback_drive and rightback_drive.
 * The motor directions must be set so a positive power goes forward on all wheels.
 * This sample assumes that the current game AprilTag Library (usually for the current season) is being loaded by default,
 * so you should choose to approach a valid tag ID (usually starting at 0)
 *
 * Under manual control, the left stick will move forward/back & left/right.  The right stick will rotate the robot.
 * Manually drive the robot until it displays Target data on the Driver Station.
 *
 * Press and hold the *Left Bumper* to enable the automatic "Drive to target" mode.
 * Release the Left Bumper to return to manual driving mode.
 *
 * Under "Drive To Target" mode, the robot has three goals:
 * 1) Turn the robot to always keep the Tag centered on the camera frame. (Use the Target Bearing to turn the robot.)
 * 2) Strafe the robot towards the centerline of the Tag, so it approaches directly in front  of the tag.  (Use the Target Yaw to strafe the robot)
 * 3) Drive towards the Tag to get to the desired distance.  (Use Tag Range to drive the robot forward/backward)
 *
 * Use DESIRED_DISTANCE to set how close you want the robot to get to the target.
 * Speed and Turn sensitivity can be adjusted using the SPEED_GAIN, STRAFE_GAIN and TURN_GAIN constants.
 *
 * Use Android Studio to Copy this Class, and Paste it into the TeamCode/src/main/java/org/firstinspires/ftc/teamcode folder.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 *
 */

@TeleOp(name="LimeLight Drive To AprilTag", group = "Concept")
public class LimeLightDriveToAprilTag extends LinearOpMode
{

    static class TargetWaypoint{
        double distance;
        double heading;
        double yaw;
        double tagID;

        public TargetWaypoint(double distance, double heading, double yaw, int tagId) {
            this.distance = distance;
            this.heading = heading;
            this.yaw = yaw;
            this.tagID = tagId;
        }
    }

    TargetWaypoint[] path = {
            // tiger's instructions, change these numbers to what you have on the board in order, you can delete this when you add the new ones
            new TargetWaypoint(69, -1, 50, 20), // pos 1
            new TargetWaypoint(21, -1, 50, 20), // pos 1
            new TargetWaypoint(62, -7, 37, 24),  // pos 2
            new TargetWaypoint(21, -16, 28, 24),  // pos 2
            new TargetWaypoint(60, -2, -31, 20),  // pos 3
            new TargetWaypoint(40, 0, -31, 20),  // pos 3
            new TargetWaypoint(21, 17, -54, 24),  // pos 4
            new TargetWaypoint(68, 3, -50, 24),  // pos 4
            new TargetWaypoint(71, -5, -20, 24),   // pos 5
            new TargetWaypoint(69, -1, 50, 20)   // pos 6 -- reset
    };
    // Adjust these numbers to suit your robot.
    //double DESIRED_DISTANCE = 30.0; //  this is how close the camera should get to the target (inches)
    //double DESIRED_YAW = 0;
    //double DESIRED_HEADING = 0;
    int currentWaypointIndex = 0;
    // positions are range, heading, yaw
    // blue id is 20, red id is 24
    // pos 1 bottom left 70, -10, 53, blue id
    // dissappers at 21, -10, 39, blue id
    // pos 2 62, -7, 37, red id
    // dissapears 21, -16, 28, red id
    // pos 3 62, 9, -39, blue id
    // pos 4 68, 7, -52 red id

    //other wise its gonna tweak
    final double RANGE_TOLERANCE = 1.5;   // inches
    final double HEADING_TOLERANCE = 2.5; // degrees
    final double YAW_TOLERANCE = 4;

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    final double SPEED_GAIN  =  0.02  ;   //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN =  0.015 ;   //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
    final double TURN_GAIN   =  0.01  ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)

    private DcMotor frontLeft   = null;  //  Used to control the left front drive wheel
    private DcMotor frontRight  = null;  //  Used to control the right front drive wheel
    private DcMotor backLeft    = null;  //  Used to control the left back drive wheel
    private DcMotor backRight   = null;  //  Used to control the right back drive wheel

    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int DESIRED_TAG_ID = 24;     // Choose the tag you want to approach or set to -1 for ANY tag.

    @Override public void runOpMode()
    {
        double  drive           = 1;        // Desired forward power/speed (-1 to +1)
        double  strafe          = 1;        // Desired strafe power/speed (-1 to +1)
        double  turn            = 1;        // Desired turning power/speed (-1 to +1)

        // Initialize the Apriltag Detection process
//        initAprilTag();

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
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0); // Switch to pipeline number

        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            // Check if we have finished the entire sequence
            if (currentWaypointIndex >= path.length) {
                moveRobot(0, 0, 0);
                telemetry.addData("Status", "SEQUENCE COMPLETE!");
                telemetry.update();
                continue; // Skip the rest of the loop
            }

            // Get the current target waypoint data
            TargetWaypoint currentTarget = path[currentWaypointIndex];
            telemetry.addData("Current Waypoint", "Pos " + (currentWaypointIndex + 1) + " (Tag " + currentTarget.tagID + ")");

            LLResult result = limelight.getLatestResult();
            boolean targetFound = false;
            double yawError = 0, headingError = 0, rangeError = 0;

            if (result != null && result.isValid()) {
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

                for (LLResultTypes.FiducialResult fiducial : fiducials) {
                    // Check if this is the tag we currently want
                    if (fiducial.getFiducialId() == currentTarget.tagID) {
                        targetFound = true;

                        Pose3D pose = fiducial.getTargetPoseCameraSpace();
                        double range = pose.getPosition().z * 39.3701; // convert to inches
                        double yaw = pose.getOrientation().getPitch(AngleUnit.DEGREES) * -1;
                        double heading = fiducial.getTargetXDegrees(); // Use specific tag's X, not overall Tx

                        yawError = yaw - currentTarget.yaw;
                        headingError = heading - currentTarget.heading;
                        rangeError = range - currentTarget.distance;

                        telemetry.addData("Range", range);
                        telemetry.addData("Heading", heading);
                        telemetry.addData("Yaw", yaw);


                        telemetry.addData("Range Error", "%.2f (Tol: %.1f)", rangeError, RANGE_TOLERANCE);
                        telemetry.addData("Heading Error", "%.2f (Tol: %.1f)", headingError, HEADING_TOLERANCE);
                        telemetry.addData("Yaw Error", "%.2f (Tol: %.1f)", yawError, YAW_TOLERANCE);

                        break; // Found our tag, stop looping through fiducials
                    }
                }
            }

            // Drive logic based on left bumper
            if (!gamepad1.left_bumper) {
                // AUTO MODE: Try to drive to the current waypoint
                if (targetFound) {
                    // Have we reached the target?
                    if (Math.abs(rangeError) <= RANGE_TOLERANCE &&
                            Math.abs(headingError) <= HEADING_TOLERANCE &&
                            Math.abs(yawError) <= YAW_TOLERANCE) {

                        // Arrived! Move to the next point
                        currentWaypointIndex++;
                        drive = 0; strafe = 0; turn = 0;
                        telemetry.addData("Status", "Reached point! Moving to next...");
                    } else {
                        // Still driving to target
                        drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                        turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                        strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
                        telemetry.addData("Auto Driving", "Drive %.2f, Strafe %.2f, Turn %.2f", drive, strafe, turn);
                    }
                } else {
                    // Target not found, stop or search
                    drive = 0; strafe = 0; turn = 0.2; // slight turn to search
                    telemetry.addData("Limelight", "Target Tag " + currentTarget.tagID + " not found. Searching...");
                }
            } else {
                // MANUAL MODE
                drive  = -gamepad1.left_stick_y  / 2.0;
                strafe = -gamepad1.left_stick_x  / 2.0;
                turn   = gamepad1.right_stick_x / 3.0;

                telemetry.addData("Manual Mode", "Hold Left Bumper for Auto Sequence");
            }

            telemetry.update();

            // Apply calculated power to wheels
            moveRobot(drive, strafe, turn);
            sleep(10);
        }
    }

    /**
     * Move robot according to desired axes motions
     * <p>
     * Positive X is forward
     * <p>
     * Positive Y is strafe left
     * <p>
     * Positive Yaw is counter-clockwise
     */
    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double leftFrontPower    =  x -y -yaw;
        double rightFrontPower   =  x +y +yaw;
        double leftBackPower     =  x +y -yaw;
        double rightBackPower    =  x -y +yaw;

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

//    /**
//     * Initialize the AprilTag processor.
//     */
//    private void initAprilTag() {
//        // Create the AprilTag processor by using a builder.
//        aprilTag = new AprilTagProcessor.Builder().build();
//
//        // Adjust Image Decimation to trade-off detection-range for detection-rate.
//        // eg: Some typical detection data using a Logitech C920 WebCam
//        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
//        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
//        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
//        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
//        // Note: Decimation can be changed on-the-fly to adapt during a match.
//        aprilTag.setDecimation(2);
//
//        // Create the vision portal by using a builder.
//        if (USE_WEBCAM) {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
//                    .addProcessor(aprilTag)
//                    .build();
//        } else {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(BuiltinCameraDirection.BACK)
//                    .addProcessor(aprilTag)
//                    .build();
//        }
//    }
//
//    /*
//     Manually set the camera gain and exposure.
//     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
//    */
//    private void    setManualExposure(int exposureMS, int gain) {
//        // Wait for the camera to be open, then use the controls
//
//        if (visionPortal == null) {
//            return;
//        }
//
//        // Make sure camera is streaming before we try to set the exposure controls
//        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
//            telemetry.addData("Camera", "Waiting");
//            telemetry.update();
//            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
//                sleep(20);
//            }
//            telemetry.addData("Camera", "Ready");
//            telemetry.update();
//        }
//
//        // Set camera controls unless we are stopping.
//        if (!isStopRequested())
//        {
//            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
//            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
//                exposureControl.setMode(ExposureControl.Mode.Manual);
//                sleep(50);
//            }
//            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
//            sleep(20);
//            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
//            gainControl.setGain(gain);
//            sleep(20);
//        }
//    }
}
