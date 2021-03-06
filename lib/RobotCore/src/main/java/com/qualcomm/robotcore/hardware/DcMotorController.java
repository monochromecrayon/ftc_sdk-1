/*
 * Copyright (c) 2014 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.DifferentialControlLoopCoefficients;

/**
 * Interface for working with DC Motor Controllers
 * <p>
 * Different DC motor controllers will implement this interface.
 */
@SuppressWarnings("unused")
public interface DcMotorController {

  public enum RunMode {
    RUN,
    RUN_NO_ENCODER,
    POSITION,
    RESET
  }

  /**
   * Device Name
   *
   * @return device manufacturer and name
   */
  public abstract String getDeviceName();

  /**
   * Version
   *
   * @return get the version of this device
   */
  public abstract int getVersion();

  /**
   * Close this device
   */
  public abstract void close();

  /**
   * Set the current channel mode
   *
   * @param motor port of motor
   * @param mode run mode
   */
  public abstract void setMotorChannelMode(int motor, RunMode mode);

  /**
   * Get the current channel mode
   *
   * @param motor port of motor
   * @return run mode
   */
  public abstract RunMode getMotorChannelMode(int motor);

  /**
   * Set the current motor power
   *
   * @param motor port of motor
   * @param power from -1.0 to 1.0
   */
  public abstract void setMotorPower(int motor, double power);

  /**
   * Get the current motor power
   *
   * @param motor port of motor
   * @return scaled from -1.0 to 1.0
   */
  public abstract double getMotorPower(int motor);

  /**
   * Allow motor to float
   *
   * @param motor port of motor
   */
  public abstract void setMotorPowerFloat(int motor);

  /**
   * Is motor power set to float?
   *
   * @param motor port of motor
   * @return true of motor is set to float
   */
  public abstract boolean getMotorPowerFloat(int motor);

  /**
   * Set the motor target position, where 1.0 is one full rotation
   *
   * Motor power should be positive if using run to position
   *
   * @param motor port of motor
   * @param position range from Integer.MIN_VALUE to Integer.MAX_VALUE
   */
  public abstract void setMotorTargetPosition(int motor, double position);

  /**
   * Get the current motor target position
   *
   * @param motor port of motor
   * @return scaled, where 1.0 is one full rotation
   */
  public abstract double getMotorTargetPosition(int motor);

  /**
   * Get the current motor position
   *
   * @param motor port of motor
   * @return scaled, where 1.0 is one full rotation
   */
  public abstract double getMotorCurrentPosition(int motor);

  /**
   * Set the gear ratio
   *
   * @param motor port of motor
   * @param ratio from -1.0 to 1.0
   */
  public abstract void setGearRatio(int motor, double ratio);

  /**
   * Get the gear ratio
   *
   * @param motor port of motor
   * @return scaled from -1.0 to 1.0
   */
  public abstract double getGearRatio(int motor);

  /**
   * Set the differential control loop coefficients
   *
   * @param motor port of motor
   * @param pid PID
   */
  public abstract void setDifferentialControlLoopCoefficients(int motor, DifferentialControlLoopCoefficients pid);

  /**
   * Get the differential control loop coefficients
   *
   * @param motor port of motor
   * @return DifferentialControlLoopCoefficients
   */
  public abstract DifferentialControlLoopCoefficients getDifferentialControlLoopCoefficients(int motor);
}
