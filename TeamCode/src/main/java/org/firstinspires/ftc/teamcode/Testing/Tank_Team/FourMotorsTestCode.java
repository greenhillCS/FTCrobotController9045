package org.firstinspires.ftc.teamcode.Testing.Tank_Team;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Four Motors Continuous Spin Test", group="Testing")
public class FourMotorsTestCode extends OpMode {

    // Motors
    private DcMotor intakeMotor;
    private DcMotor pusherMotor;
    private DcMotor shooterMotorLeft;
    private DcMotor shooterMotorRight;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        // Hardware map names MUST match the Robot Controller config
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");
        pusherMotor = hardwareMap.get(DcMotor.class, "pusher");
        shooterMotorLeft = hardwareMap.get(DcMotor.class, "shooterLeft");
        shooterMotorRight = hardwareMap.get(DcMotor.class, "shooterRight");

        // Reverse one shooter motor so they spin opposite directions
        shooterMotorLeft.setDirection(DcMotor.Direction.FORWARD);
        shooterMotorRight.setDirection(DcMotor.Direction.REVERSE);

        // Optional but recommended
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        pusherMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        runtime.reset();

        // Continuous spinning powers
        intakeMotor.setPower(1.0);        // intake speed
        pusherMotor.setPower(1.0);        // wheel pusher speed

        // Shooter motors VERY fast
        shooterMotorLeft.setPower(0.5);
        shooterMotorRight.setPower(0.5);
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Intake Power", intakeMotor.getPower());
        telemetry.addData("Pusher Power", pusherMotor.getPower());
        telemetry.addData("Shooter Left Power", shooterMotorLeft.getPower());
        telemetry.addData("Shooter Right Power", shooterMotorRight.getPower());
    }

    @Override
    public void stop() {
        // Stop everything when OpMode ends
        intakeMotor.setPower(0);
        pusherMotor.setPower(0);
        shooterMotorLeft.setPower(0);
        shooterMotorRight.setPower(0);
    }
}
