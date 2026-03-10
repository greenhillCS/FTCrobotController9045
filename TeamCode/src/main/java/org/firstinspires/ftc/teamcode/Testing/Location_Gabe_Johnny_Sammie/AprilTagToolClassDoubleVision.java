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

public class AprilTagToolClassDoubleVision {
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;

    public Pose2d position;
    private AprilTagProcessor aprilTagBack;
    private VisionPortal visionPortalBack;

    public Pose2d positionBack;
    public Pose2d positionFinal;
    private int[] excludes = {21, 22, 23};
    private int[] viewIds;

    public AprilTagToolClassDoubleVision(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;

        viewIds = VisionPortal.makeMultiPortalView(2, VisionPortal.MultiPortalLayout.VERTICAL);

        aprilTag = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(CameraName.class,"Camera"));
        builder.setLiveViewContainerId(viewIds[0]);
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();

        aprilTagBack = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builderBack = new VisionPortal.Builder();
        builderBack.setCamera(hardwareMap.get(CameraName.class,"CameraBack"));
        builder.setLiveViewContainerId(viewIds[1]);
        builderBack.addProcessor(aprilTagBack);
        visionPortalBack = builder.build();
    }
public Pose2d update(){
  List<AprilTagDetection> detections = aprilTag.getDetections();
  List<AprilTagDetection> detectionsBack = aprilTagBack.getDetections();
  int nonMonoDetections = 0;
  telemetry.addData("Number Of Detections", detections.size());
  position = new Pose2d();
  positionBack = new Pose2d();
  if (detections.size()+detectionsBack.size() == 0){
      return null;
  }
  for (AprilTagDetection detection : detections) {
      telemetry.addData("ID",String.format("%s: %s", detection.id, detection.metadata.name));
//      telemetry.addData("Robot Pos X", detection.robotPose.getPosition().x);
//      telemetry.addData("Robot Pos Y", detection.robotPose.getPosition().y);
//      telemetry.addData("Robot Angle Yaw", detection.robotPose.getOrientation().getYaw());

      //position = new Pose2d(detection.robotPose.getPosition().x, detection.robotPose.getPosition().y,detection.robotPose.getOrientation().getYaw());
      if (detection.id != 21 && detection.id != 22 && detection.id != 23) {
          nonMonoDetections++;
          position = new Pose2d(position.getX() + detection.robotPose.getPosition().x, position.getY() + detection.robotPose.getPosition().y, position.getHeading() + detection.robotPose.getOrientation().getYaw());
      }
  }
  position = new Pose2d( position.getX()/nonMonoDetections, position.getY()/nonMonoDetections, position.getHeading()/nonMonoDetections);
    nonMonoDetections = 0;

    for (AprilTagDetection detectionBack : detectionsBack) {
        telemetry.addData("ID",String.format("%s: %s", detectionBack.id, detectionBack.metadata.name));
        //telemetry.addData("Robot Pos X", detection.robotPose.getPosition().x);
        //telemetry.addData("Robot Pos Y", detection.robotPose.getPosition().y);
        //telemetry.addData("Robot Angle Yaw", detection.robotPose.getOrientation().getYaw());
        if (detectionBack.id != 21 && detectionBack.id != 22 && detectionBack.id != 23){
            nonMonoDetections ++;
            positionBack = new Pose2d( positionBack.getX()+detectionBack.robotPose.getPosition().x, positionBack.getY()+detectionBack.robotPose.getPosition().y, positionBack.getHeading() + detectionBack.robotPose.getOrientation().getYaw());
        }




    }
    positionBack = new Pose2d( positionBack.getX()/nonMonoDetections, positionBack.getY()/nonMonoDetections, positionBack.getHeading()/nonMonoDetections + 180);

    positionFinal = new Pose2d((positionBack.getX()+position.getX())/2, (positionBack.getY()+position.getY())/2, (positionBack.getHeading() + position.getHeading())/2);
    return positionFinal;

  //April Tag IDs: GPP = 21, PGP = 22, PPG = 23,
    // Blue Goal = 20
    // Red Goal = 24

}

}

