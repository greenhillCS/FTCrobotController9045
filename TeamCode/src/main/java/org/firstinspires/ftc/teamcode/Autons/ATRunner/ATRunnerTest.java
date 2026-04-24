package org.firstinspires.ftc.teamcode.Autons.ATRunner;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutonAssets.drive.PositionStorage;
import org.firstinspires.ftc.teamcode.AutonAssets.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.AutonAssets.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.Testing.LimeLight.TurretTest;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.IntakeToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.LauncherToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.TurretLimelight;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.v;

@Autonomous(name="ATRunner Test", group="ATRunner")

public class ATRunnerTest extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private ATRunnerBasic atr;
    private TurretLimelight turret;
    private LauncherToolClass launcher;
    private IntakeToolClass intake;
    private int m = -1; // 1 for Red, -1 for Blue
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        atr = new ATRunnerBasic(hardwareMap, telemetry, gamepad1);
        atr.addPoint(70, 0 * m, 0 * m, 20, 3, 1);
        atr.addPoint(70, 0 * m, 0 * m, 20, 6, 1);
        atr.addPoint(110, -50 * m, 0 * m, 20, 0, 1);
        atr.addPoint(65, -50 * m, 0 * m, 20, 0, 1);
        atr.addPoint(70, 0 * m, 0 * m, 20, 6, 1);
        atr.addPoint(70, -20 * m, 0 * m, 20, 2, 1);

        turret = new TurretLimelight(hardwareMap, telemetry, gamepad1);
        launcher = new LauncherToolClass(hardwareMap, telemetry, gamepad1);
        intake = new IntakeToolClass(hardwareMap, telemetry, gamepad1);

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
        runtime.reset();
        atr.init();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
//        turret.update();
        atr.update();
        launcher.update();
        intake.update();
        if(atr.state.equals(ATRunnerBasic.STATE.WAITING)){
            switch(atr.pointIndex){
                case 0:
                    launcher.setState(v.STATE.CLOSED);
                    break;
                case 1, 4:
                    intake.setState(v.STATE.IN);
                    launcher.launch = true;
                    break;
                case 2, 3, 5:
                    launcher.launch = false;
                    launcher.setState(v.STATE.CLOSED);
                    break;
            }
        }
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

}
