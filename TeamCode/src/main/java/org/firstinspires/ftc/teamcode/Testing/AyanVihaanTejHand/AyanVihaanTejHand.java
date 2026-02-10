package org.firstinspires.ftc.teamcode.Testing.AyanVihaanTejHand;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="AyanVihaanTejHand", group="Testing")

public class AyanVihaanTejHand extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private Servo leftRight;
    private Servo upDown;

    private double firstServoPosition = 0.0;
    private double secondServoPosition = 0.0;


    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initializing");

        leftRight = hardwareMap.get(Servo.class, "leftRightServo");

        leftRight.setPosition(firstServoPosition);

        upDown = hardwareMap.get(Servo.class, "upDownServo");

        upDown.setPosition(secondServoPosition);

        telemetry.addData("Status", "Initialized");
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

        if (gamepad1.dpad_up) {
            firstServoPosition += 0.005;
        }


        if (gamepad1.dpad_down) {
            firstServoPosition -= 0.005;
        }

        if (gamepad1.dpad_left) {
            secondServoPosition += 0.005;
        }

        if (gamepad1.dpad_right) {
            secondServoPosition -= 0.005;
        }


        firstServoPosition = Math.max(0.0, Math.min(1.0, firstServoPosition));
        secondServoPosition = Math.max(0.0, Math.min(1.0, secondServoPosition));

        leftRight.setPosition(firstServoPosition);

        upDown.setPosition(secondServoPosition);

        telemetry.addData("Servo Position 1", firstServoPosition);
        telemetry.addData("Servo Position 2", secondServoPosition);
        telemetry.addData("Status", "Run Time: " + runtime.toString());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}
