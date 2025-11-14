package org.firstinspires.ftc.teamcode.Testing.normalkidscode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class shooterNew{

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor shooterLeft;
    private DcMotor shooterRight;
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad;

    public shooterNew(HardwareMap h, Telemetry t, Gamepad g){
        hardwareMap = h;
        telemetry = t;
        gamepad = g;
        shooterLeft = h.get(DcMotor.class, "shooterLeft");
        shooterRight = h.get(DcMotor.class, "shooterRight");
        shooterLeft.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void update(){
        if (gamepad.a)  {
            shooterLeft.setPower(1);
            shooterRight.setPower(1);
        }
        shooterLeft.setPower(0);
        shooterRight.setPower(0);
    }
}
