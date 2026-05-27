package org.firstinspires.ftc.teamcode.Autons.ATRunner;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Tools.ATRunner.IntakeToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.LauncherToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.TurretLimelight;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.v;

@Autonomous(name="ATRunner Test", group="ATRunner")
@Disabled
public class ATRunnerTest extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private ATRunner atr;
    private TurretLimelight turret;
    private LauncherToolClass launcher;
    private IntakeToolClass intake;
    private final int launchDist = 65;
    private final double launchWait = 4;
    private final double launchYaw = -10;
    private int m = -1; // 1 for Red, -1 for Blue
    private int id = 20;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        turret = new TurretLimelight(hardwareMap, telemetry, gamepad1);
        launcher = new LauncherToolClass(hardwareMap, telemetry, gamepad1);
        intake = new IntakeToolClass(hardwareMap, telemetry, gamepad1);

        atr = new ATRunner(hardwareMap, telemetry, gamepad1, turret);
        atr.addPoint(100, 100, 100, 24);

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
        intake.setState(v.STATE.IN);
        launcher.setState(v.STATE.OUT);
        turret.locked = true;
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
//        turret.update();
        atr.update();
//        launcher.update();
//        intake.update();
        turret.update();
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

    }

}
