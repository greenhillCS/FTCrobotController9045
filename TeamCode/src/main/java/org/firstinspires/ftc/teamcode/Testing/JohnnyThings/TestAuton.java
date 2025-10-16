package org.firstinspires.ftc.teamcode.Testing.JohnnyThings;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.AutonAssets.drive.PositionStorage;
import org.firstinspires.ftc.teamcode.AutonAssets.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.AutonAssets.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.Tools.Intake;
import org.firstinspires.ftc.teamcode.Tools.Launcher;

@Autonomous(name="Red Auton", group="Testing")
public class TestAuton extends OpMode {
    // Declare OpMode members.

    private ElapsedTime runtime = new ElapsedTime();
    private SampleMecanumDrive drive;
    private Pose2d startPose;
    private TrajectorySequence trajectory;
    Intake intake;
    Launcher launcher;
    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        double robotLen = 18;
        startPose = new Pose2d(72-(robotLen/2), 36, Math.toRadians(180));
        Pose2d endPose = new Pose2d(72-10, 36, Math.toRadians(90));
        Pose2d launchPose = new Pose2d(-12, 12, Math.toRadians(135));
        launcher = new Launcher(hardwareMap, telemetry, gamepad2);
        intake = new Intake(hardwareMap, telemetry, gamepad2);
        drive = new SampleMecanumDrive(hardwareMap);

        drive.setPoseEstimate(startPose);

        trajectory = drive.trajectorySequenceBuilder(drive.getPoseEstimate())

                .splineToLinearHeading(launchPose, Math.toRadians(180))
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    launcher.out();
                })
                .waitSeconds(2)

                .splineTo(new Vector2d(-12, 24), Math.toRadians(90))
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    intake.in();
                })
                .splineTo(new Vector2d(-12, 48), Math.toRadians(90))
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    intake.stop();
                })


                .lineToSplineHeading(launchPose)


                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    intake.in();
                })
                .UNSTABLE_addTemporalMarkerOffset(0, () -> {
                    launcher.open();
                })



                .UNSTABLE_addTemporalMarkerOffset(1, () -> {
                    launcher.close();
                })
                .UNSTABLE_addTemporalMarkerOffset(1, () -> {
                    intake.stop();
                })



                .UNSTABLE_addTemporalMarkerOffset(2, () -> {
                    launcher.open();
                })
                .UNSTABLE_addTemporalMarkerOffset(2, () -> {
                    intake.in();
                })


                .UNSTABLE_addTemporalMarkerOffset(3, () -> {
                    launcher.close();
                })
                .UNSTABLE_addTemporalMarkerOffset(3, () -> {
                    intake.stop();
                })

                .waitSeconds(3.1)

                .splineTo(new Vector2d(12, 24), Math.toRadians(90))
                .splineTo(new Vector2d(12, 48), Math.toRadians(90))
                .lineToSplineHeading(launchPose)

                .waitSeconds(2)

                .splineTo(new Vector2d(36, 24), Math.toRadians(90))
                .splineTo(new Vector2d(36, 48), Math.toRadians(90))
                .lineToSplineHeading(launchPose)

                .waitSeconds(2)

                .lineToSplineHeading(endPose)
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
