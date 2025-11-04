package org.firstinspires.ftc.teamcode.Autons;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutonAssets.drive.PositionStorage;
import org.firstinspires.ftc.teamcode.AutonAssets.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.AutonAssets.trajectorysequence.TrajectorySequence;

@Autonomous(name="Go Forward 24 Inches", group="A_Decode")

public class GoForward24Inches extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private SampleMecanumDrive drive;
    private Pose2d startPose;
    private TrajectorySequence trajectory;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        startPose = new Pose2d(72-(18.0/2), 12, Math.toRadians(180));
        drive = new SampleMecanumDrive(hardwareMap);

        drive.setPoseEstimate(startPose);

        trajectory = drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                .forward(24)
                .build();

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
        drive.followTrajectorySequenceAsync(trajectory);
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        drive.update();
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        PositionStorage.store(trajectory.end());
    }

}
