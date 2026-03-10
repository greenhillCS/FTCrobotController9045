package org.firstinspires.ftc.teamcode.Testing.Sorter;
/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import android.graphics.Color;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Testing.ColorSensor.ColorSensorToolClass;
// import org.firstinspires.ftc.teamcode.Testing.normalkidscode.flickerNew;
// import org.firstinspires.ftc.teamcode.Testing.normalkidscode.shooterNew;

public class SorterToolClass {
    private static int[] newColor = new int[3];
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private NormalizedColorSensor ballColor;
    private int index;
    private boolean isPressed = false;
    private NormalizedRGBA colors;
    final float[] hsvValue = new float[3];

    public SorterToolClass(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        ballColor = hardwareMap.get(NormalizedColorSensor.class,"sensor_color");
    }

    public SorterToolClass(int index){
        this.index = index;
    }
   public void setIndex(){
        index++;
   }
   /* flickerNew flicker;
    shooterNew shooter;
    boolean wantsToShoot;
    SorterRotateToolClass rotate; */
public void Update(){
    if (gamepad.crossWasPressed()){
        isPressed = true;
    }else{
        isPressed = false;
    }
    colors = ballColor.getNormalizedColors();
    Color.colorToHSV(colors.toColor(), hsvValue);

    //sets the first posistion of intake as green, purple or not there
                   if (hsvValue[0] <= 165 && hsvValue[0] >= 145) {
                       newColor[index] = 2;
                     //  rotate.update();
                   } else if (210 <= hsvValue[0] && hsvValue[0] <= 240) {
                       newColor[index] = 1;
                    //   rotate.update();
                   }
     /*              else if(wantsToShoot){
                       flicker.update();
                       shooter.update();
                       rotate.update();
                       flicker.update();
                       shooter.update();
                       rotate.update();
                       flicker.update();
                       shooter.update();
                   } */
                   else {
                       newColor[index] = 0;
                   }
                   if (gamepad.crossWasPressed()){
                       index++;
                       telemetry.addData("If Gamepad working: ", "True");
                   } else if(!isPressed){
                       telemetry.addData("If Gamepad working: ", "False");
                   }

    //Cycles the balls to the next position
    telemetry.addData("Positions: ", newColor[0] + ", " + newColor[1] + ", " + newColor[2]);
    telemetry.addData("HsvValue: : ", hsvValue[0]);
    telemetry.addData("Index: ", index);

}
}
