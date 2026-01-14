package org.firstinspires.ftc.teamcode.Testing.AlianceColor;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;


import org.firstinspires.ftc.robotcore.external.Telemetry;

public class AlianceColorSyncTool {
    //Define variables here
    HardwareMap hardwareMap;
    Telemetry telemetry;
    Gamepad gamepad;
    private static String selectedColor = "None";

    private boolean buttonXPressed = false;
    private boolean buttonYPressed = false;


    public AlianceColorSyncTool(HardwareMap h, Telemetry t, Gamepad g){
        //Initialize devices and other variables here
        hardwareMap = h;
        telemetry = t;
        gamepad = g;


    }

    public void update(){
        //Logic that goes in the loop goes here
        buttonXPressed = gamepad.x;
        buttonYPressed = gamepad.y;
        // Gamepad
        if (gamepad.x && !buttonXPressed) {
            if (selectedColor.equals("Blue")) {
                selectedColor = "None";
            } else {
                selectedColor = "Blue";
            }
        }

        if (gamepad.y && !buttonYPressed) {
            if (selectedColor.equals("Red")) {
                selectedColor = "None";
            } else {
                selectedColor = "Red";
            }
        }
        telemetry.addData("ColorStored", selectedColor);
    }

    public static String getSelectedColor() {
        return selectedColor;
    }
//    public static void setSelectedColor(String color) {
//        selectedColor = color;
//    }
}

