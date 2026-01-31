package org.firstinspires.ftc.teamcode.Testing.LimeLight;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
public class LimelightBotPos {
private double hight = 43.5;


    private Limelight3A limelight;
    LLResult result = limelight.getLatestResult();
    private double angleOfset = result.getTx(); // How far left or right the target is (degrees)
    private double distance = hight / (double) Math.tan(result.getTy()); // distance the bot is away from apriltag


    public double botPosX() {
        return Math.cos(angleOfset) * distance; // x-coordinates of bot
    }
    public double botPosY(){
        return Math.sin(angleOfset) * distance; // y-coordinates of bot
    }
}