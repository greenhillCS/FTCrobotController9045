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

package org.firstinspires.ftc.teamcode.Testing.NewToolsNovDec;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.AutonAssets.drive.PatternStorage;
import org.firstinspires.ftc.teamcode.Testing.LauncherTest.ArtifactTrajectory;
import org.firstinspires.ftc.teamcode.Testing.Location_Gabe_Johnny_Sammie.AprilTagToolClassSingleVision;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

/*
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 */
//TODO:Uncomment one of the following and rename group and name as needed.
@TeleOp(name="April Tag Stuff", group="Testing")
//@Autonomous(name="Change the name of your Auton", group="zzzzz")

public class AprilTagDistance extends OpMode
{

    private AprilTagToolClass aprilTagToolClass;
    private ArtifactTrajectory artifactTrajectory;
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor shooterLeft;
    private DcMotor shooterRight;
    private Servo flicker;
    double distance = 6.7;
    double omega_w0 = 200*Math.PI;
    double angle = 45.0;
    double yTarget;

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");
        aprilTagToolClass = new AprilTagToolClass(hardwareMap, telemetry, gamepad1);
        artifactTrajectory = new ArtifactTrajectory(omega_w0);
        telemetry.addData("Status", "Initialized");
        aprilTagToolClass.worm_Gear.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterLeft = hardwareMap.get(DcMotor.class, "shooterLeft");
        shooterRight = hardwareMap.get(DcMotor.class, "shooterRight");
        shooterLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        flicker = hardwareMap.get(Servo.class, "flicker");
        flicker.setPosition(0.5);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {


    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {

        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        distance = aprilTagToolClass.getDistance();
        angle = artifactTrajectory.solveForAngle(distance*0.0254+0.1524,46.0);
        telemetry.addData("Distance: ", distance);
        telemetry.addData("Angle: ", angle);
        aprilTagToolClass.worm_Gear.setTargetPosition((int) ((384.5/360)*angle));
        aprilTagToolClass.worm_Gear.setPower(0.5);
        telemetry.addData("ticks to run to: ", (int) ((384.5/360)*angle));
        if (gamepad1.a)  {
            shooterLeft.setPower(1);
            shooterRight.setPower(1);
        }
        shooterLeft.setPower(0);
        shooterRight.setPower(0);
        if (gamepad1.y) {
            flicker.setPosition(1);
        }
        if (gamepad1.x) {
            flicker.setPosition(0);
        }

    }
    //SanaysFunction.get angle (x distance, y distance)


    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
