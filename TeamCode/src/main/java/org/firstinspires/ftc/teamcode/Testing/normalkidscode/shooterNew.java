package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class shooterNew{

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor shooter;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;

    public shooterNew(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        shooter = h.get(DcMotor.class, "shooter");
    }

    public void update(){
        if (gamepad.a)  {
            shooter.setPower(1);
        }
        shooter.setPower(0);
    }
}
