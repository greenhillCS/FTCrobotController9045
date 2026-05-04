package org.firstinspires.ftc.teamcode.Testing.TigerGroupComp;

import static java.lang.Math.abs;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;


@TeleOp(name = "sammie competition robot teleop")
public class sammieCompDrive extends OpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotorEx shooter;
    private Servo gate;
    private double shooterSpeed;
    private double testServo;
    private double shootertpr = 103.8;
    private double targetRPM = 1100;
    private Limelight3A limelight;
    private final double TURN_KP = 0.03;
    private ElapsedTime timer = new ElapsedTime();
    private int gateCount = 0;
    private boolean gateOpen = false;
    private boolean running = false;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        gate = hardwareMap.get(Servo.class, "gate");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    @Override
    public void loop() {
        double tps = shooter.getVelocity();
        double currentRPM = (tps / shootertpr) * 60;
        telemetry.addData("israel save us", "confirmed");
        telemetry.addData("rpm shooter", currentRPM);
        telemetry.addData("target rpm", targetRPM);
        telemetry.addData("gate", testServo);
        telemetry.update();

        if (gamepad1.right_bumper) {
            targetRPM++;
        }
        if (gamepad1.left_bumper) {
            targetRPM--;
        }

        shooter.setVelocity((targetRPM * shootertpr) / 60);

        if (gamepad1.right_trigger > 0.5 && !running) {
            gateCount = 0;
            gateOpen = false;
            running = true;
            gate.setPosition(0.3);
            timer.reset();
        }

        if (running) {
            if (!gateOpen && timer.milliseconds() >= 400) {
                // Open phase done → close
                gate.setPosition(0.05);
                gateOpen = true;
                timer.reset();

            } else if (gateOpen && timer.milliseconds() >= 600) {
                // Close phase done → next cycle
                gateCount++;
                gateOpen = false;

                if (gateCount < 3) {
                    gate.setPosition(0.3);
                    timer.reset();
                } else {
                    running = false; // All 3 cycles done
                }
            }
        }

        if (gamepad1.a) {
            gate.setPosition(0.29);
        }
        if (gamepad1.y) {
            gate.setPosition(0.05);
        }

        if (gamepad1.left_bumper){
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


                        range = pose.getPosition().z * 39.3701;
                        yaw = pose.getOrientation().getPitch(AngleUnit.DEGREES) * -1;
                        heading = tx;

                        yawError = 0;
                        headingError = heading - 0;
                        rangeError = 0;
                    final double SPEED_GAIN  =  0.04  ;   //  Forward Speed Control "Gain". eg: Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
                    final double STRAFE_GAIN =  0.03 ;   //  Strafe Speed Control "Gain".  eg: Ramp up to 25% power at a 25 degree Yaw error.   (0.25 / 25.0)
                    final double TURN_GAIN   =  0.02  ;   //  Turn Control "Gain".  eg: Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

                    final double MAX_AUTO_SPEED = 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
                    final double MAX_AUTO_STRAFE= 0.5;   //  Clip the approach speed to this max value (adjust for your robot)
                    final double MAX_AUTO_TURN  = 0.3;   //  Clip the turn speed to this max value (adjust for your robot)
                    // Use the speed and turn "gains" to calculate how we want the robot to move.
                    double drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                    double turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
                    double strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
                    moveRobot(drive, strafe, turn);
                }
            }
        }else{
            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            frontLeft.setPower(-0.8 * ((y + x + rx) / denominator));
            backLeft.setPower(-0.8 * ((y - x + rx) / denominator));
            frontRight.setPower(0.8 * (y - x - rx) / denominator);
            backRight.setPower(0.8 * (y + x - rx) / denominator);
        }

    }

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
