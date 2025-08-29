package com.example.meepmeeptesting.Testing.JohnnyFunTime;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class SquareTest {
    public static void main(String[] args) {
        double difference = 54.5; // difference between peak and trough 54.5
        double peak = 55; // How far the farthest point is from the center 55
        double trough = peak - difference; // How close the closest point is from the center
        double divisions = 1.01; // Increase to make the middle hump smaller, and decrease to do the opposite 1.01
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel 60, maxAccel 60, maxAngVel 180, maxAngAccel 180, track width 15
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(peak, -peak, Math.toRadians(45)))
                .splineTo(new Vector2d(trough, -peak/divisions), Math.toRadians(90))
                .splineTo(new Vector2d(peak, 0), Math.toRadians(90))
                .splineTo(new Vector2d(trough, peak/divisions), Math.toRadians(90))
                .splineTo(new Vector2d(peak, peak), Math.toRadians(135))

                .splineTo(new Vector2d(peak/divisions, trough), Math.toRadians(180))
                .splineTo(new Vector2d(0, peak), Math.toRadians(180))
                .splineTo(new Vector2d(-peak/divisions, trough), Math.toRadians(180))
                .splineTo(new Vector2d(-peak, peak), Math.toRadians(-135))

                .splineTo(new Vector2d(-trough, peak/divisions), Math.toRadians(-90))
                .splineTo(new Vector2d(-peak, 0), Math.toRadians(-90))
                .splineTo(new Vector2d(-trough, -peak/divisions), Math.toRadians(-90))
                .splineTo(new Vector2d(-peak, -peak), Math.toRadians(-45))

                .splineTo(new Vector2d(-peak/divisions, -trough), Math.toRadians(0))
                .splineTo(new Vector2d(0, -peak), Math.toRadians(0))
                .splineTo(new Vector2d(peak/divisions, -trough), Math.toRadians(0))
                .splineTo(new Vector2d(peak, -peak), Math.toRadians(45))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}