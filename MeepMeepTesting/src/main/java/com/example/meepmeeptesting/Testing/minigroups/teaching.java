package com.example.meepmeeptesting.Testing.minigroups;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class teaching {
    public static void main(String[] args) {
        double difference = 54.5; // difference between peak and trough 54.5
        double peak = 55; // How far the farthest point is from the center 55
        double trough = peak - difference; // How close the closest point is from the center
        double divisions = 1.01; // Increase to make the middle hump smaller, and decrease to do the opposite 1.01
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(0, 0, 0))
                        .lineTo(new Vector2d(0, 55))
                        .lineTo(new Vector2d(40, 55))
                        .lineTo(new Vector2d(40, -55))
                        .lineTo(new Vector2d(-40, -55))
                        .lineTo(new Vector2d(-40, 55))
                        .lineTo(new Vector2d(0, 55))
                        .build());

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
