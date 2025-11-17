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

public class SorterToolClass {
    private static int colorPosistion1;
    private static int colorPosistion2;
    private static int colorPosistion3;
    private static int newColorPosistion1;
    private static int newColorPosistion2;
    private static int newColorPosistion3;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private NormalizedColorSensor ballColor;
    private NormalizedRGBA colors;
    final float[] hsvValue = new float[3];

    public SorterToolClass(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        ballColor = hardwareMap.get(NormalizedColorSensor.class,"sensor_color");
    }
public void Update(){
    colors = ballColor.getNormalizedColors();
    Color.colorToHSV(colors.toColor(), hsvValue);

    //sets the first posistion of intake as green, purple or not there
                   if (hsvValue[0] <= 165 && hsvValue[0] >= 145) {
                       colorPosistion1 = 1;
                   } else if (210 <= hsvValue[0] && hsvValue[0] <= 240) {
                       colorPosistion1 = 2;
                   } else {
                       colorPosistion1 = 0;
                   }

 /*                  if (colorPosistion2 == (ball needed)){
                       shoot();
                   } else{
                       rotate();
                   }*/

                   //Cycles the balls to the next position
                   colorPosistion1 = newColorPosistion1;
                   colorPosistion2 = newColorPosistion2;
                   colorPosistion3 = newColorPosistion3;

                   newColorPosistion1 = colorPosistion2;
                   newColorPosistion2 = colorPosistion3;
                   newColorPosistion3 = colorPosistion1;

    telemetry.addData("Positions: ", colorPosistion1 + ", " + colorPosistion2 + ", " + colorPosistion3);
    telemetry.addData("HsvValue: : ", hsvValue[0]);
}
public void rotate(){}
    public void shoot(){}
}
