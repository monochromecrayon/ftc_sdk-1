package com.qualcomm.ftcrobotcontroller.opmodes;

 import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class RR10TankDrive extends OpMode  {
    DcMotor motorTopRight;
    DcMotor motorTopLeft;
    DcMotor motorBotRight;
    DcMotor motorBotLeft;

    public RR10TankDrive() {}

    public void start() {
        motorTopRight = hardwareMap.dcMotor.get("motor_1");
        motorBotRight = hardwareMap.dcMotor.get("motor_2");
        motorTopLeft = hardwareMap.dcMotor.get("motor_3");
        motorBotLeft = hardwareMap.dcMotor.get("motor_4");

        motorTopLeft.setDirection(DcMotor.Direction.REVERSE);
        motorBotLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    public void run() {
        float left = -gamepad1.left_stick_y;
        float right = -gamepad1.right_stick_y;

        right = Range.clip(right, -1, 1);
        left = Range.clip(left, -1, 1);

        right = (float)scaleInput(right);
        left =  (float)scaleInput(left);

        motorTopRight.setPower(right);
        motorBotRight.setPower(right);
        motorTopLeft.setPower(left);
        motorBotLeft.setPower(left);

        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("left tgt pwr", "left  pwr: " + Float.toString(left));
        telemetry.addData("right tgt pwr", "right pwr: " + Float.toString(right));
    }

    public void stop(){}

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        int index = (int) (dVal * 16.0);
        if (index < 0) {
            index = -index;
        }

        else if (index > 16) {
            index = 16;
        }

        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        }

        else {
            dScale = scaleArray[index];
        }

        return dScale;
    }
}

