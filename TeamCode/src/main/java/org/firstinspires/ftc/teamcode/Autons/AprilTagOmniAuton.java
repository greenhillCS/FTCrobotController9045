package org.firstinspires.ftc.teamcode.Autons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutonAssets.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Tools.RobotAutoDriveToAprilTagOmniToolClass;

@Autonomous(name="BIG JUICY AUTON", group="Testing")
@Disabled
public class AprilTagOmniAuton extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private SampleMecanumDrive drive;
    private double maxPower = 1.0;
    RobotAutoDriveToAprilTagOmniToolClass distance;
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotor intake;
    private Servo flicker;
    private DcMotor shooterLeft;
    private DcMotor shooterRight;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        distance = new RobotAutoDriveToAprilTagOmniToolClass(55, hardwareMap, telemetry, gamepad2);

        frontRight = hardwareMap.get(DcMotor.class, "rightFront");
        frontLeft = hardwareMap.get(DcMotor.class, "leftFront");
        backRight = hardwareMap.get(DcMotor.class, "rightBack");
        backLeft = hardwareMap.get(DcMotor.class, "leftBack");

        drive = new SampleMecanumDrive(hardwareMap);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotor.class,"intake"); //Port 1
        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        flicker = hardwareMap.get(Servo.class, "flicker");

        shooterLeft = hardwareMap.get(DcMotor.class, "shooterLeft");
        shooterRight = hardwareMap.get(DcMotor.class, "shooterRight");
        shooterLeft.setDirection(DcMotorSimple.Direction.REVERSE);

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
        intake.setPower(maxPower);
        shooterLeft.setPower(1);
        shooterRight.setPower(1);

        if (runtime.seconds() < 1){
            frontLeft.setPower (.5);
            frontRight.setPower (.5);
            backRight.setPower (.5);
            backLeft.setPower (.5);
        }
        else {
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backRight.setPower(0);
            backLeft.setPower(0);
        }
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        if(runtime.seconds() < 30){
            intake.setPower(maxPower);
            shooterLeft.setPower(1);
            shooterRight.setPower(1);
        }
        if(runtime.seconds() > 1 && runtime.seconds() <5){
            distance.update();
            distance.setDESIRED_YAW(30);
        }
        if(runtime.seconds() > 5 && runtime.seconds() < 6){
            flicker.setPosition(1);
        }
        if(runtime.seconds() > 6){
            flicker.setPosition(0.4);
            shooterRight.setPower(0);
            shooterLeft.setPower(0);
            intake.setPower(0);
        }
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
}
