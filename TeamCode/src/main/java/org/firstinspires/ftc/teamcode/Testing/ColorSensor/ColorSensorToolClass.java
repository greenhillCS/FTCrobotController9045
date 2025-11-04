package org.firstinspires.ftc.teamcode.Testing.ColorSensor;

import android.graphics.Color;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.teamcode.AutonAssets.drive.PatternStorage;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

/*
1. Make a function to find out the color of the ball in each slot and assign that to an array (Look
up Enumerators for this)
2. Test this code using the TeleOp by calling said function in the update function and displaying
its output using telemetry
3. Create a function that Returns what index in said array contains a given color
4. Test this by adding some if statements for the A and B buttons on the gamepad, when A is pressed
find the index of a purple ball, when B is pressed find the index of a green ball and display this
output using telemetry.
 */

public class ColorSensorToolClass {
    //private VisionPortal visionPortal;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private NormalizedColorSensor ballColor;
    private NormalizedRGBA colors;
    final float[] hsvValue = new float[3];




    public ColorSensorToolClass(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        ballColor = hardwareMap.get(NormalizedColorSensor.class,"sensor_color");
//        ((SwitchableLight)ballColor).enableLight(true);



    }
public void update() {
colors = ballColor.getNormalizedColors();
    Color.colorToHSV(colors.toColor(), hsvValue);

    telemetry.addLine()
            .addData("Red", "%.3f", colors.red)
            .addData("Green", "%.3f", colors.green)
            .addData("Blue", "%.3f", colors.blue);
    telemetry.addLine()
            .addData("Hue", "%.3f", hsvValue[0])
            .addData("Saturation", "%.3f", hsvValue[1])
            .addData("Value", "%.3f", hsvValue[2]);
    telemetry.addData("Alpha", "%.3f", colors.alpha);

    //Purple: Hue 225 (210-240)
    //Green:  Hue 155 (145-165) idk why but green is more accurate

}







}