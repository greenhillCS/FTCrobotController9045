package org.firstinspires.ftc.teamcode.Testing.LauncherTest;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LauncherAngleNew{

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor shooter;
    private Servo angleServo;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;
    private double angleScale = 0.01;
    //range of actual shooter is 20-60

    public LauncherAngleNew(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        shooter = h.get(DcMotor.class, "shooter");
        angleServo = h.get(Servo.class, "angleServo");
    }

    public double getShooterRefAngle() {
        telemetry.addData("Servo Angle", (angleServo.getPosition()*40)+20);
        return (angleServo.getPosition()*40)+20;

    }

    public void update(){
        angleServo.setPosition(angleServo.getPosition()+(gamepad.right_stick_y*angleScale));
        telemetry.addData("Servo Position", angleServo.getPosition());
        telemetry.update();
    }
}
