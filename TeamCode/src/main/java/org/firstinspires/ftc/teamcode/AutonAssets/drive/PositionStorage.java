package org.firstinspires.ftc.teamcode.AutonAssets.drive;

import com.acmerobotics.roadrunner.geometry.Pose2d;

public class PositionStorage {
    static Pose2d pose = new Pose2d();
    public static void store(Pose2d p){
        pose = p;
    }
    public static Pose2d get(){
        return pose;
    }
}
