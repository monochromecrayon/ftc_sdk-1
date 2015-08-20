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

package com.qualcomm.ftccommon;

/**
 * List of RobotCore Robocol commands used by the FIRST apps
 */
public class CommandList {

  /**
   * Command to restart the robot
   */
  public static final String CMD_RESTART_ROBOT = "CMD_RESTART_ROBOT";

  /**
   * Command to request the list of op modes
   */
  public static final String CMD_REQUEST_OP_MODE_LIST = "CMD_REQUEST_OP_MODE_LIST";

  /**
   * Response to a command to request the list of op modes
   *
   *
   * Op modes will be in extra data
   */
  public static final String CMD_REQUEST_OP_MODE_LIST_RESP = "CMD_REQUEST_OP_MODE_LIST_RESP";

  /**
   * Command to switch op modes
   *
   * New op mode will be in extra data
   */
  public static final String CMD_SWITCH_OP_MODE = "CMD_SWITCH_OP_MODE";

  /**
   * Response to a command to switch op modes
   *
   * Current op mode will be in extra data
   */
  public static final String CMD_SWITCH_OP_MODE_RESP = "CMD_SWITCH_OP_MODE_RESP";
}
