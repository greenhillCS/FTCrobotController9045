package com.example.meepmeeptesting.Testing.JohnnyFunTime;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import org.rowlandhall.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestDiscoverPath {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        // The starting position of the robot, The axis go from the center-0, 0-and go 72 inches outward in each direction
        double depth = 48;
        double launchTime = 1;

        Pose2d startPose = new Pose2d(72-10, 36, Math.toRadians(180));
        Pose2d endPose = new Pose2d(60, 36, Math.toRadians(180));
        Pose2d focalPose = new Pose2d(-16, 16, Math.toRadians(135));

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .setColorScheme(new ColorSchemeRedDark())
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(startPose)
                            //Put trajectories here
                            .splineToLinearHeading(focalPose, Math.toRadians(180))

                            .waitSeconds(launchTime)

                            .splineTo(new Vector2d(-12, depth/2), Math.toRadians(90))
                            .splineTo(new Vector2d(-12, depth), Math.toRadians(90))
                            .lineToSplineHeading(focalPose)

                            .waitSeconds(launchTime)

                            .splineTo(new Vector2d(12, depth/2), Math.toRadians(90))
                            .splineTo(new Vector2d(12, depth), Math.toRadians(90))
                            .lineToSplineHeading(focalPose)

                            .waitSeconds(launchTime)

                            .splineTo(new Vector2d(36, depth/2), Math.toRadians(90))
                            .splineTo(new Vector2d(36, depth), Math.toRadians(90))
                            .lineToSplineHeading(focalPose)

                            .waitSeconds(launchTime)

                            .lineToSplineHeading(endPose)
                            .waitSeconds(5.05)
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
                .addEntity(myBot)
                .start();
    }
}