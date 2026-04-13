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

package org.firstinspires.ftc.teamcode.Teacher;

import static java.lang.Math.abs;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@TeleOp(name="Teacher LimeLight Drive To AprilTag", group = "Main")
public class LimeLightDriveToAprilTag_Teacher extends LinearOpMode
{
    private class Waypoint{
        public int id;
        public double distance;
        public double yaw;
        public double heading;
        public double shootTPS;
        public Waypoint(int id, double distance, double yaw, double heading, double shootTPS){
            this.id = id;
            this.distance = distance;
            this.yaw = yaw;
            this.heading = heading;
            this.shootTPS = shootTPS;
        }
    }

    // Adjust these numbers to suit your robot.
    double DESIRED_DISTANCE = 30.0; //  this is how close the camera should get to the target (inches)
    double DESIRED_YAW = 0;
    double DESIRED_HEADING = 0;

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
    final double SPEED_GAIN  =  0.04  ;   //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
    final double STRAFE_GAIN =  0.03 ;   //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
    final double TURN_GAIN   =  0.02  ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)

    private DcMotor frontLeft   = null;  //  Used to control the left front drive wheel
    private DcMotor frontRight  = null;  //  Used to control the right front drive wheel
    private DcMotor backLeft    = null;  //  Used to control the left back drive wheel
    private DcMotor backRight   = null;  //  Used to control the right back drive wheel
    private DcMotorEx shooter = null;
    private Servo release = null;
    private double releaseBalls = 0.5;
    private double holdBalls = 1;
    @Override public void runOpMode()
    {
        double  drive           = 0;        // Desired forward power/speed (-1 to +1)
        double  strafe          = 0;        // Desired strafe power/speed (-1 to +1)
        double  turn            = 0;        // Desired turning power/speed (-1 to +1)
        ElapsedTime runtime = new ElapsedTime();
        double shotTime = 0;

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        frontLeft  = hardwareMap.get(DcMotor.class, "rightBack");
        frontRight = hardwareMap.get(DcMotor.class, "leftBack");
        backLeft  = hardwareMap.get(DcMotor.class, "rightFront");
        backRight = hardwareMap.get(DcMotor.class, "leftFront");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        release = hardwareMap.get(Servo.class, "release");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        release.setPosition(holdBalls);

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0); // Switch to pipeline number

        // Make Waypoints
        HashMap<String, Waypoint> waypoints = new HashMap<String, Waypoint>();
//        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
        // start point
        waypoints.put("humanDrop",new Waypoint(24, 152,-2, 0, 0));

        // halfway to blue
        waypoints.put("longShoot", new Waypoint(24, 130, -24,-5, 2200));

        // all the way to blue
        waypoints.put("mediumShoot", new Waypoint(24, 90, 0, -5, 1800));

        // halfway to red
        waypoints.put("shortShoot", new Waypoint(24, 54, 0,-5, 1700));

        // all the way to red
        waypoints.put("park", new Waypoint(24, 60, -34,6, 0));

        String currentWaypoint = "longShoot";

        telemetry.setAutoClear(true);
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
        waitForStart();

        while (opModeIsActive())
        {
            LLResult result = limelight.getLatestResult();
            if (gamepad1.triangleWasPressed()){
                currentWaypoint = "shortShoot";
            }
            if (gamepad1.circleWasPressed()){
                currentWaypoint = "mediumShoot";
            }
            if (gamepad1.crossWasPressed()){
                currentWaypoint = "longShoot";
            }
            if (gamepad1.squareWasPressed()){
                currentWaypoint = "humanDrop";
            }
            if (gamepad1.dpadLeftWasPressed()){
                currentWaypoint = "park";
            }

            Waypoint curPose = waypoints.get(currentWaypoint);
            assert curPose != null;
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

                    if (curPose.id == id){
                        range = pose.getPosition().z * 39.3701;
                        yaw = pose.getOrientation().getPitch(AngleUnit.DEGREES) * -1;
                        heading = tx;
                        DESIRED_HEADING = curPose.heading;
                        DESIRED_DISTANCE = curPose.distance;
                        DESIRED_YAW = curPose.yaw;

                        yawError = yaw - DESIRED_YAW;
                        headingError =  heading - DESIRED_HEADING;
                        rangeError = range - DESIRED_DISTANCE;
                        variance = abs(yawError) + abs(headingError) + abs(rangeError);

                        shooter.setVelocity(curPose.shootTPS);
                        if (currentWaypoint.equals("humanDrop")){
                            release.setPosition(holdBalls);
                        }
                        if ((variance < 2.0) && (currentWaypoint.contains("Shoot"))){
                            // release the servo
                            release.setPosition(releaseBalls);
                            //start timer
//                            if (shotTime == 0){
//                                shotTime = runtime.milliseconds();
//                            }
//                            if (runtime.milliseconds() - shotTime >= 2000){
//                                currentWaypoint = "humanDrop";
//                                shotTime = 0;
//                            }
                        }
                    }else{
                        telemetry.addData("wrong tag", "looking for Tag");
                        range = 0;
                        heading = 0;
                        yaw = 0;
                        headingError = 10;
                        rangeError = 0;
                        yawError = 0;
                    }


                    telemetry.addData("Variance", variance);
                    telemetry.addData("Current WP", currentWaypoint);
                    telemetry.addData("curPoseid", curPose.id);
                    telemetry.addData("Range", range);
                    telemetry.addData("Heading", heading);
                    telemetry.addData("Yaw", yaw);
                    telemetry.addData("Range Error", rangeError);
                    telemetry.addData("Heading Error", headingError);
                    telemetry.addData("Yaw Error", yawError);

                }
                // Use the speed and turn "gains" to calculate how we want the robot to move.
                drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
                strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

            } else {
                drive= 0.0;
                strafe = 0.0;
                turn = 0.15;
                telemetry.addData("Limelight", "No Targets");
            }

                        // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
            if (gamepad1.left_bumper) {
                if (gamepad1.rightBumperWasPressed()){
                    release.setPosition(holdBalls);
                }
                if (gamepad1.right_trigger > .5){
                    release.setPosition(releaseBalls);
                }
                // drive using manual POV Joystick mode.  Slow things down to make the robot more controlable.
                drive  = -gamepad1.left_stick_y  / 2.0;  // Reduce drive rate to 50%.
                strafe = -gamepad1.left_stick_x  / 2.0;  // Reduce strafe rate to 50%.
                turn   = gamepad1.right_stick_x / 3.0;  // Reduce turn rate to 33%.
                telemetry.addData("Manual","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);

            }else{
                telemetry.addData("Auto","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
            }
            telemetry.update();

            // Apply desired axes motions to the drivetrain.
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
        frontLeft.setPower(leftFrontPower);
        frontRight.setPower(rightFrontPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);
    }

}
