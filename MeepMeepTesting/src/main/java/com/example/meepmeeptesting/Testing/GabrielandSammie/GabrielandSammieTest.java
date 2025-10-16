package com.example.meepmeeptesting.Testing.GabrielandSammie;

import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GabrielandSammieTest {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        // The starting position of the robot, The axis go from the center-0, 0-and go 72 inches outward in each direction
        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(0));

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(67.14584, 67.14584, Math.toRadians(180), Math.toRadians(180), 15.35)
                .setColorScheme(new ColorSchemeRedDark())
                .followTrajectorySequence(drive ->
                    drive.trajectorySequenceBuilder(startPose)

                            .splineToLinearHeading(new Pose2d(0, 36, Math.toRadians(0)), Math.toRadians(0))
                            .splineToLinearHeading(new Pose2d(36, 36, Math.toRadians(0)), Math.toRadians(0))
                            .splineToLinearHeading(new Pose2d(36, -36, Math.toRadians(0)), Math.toRadians(0))
                            .splineToLinearHeading(new Pose2d(-36, -36, Math.toRadians(0)), Math.toRadians(0))
                            .splineToLinearHeading(new Pose2d(-36, 36, Math.toRadians(0)), Math.toRadians(0))
                            .splineToLinearHeading(new Pose2d(0, 36, Math.toRadians(0)), Math.toRadians(0))
                            .splineToLinearHeading(new Pose2d(0, 0, Math.toRadians(0)), Math.toRadians(0))
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