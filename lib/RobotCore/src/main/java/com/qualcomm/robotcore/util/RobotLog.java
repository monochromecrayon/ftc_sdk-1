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

package com.qualcomm.robotcore.util;

import android.content.Context;
import android.os.Environment;

import com.qualcomm.robotcore.exception.RobotCoreException;

import java.io.File;

/**
 * Allows consistent logging across all RobotCore packages
 */
public class RobotLog {

  /*
   * Currently only supports android style logging, but may support more in the future.
   */

  /*
   * Only contains static utility methods
   */
  private RobotLog() {}

  public static final String TAG = "RobotCore";

  private static boolean writeLogcatToDiskEnabled = false;

  public static void v(String message) {
    android.util.Log.v(TAG, message);
  }

  public static void d(String message) {
    android.util.Log.d(TAG, message);
  }

  public static void i(String message) {
    android.util.Log.i(TAG, message);
  }

  public static void w(String message) {
    android.util.Log.w(TAG, message);
  }

  public static void e(String message) {
    android.util.Log.e(TAG, message);
  }

  public static void logStacktrace(Exception e) {
    RobotLog.e(e.toString());
    for (StackTraceElement el : e.getStackTrace()) {
      RobotLog.e(el.toString());
    }
  }

  public static void logStacktrace(RobotCoreException e) {
    RobotLog.e(e.toString());
    for (StackTraceElement el : e.getStackTrace()) {
      RobotLog.e(el.toString());
    }

    if (e.isChainedException()) {
      RobotLog.e("Exception chained from:");
      if (e.getChainedException() instanceof RobotCoreException) {
        logStacktrace((RobotCoreException) e.getChainedException());
      } else {
        logStacktrace(e.getChainedException());
      }
    }
  }

  /**
   * Write logcat logs to disk. This method will continue writing logcat files for as long as the
   * program runs. Additional calls to this method will be a NOOP.
   *
   * @param context this app's context
   */
  public static void writeLogcatToDisk(Context context, final int fileSizeKb) {
    if (writeLogcatToDiskEnabled) { return; }
    writeLogcatToDiskEnabled = true;

    final String packageName = context.getPackageName();
    final String filename =
        (new File(Environment.getExternalStorageDirectory(), packageName)).getAbsolutePath();

    Thread logThread = new Thread() {
      @Override
      public void run() {
        try {

          final String filter = "UsbRequestJNI:S UsbRequest:S *:V";

          RobotLog.v("saving logcat to " + filename);
          RunShellCommand shell = new RunShellCommand();
          RunShellCommand.killSpawnedProcess("logcat", packageName, shell);
          shell.run(String.format("logcat -f %s.logcat -r%d -v time %s", filename, fileSizeKb,
              filter));
        } catch (RobotCoreException e) {
          RobotLog.v("Error while writing log file to disk: " + e.toString());
        } finally {
          writeLogcatToDiskEnabled = false;
        }
      }
    };
    logThread.start();
  }

  public static void cancelWriteLogcatToDisk(Context context) {
    final String packageName = context.getPackageName();
    final String filename = (new File(context.getFilesDir(), packageName)).getAbsolutePath();

    writeLogcatToDiskEnabled = false;

    try {
      RobotLog.v("closing logcat file " + filename);
      RunShellCommand shell = new RunShellCommand();
      RunShellCommand.killSpawnedProcess("logcat", packageName, shell);
    } catch (RobotCoreException e) {
      RobotLog.v("Unable to cancel writing log file to disk: " + e.toString());
    }
  }
}
