/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * TEST OP Mode
 * <p>
 * USED FOR TESTING ONLY
 */
public class TestOp extends OpMode {

  ElapsedTime timer = new ElapsedTime();

  DcMotor left;
  DcMotor right;

  DcMotor flag;
  DcMotor arm;

  Servo servoA;
  Servo servoB;

  IrSeekerSensor irSeekerSensor;
  LightSensor lightSensor;

  /*
   * Code to run when the op mode is first enabled goes here
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
   */
  @Override
  public void start() {

    DbgLog.msg("****** TEST OP: start");

    left = hardwareMap.dcMotor.get("left");
    right = hardwareMap.dcMotor.get("right");
    right.setDirection(DcMotor.Direction.REVERSE);

    flag = hardwareMap.dcMotor.get("flag");
    arm = hardwareMap.dcMotor.get("arm");

    servoA = hardwareMap.servo.get("a");
    servoB = hardwareMap.servo.get("b");

    irSeekerSensor = hardwareMap.irSeekerSensor.get("ir_seeker");
    lightSensor = hardwareMap.lightSensor.get("light");

    lightSensor.enableLed(true);
  }

  /*
   * This method will be called repeatedly in a loop
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#run()
   */
  @Override
  public void run() {
    servoA.setPosition(gamepad1.left_trigger);
    servoB.setPosition(gamepad1.right_trigger);

    left.setPower(gamepad1.left_stick_y);
    right.setPower(gamepad1.right_stick_y);

    if (gamepad1.x) {
      flag.setPower(0.25);
    } else {
      flag.setPower(0.0);
    }

    if (gamepad1.y) {
      arm.setPower(0.30);
    } else if (gamepad1.a) {
      arm.setPower(-0.30);
    } else {
      arm.setPower(0.0);
    }

    telemetry.addData("servo a", servoA.getPosition());
    telemetry.addData("servo b", servoB.getPosition());
    telemetry.addData("runtime", time);
    telemetry.addData("ir seeker strength", irSeekerSensor.getStrength());
    telemetry.addData("ir seeker angle", irSeekerSensor.getAngle());
    telemetry.addData("light", lightSensor.getLightLevel());
    telemetry.addData("timer", timer.time());

    timer.reset();
  }

  /*
   * Code to run when the op mode is first disabled goes here
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
   */
  @Override
  public void stop() {
    DbgLog.msg("****** TEST OP: stop");
  }

}
