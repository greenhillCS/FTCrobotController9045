package org.firstinspires.ftc.teamcode.Autons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutonAssets.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.Testing.AlianceColor.AlianceColorSyncTool;
import org.firstinspires.ftc.teamcode.Testing.LimeLight.SensorLimelight3A;
import org.firstinspires.ftc.teamcode.Testing.LimeLight.TurretLimelight;

@Autonomous(name="LimelightAuton Far", group="Autons")

public class AutonLimeLight extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private SampleMecanumDrive drive;
    private double maxPower = 1.0;
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private DcMotorEx launcher;
    private DcMotor intake;
    private TurretLimelight turret;

    private double farTps = 1000;
    private AlianceColorSyncTool as;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        as = new AlianceColorSyncTool(hardwareMap, telemetry, gamepad1);

        frontRight = hardwareMap.get(DcMotor.class, "leftBack");//Port 3 was RF
        frontLeft = hardwareMap.get(DcMotor.class, "rightBack");//Port 0 was LF
        backRight = hardwareMap.get(DcMotor.class, "leftFront");//Port2 was RB
        backLeft = hardwareMap.get(DcMotor.class, "rightFront");//Port 1 was LB
        turret = new TurretLimelight(hardwareMap, telemetry, gamepad2);

        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotor.class,"intake");// Port 0
        intake.setDirection(DcMotorSimple.Direction.FORWARD);


        launcher = hardwareMap.get(DcMotorEx.class,"launcher");// Port 2
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);



        telemetry.addData("Status", "Initializing");

        drive = new SampleMecanumDrive(hardwareMap);


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
        launcher.setVelocity(farTps);
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        turret.update();
        if(runtime.seconds() < 14){
            launcher.setVelocity(farTps);
        }
        if (runtime.seconds() > 19 && runtime.seconds() < 20){
            frontLeft.setPower (.5);
            frontRight.setPower (.5);
            backRight.setPower (.5);
            backLeft.setPower (.5);
        }
        if (runtime.seconds() > 14 && runtime.seconds() < 19 && turret.state.equals(TurretLimelight.STATE.FOUND)){
            intake.setPower(maxPower);
        }
        if (runtime.seconds() > 20 && runtime.seconds() < 20.5){
//            frontLeft.setPower(.5);
//            frontRight.setPower(.5);
//            backRight.setPower(-.5);
//            backLeft.setPower(-.5);
            launcher.setVelocity(0);
            intake.setPower(0);
        }
        if (runtime.seconds() > 20.6){
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backRight.setPower(0);
            backLeft.setPower(0);
        }



//        if(runtime.seconds() < 0.5 && !cameraState.equals(SensorLimelight3A.STATE.FOUND)){
//            frontLeft.setPower (-.5);
//            frontRight.setPower (-.5);
//            backRight.setPower (-.5);
//            backLeft.setPower (-.5);
//        }
//        if(cameraState.equals(SensorLimelight3A.STATE.FOUND) || runtime.seconds() > 0.5 && runtime.seconds() < 5){
//            intake.setPower(1);
//        }
//        if(runtime.seconds() > 5 && runtime.seconds() < 5.5) {
//            intake.setPower(0);
//            launcher.setVelocity(0);
//            frontLeft.setPower(-.5);
//            frontRight.setPower(-.5);
//            backLeft.setPower(.5);
//            backLeft.setPower(.5);
//        }
//        if (runtime.seconds() > 5.5){
//            frontLeft.setPower(0);
//            frontRight.setPower(0);
//            backLeft.setPower(0);
//            backLeft.setPower(0);
//        }
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }


    }

    /*
     * Code to run ONCE after the driver hits STOP
     */

