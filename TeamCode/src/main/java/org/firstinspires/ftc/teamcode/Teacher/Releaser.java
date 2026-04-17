package org.firstinspires.ftc.teamcode.Teacher;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Releaser {
    private final Servo release;

    public Releaser(HardwareMap hardwareMap){
        release = hardwareMap.get(Servo.class, "release");
        this.setHold();
    }

    public void setHold(){
        double holdBalls = 1;
        release.setPosition(holdBalls);
    }
    public void setRelease(){
        double releaseBalls = 0.5;
        release.setPosition(releaseBalls);
    }
}
