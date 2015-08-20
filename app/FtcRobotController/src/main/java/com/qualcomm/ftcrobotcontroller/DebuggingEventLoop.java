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

package com.qualcomm.ftcrobotcontroller;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.eventloop.SyncdDevice;
import com.qualcomm.robotcore.robocol.Command;

/**
 * A simple class that logs all event loops, and nothing more
 */
public class DebuggingEventLoop implements EventLoop {

  // Mock hardware used to control the rate at which the event loop runs
  private static class MockHardware implements SyncdDevice {
    private static int DELAY_IN_MS = 1000;

    @Override
    public void blockUntilReady() throws InterruptedException {
      Thread.sleep(DELAY_IN_MS);
    }

    @Override
    public void startBlockingWork() {
      // NO OP
    }
  }

  MockHardware hardware = new MockHardware();

  EventLoopManager manager;

  @Override
  public void init(EventLoopManager eventLoopManager) {
    manager = eventLoopManager;
    DbgLog.msg("====== Event Loop Init ==============================");

    // register the hardware ourselves since no factor is being used
    manager.registerSyncdDevice(hardware);
  }

  @Override
  public void loop() {
    DbgLog.msg("====== Event Loop ===================================");

    DbgLog.msg(manager.getGamepad(0).toString());
    DbgLog.msg(manager.getGamepad(1).toString());
    DbgLog.msg(manager.getHeartbeat().toString());
  }

  @Override
  public void teardown() {
    DbgLog.msg("====== Event Loop Teardown ==========================");
  }

  @Override
  public void processCommand(Command command) {
    DbgLog.msg("====== Event Loop Command ==========================");
    DbgLog.msg("                  Command: " + command.getName());
  }
}
