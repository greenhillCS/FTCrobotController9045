package org.firstinspires.ftc.teamcode.Teacher;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Shooter {
    private double spinSpeed = 0;
    private final DcMotorEx shooter;

    public Shooter(HardwareMap hardwareMap){
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
    }

    public void setSpinSpeed(double newSpinSpeed){
        spinSpeed = newSpinSpeed;
    }
    public void Update(){
        shooter.setVelocity(spinSpeed);
    }
}
