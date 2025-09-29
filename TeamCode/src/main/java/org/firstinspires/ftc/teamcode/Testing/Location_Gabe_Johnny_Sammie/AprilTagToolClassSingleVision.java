package org.firstinspires.ftc.teamcode.Testing.Location_Gabe_Johnny_Sammie;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class AprilTagToolClassSingleVision {
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;

    public Pose2d position;

    public AprilTagToolClassSingleVision(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        aprilTag = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(CameraName.class,"Camera"));
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();
    }
public Pose2d update(){
  List<AprilTagDetection> detections = aprilTag.getDetections();
  telemetry.addData("Number Of Detections", detections.size());
  position = new Pose2d();
  for (AprilTagDetection detection : detections) {
      telemetry.addData("ID",String.format("%s: %s", detection.id, detection.metadata.name));
//      telemetry.addData("Robot Pos X", detection.robotPose.getPosition().x);
//      telemetry.addData("Robot Pos Y", detection.robotPose.getPosition().y);
//      telemetry.addData("Robot Angle Yaw", detection.robotPose.getOrientation().getYaw());

      //position = new Pose2d(detection.robotPose.getPosition().x, detection.robotPose.getPosition().y,detection.robotPose.getOrientation().getYaw());
      position = new Pose2d( position.getX()+detection.robotPose.getPosition().x, position.getY()+detection.robotPose.getPosition().y, position.getHeading() + detection.robotPose.getOrientation().getYaw());

  }
    position = new Pose2d( position.getX()/detections.size(), position.getY()/detections.size(), position.getHeading()/detections.size());

    return position;
  //April Tag IDs: GPP = 21, PGP = 22, PPG = 23,
    // Blue Goal = 20
    // Red Goal = 24

}

}

