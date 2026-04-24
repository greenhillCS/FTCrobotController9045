package org.firstinspires.ftc.teamcode.Autons.ATRunner;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Testing.AlianceColor.AlianceColorSyncTool;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.TurretLimelight;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.IntakeToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.LauncherToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.v;

@Autonomous(name="*Special Close Auton*", group="AAAAAAAAAAAAAAAAAAAAAAAAA")

public class CloseAuton extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private Limelight3A limelight;
    private AlianceColorSyncTool as;
    private DcMotor frontRight;
    private DcMotor frontLeft;
    private DcMotor backRight;
    private DcMotor backLeft;
    private TurretLimelight turret;
    private IntakeToolClass intake;
    private LauncherToolClass launcher;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

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

        intake = new IntakeToolClass(hardwareMap, telemetry, gamepad1);

        launcher = new LauncherToolClass(hardwareMap, telemetry, gamepad1);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking!
        limelight.pipelineSwitch(0); // Switch to pipeline number

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
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        double s = runtime.seconds();

        if(s < 2){
            frontLeft.setPower (-0.5);
            frontRight.setPower (-0.5);
            backRight.setPower (-0.5);
            backLeft.setPower (-0.5);
        }else if (s < 8){
            turret.update();
            launcher.setState(v.STATE.CLOSED);

            frontLeft.setPower (0.0);
            frontRight.setPower (0.0);
            backRight.setPower (0.0);
            backLeft.setPower (0.0);
        }else if (s < 18){
            turret.update();
            intake.setState(v.STATE.IN);
            launcher.launch = true;

            frontLeft.setPower (0.0);
            frontRight.setPower (0.0);
            backRight.setPower (0.0);
            backLeft.setPower (0.0);
        }else if (s < 19){
            intake.setState(v.STATE.STOP);
            launcher.setState(v.STATE.STOP);

            frontLeft.setPower (-0.5);
            frontRight.setPower (-0.5);
            backRight.setPower (-0.5);
            backLeft.setPower (-0.5);
        }else{
            intake.setState(v.STATE.STOP);
            launcher.setState(v.STATE.STOP);

            frontLeft.setPower (0.0);
            frontRight.setPower (0.0);
            backRight.setPower (0.0);
            backLeft.setPower (0.0);
        }

        intake.update();
        launcher.update();
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

}
