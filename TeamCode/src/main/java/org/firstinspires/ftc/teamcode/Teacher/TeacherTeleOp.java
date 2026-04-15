/* Copyright (c) 2023 FIRST. All rights reserved.
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

package org.firstinspires.ftc.teamcode.Teacher;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Testing.AlianceColor.AlianceColorSyncTool;

import java.util.HashMap;

@TeleOp(name="Teacher TeleOp 70pts", group = "Main")
public class TeacherTeleOp extends OpMode
{
    TeacherBot bot;
    HashMap<String, Waypoint> waypoints;
    AlianceColorSyncTool ac;

    @Override
    public void init_loop(){
        ac.update();
        telemetry.update();
    }
    @Override
    public void init() {
        // Make Waypoints

        ac = new AlianceColorSyncTool(hardwareMap, telemetry, gamepad1);

        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
    }

    @Override
    public void start() {
        super.start();
        waypoints = Waypoint.makeWaypoints();
        bot = new TeacherBot(hardwareMap, gamepad1, telemetry, waypoints);
        bot.setCurrentWaypoint("shortShoot");
    }

    @Override
    public void loop() {
        if (gamepad1.triangleWasPressed()){
            bot.setCurrentWaypoint("shortShoot");
        }
        if (gamepad1.circleWasPressed()){
            bot.setCurrentWaypoint("mediumShoot");
        }
        if (gamepad1.crossWasPressed()){
            bot.setCurrentWaypoint("longShoot");
        }
        if (gamepad1.squareWasPressed()){
            bot.setCurrentWaypoint("humanDrop");
        }
        if (gamepad1.dpadLeftWasPressed()){
            bot.setCurrentWaypoint("park");
        }
        if (gamepad1.dpadRightWasPressed()){
            bot.setCurrentWaypoint("ballRelease");
        }
        bot.Update();
        telemetry.update();
    }
}
