package org.firstinspires.ftc.teamcode.Testing.LimeLight;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutonAssets.drive.PositionStorage;
import org.firstinspires.ftc.teamcode.AutonAssets.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.AutonAssets.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.Testing.AlianceColor.AlianceColorSyncTool;

@Autonomous(name="LimelightAuton Near", group="Limelight")

public class LimelightAuton extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private TurretLimelight turret;
    private AlianceColorSyncTool as;
    private DcMotor leftFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightFrontDrive;
    private DcMotor rightBackDrive;
    private DcMotorEx launcher;
    private DcMotor intake;
    private double moveBackEnd = 1.4;
    private double speedupEnd = 14;
    private double launchEnd1 = speedupEnd + 0.6;
    private double waitEnd = launchEnd1 + 0.5;
    private double launchEnd2 = waitEnd + 4;
    private double parkEnd = launchEnd2 + 1;
    private double speed = -0.5;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        as = new AlianceColorSyncTool(hardwareMap, telemetry, gamepad2);

        turret = new TurretLimelight(hardwareMap, telemetry, gamepad2);

        launcher = hardwareMap.get(DcMotorEx.class,"launcher");// Port 2
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        intake = hardwareMap.get(DcMotor.class,"intake");// Port 0
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.setPower(0);

        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFront");//port 1
        leftBackDrive = hardwareMap.get(DcMotor.class, "leftBack");//port 3
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront");//port 0
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBack");//port 2
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
        as.update();
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        turret.updateID();
        launcher.setVelocity(700);
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        if(runtime.seconds() < launchEnd2 && runtime.seconds() > moveBackEnd) {
            turret.update();
            telemetry.addData("Turret: ", "Active");
        }else {
            telemetry.addData("Turret: ", "Inactive");
        }

        if(runtime.seconds() < moveBackEnd) {
            rightFrontDrive.setPower(speed);
            leftFrontDrive.setPower(speed);
            rightBackDrive.setPower(speed);
            leftBackDrive.setPower(speed);
            telemetry.addData("Moving Back until Runtime: ", moveBackEnd);
        }else if(runtime.seconds() < speedupEnd) {
            rightFrontDrive.setPower(0);
            leftFrontDrive.setPower(0);
            rightBackDrive.setPower(0);
            leftBackDrive.setPower(0);
            telemetry.addData("Speeding Up until Runtime: ", speedupEnd);
        }else if(runtime.seconds() < launchEnd1) {
            intake.setPower(1);
            telemetry.addData("Launching (1/2) until Runtime: ", launchEnd1);
        }else if(runtime.seconds() < waitEnd) {
            intake.setPower(0);
            telemetry.addData("Waiting until Runtime: ", waitEnd);
        }else if(runtime.seconds() < launchEnd2) {
            intake.setPower(1);
            telemetry.addData("Launching (2/2) until Runtime: ", launchEnd2);
        }else if(runtime.seconds() < parkEnd) {
            launcher.setVelocity(0);
            intake.setPower(0);
            rightFrontDrive.setPower(speed);
            leftFrontDrive.setPower(speed);
            rightBackDrive.setPower(speed);
            leftBackDrive.setPower(speed);
            telemetry.addData("Parking until Runtime: ", parkEnd);
        }else {
            intake.setPower(0);
            rightFrontDrive.setPower(0);
            leftFrontDrive.setPower(0);
            rightBackDrive.setPower(0);
            leftBackDrive.setPower(0);
            telemetry.addData("Parking until: ", "TeleOp Start");
        }
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {}

}
