package com.example.meepmeeptesting.Testing.JohnnyFunTime;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import org.rowlandhall.meepmeep.core.colorscheme.scheme.ColorSchemeBlueLight;
import org.rowlandhall.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import org.rowlandhall.meepmeep.core.colorscheme.scheme.ColorSchemeRedLight;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestDiscoverPath4x {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        // The starting position of the robot, The axis go from the center-0, 0-and go 72 inches outward in each direction
        double depth = 48;
        double launchTime = 1;

        Pose2d parkPoseTop = new Pose2d(37.75, -31.75, Math.toRadians(90));
        Pose2d parkPoseBottom = new Pose2d(37.75, 33.75, Math.toRadians(-90));

        Pose2d startPoseRed1 = new Pose2d(72-10, 36, Math.toRadians(180));
        Pose2d endPoseRed1 = new Pose2d(60, 36, Math.toRadians(180));
        Pose2d focalPoseRed1 = new Pose2d(-16, 16, Math.toRadians(135));

        Pose2d startPoseRed2 = new Pose2d(-47, 47, Math.toRadians(135));
        Vector2d endVectorRed2 = new Vector2d(60, 12);
        double endAngleRed2 = Math.toRadians(0);
        Pose2d focalPoseRed2 = new Pose2d(-40, 36, Math.toRadians(135));

        Pose2d startPoseBlue1 = new Pose2d(72-10, -36, Math.toRadians(180));
        Pose2d endPoseBlue1 = new Pose2d(60, -36, Math.toRadians(180));
        Pose2d focalPoseBlue1 = new Pose2d(-16, -16, Math.toRadians(-135));

        Pose2d startPoseBlue2 = new Pose2d(-47, -47, Math.toRadians(-135));
        Vector2d endVectorBlue2 = new Vector2d(60, -12);
        double endAngleBlue2 = Math.toRadians(0);
        Pose2d focalPoseBlue2 = new Pose2d(-40, -36, Math.toRadians(-135));

        RoadRunnerBotEntity myBotRed1 = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setColorScheme(new ColorSchemeRedDark())
                .followTrajectorySequence(drive ->
                    drive.trajectorySequenceBuilder(startPoseRed1)
                            //Put trajectories here
                            .splineToLinearHeading(focalPoseRed1, Math.toRadians(180))

                            .waitSeconds(launchTime)

                            .splineTo(new Vector2d(-12, depth/2), Math.toRadians(90))
                            .splineTo(new Vector2d(-12, depth), Math.toRadians(90))
                            .lineToSplineHeading(focalPoseRed1)

                            .waitSeconds(launchTime)

                            .splineTo(new Vector2d(12, depth/2), Math.toRadians(90))
                            .splineTo(new Vector2d(12, depth), Math.toRadians(90))
                            .lineToSplineHeading(focalPoseRed1)

                            .waitSeconds(launchTime)

                            .splineTo(new Vector2d(36, depth/2), Math.toRadians(90))
                            .splineTo(new Vector2d(36, depth), Math.toRadians(90))
                            .lineToSplineHeading(focalPoseRed1)

                            .waitSeconds(launchTime)

                            .lineToSplineHeading(endPoseRed1)
                            .waitSeconds(5.05)
                            .build()
                );
        RoadRunnerBotEntity myBotRed2 = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setColorScheme(new ColorSchemeRedLight())
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPoseRed2)
                                //Put trajectories here
                                .waitSeconds(launchTime)

                                .lineToSplineHeading(focalPoseRed2)

                                .waitSeconds(launchTime)
                                .waitSeconds(6.5)

                                .setReversed(true)
                                .splineTo(new Vector2d(-36, 0), Math.toRadians(0))
                                .splineTo(endVectorRed2, endAngleRed2)
                                .waitSeconds(16.27)
                                .build()
                );
        RoadRunnerBotEntity myBotBlue1 = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setColorScheme(new ColorSchemeBlueDark())
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPoseBlue1)
                                //Put trajectories here
                                .splineToLinearHeading(focalPoseBlue1, Math.toRadians(180))

                                .waitSeconds(launchTime)

                                .splineTo(new Vector2d(-12, -depth/2), Math.toRadians(-90))
                                .splineTo(new Vector2d(-12, -depth), Math.toRadians(-90))
                                .lineToSplineHeading(focalPoseBlue1)

                                .waitSeconds(launchTime)

                                .splineTo(new Vector2d(12, -depth/2), Math.toRadians(-90))
                                .splineTo(new Vector2d(12, -depth), Math.toRadians(-90))
                                .lineToSplineHeading(focalPoseBlue1)

                                .waitSeconds(launchTime)

                                .splineTo(new Vector2d(36, -depth/2), Math.toRadians(-90))
                                .splineTo(new Vector2d(36, -depth), Math.toRadians(-90))
                                .lineToSplineHeading(focalPoseBlue1)

                                .waitSeconds(launchTime)

                                .lineToSplineHeading(endPoseBlue1)
                                .waitSeconds(5.05)
                                .build()
                );
        RoadRunnerBotEntity myBotBlue2 = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setColorScheme(new ColorSchemeBlueLight())
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPoseBlue2)
                                //Put trajectories here
                                .waitSeconds(launchTime)

                                .lineToSplineHeading(focalPoseBlue2)

                                .waitSeconds(launchTime)

                                .setReversed(true)
                                .splineTo(new Vector2d(-36, 0), Math.toRadians(0))
                                .splineTo(endVectorBlue2, endAngleBlue2)
                                .waitSeconds(22.77)
                                .build()
                );

        String path = System.getProperty("user.dir");
        File parent = new File(path);
        Image img = null;
        try { img = ImageIO.read(new File(parent + "\\MeepMeepTesting\\src\\main\\java\\com\\example\\meepmeeptesting\\Testing\\field-2025-juice-dark.png"));}
        catch(IOException e) {}

        meepMeep.setBackground(img)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBotRed1)
                .addEntity(myBotRed2)
                .addEntity(myBotBlue1)
                .addEntity(myBotBlue2)
                .start();
    }
}