package org.firstinspires.ftc.teamcode.Autons.ATRunner;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Tools.ATRunner.IntakeToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.LauncherToolClass;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.TurretLimelight;
import org.firstinspires.ftc.teamcode.Tools.ATRunner.v;

@Autonomous(name="ATRunner Test Red", group="ATRunner")
@Disabled
public class ATRunnerTestRed extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private ATRunnerBasic atr;
    private TurretLimelight turret;
    private LauncherToolClass launcher;
    private IntakeToolClass intake;
    private final int launchDist = 65;
    private final double launchWait = 4;
    private final double launchYaw = -10;
    private int m = 1; // 1 for Red, -1 for Blue
    private int id = 24;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        atr = new ATRunnerBasic(hardwareMap, telemetry, gamepad1);
        atr.addPoint(launchDist, launchYaw * m, 0 * m, id, 2.5, 1);//0
        atr.addPoint(launchDist, launchYaw * m, 0 * m, id, launchWait, 1);//1
        atr.addPoint(110, -48 * m, 0 * m, id, 0, 1);//2
        atr.addPoint(80, -48 * m, 0 * m, id, 0, 1);//3
        atr.addPoint(launchDist, launchYaw * m, 0 * m, id, 0, 1);//4
        atr.addPoint(launchDist, launchYaw * m, 0 * m, id, launchWait, 1);//5
        atr.addPoint(launchDist, -40 * m, 0 * m, id, 0, 1);//6

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
        launcher.update();
        intake.update();
        turret.update();
        if(atr.state.equals(ATRunnerBasic.STATE.WAITING)){
            switch(atr.pointIndex){
                case 0:
                    break;
                case 1, 5:
                    launcher.launch = true;
                    break;
                case 2, 3, 4:
                    launcher.launch = false;
                    break;
                case 6:
                    launcher.launch = false;
                    intake.setState(v.STATE.STOP);
                    launcher.setState(v.STATE.STOP);
            }
        }else if(atr.state.equals(ATRunnerBasic.STATE.MOVING)){
            switch(atr.pointIndex){
                case 2, 3, 4:
                    launcher.launch = false;
                    break;
                case 6:
                    intake.setState(v.STATE.STOP);
                    launcher.setState(v.STATE.STOP);
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
