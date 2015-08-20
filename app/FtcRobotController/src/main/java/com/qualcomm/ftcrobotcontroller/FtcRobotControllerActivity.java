/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.ftcrobotcontroller.FtcRobotControllerService.FtcRobotControllerBinder;
import com.qualcomm.hitechnic.HiTechnicHardwareFactory;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DeviceManager;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareFactory;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.hardware.configuration.Utility;
import com.qualcomm.robotcore.hardware.mock.MockDeviceManager;
import com.qualcomm.robotcore.hardware.mock.MockHardwareFactory;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.SerialNumber;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FtcRobotControllerActivity extends Activity
    implements FtcRobotControllerService.Callback {

  private static final boolean USE_MOCK_HARDWARE_FACTORY = false;
  private static final int NUM_GAMEPADS = 2;

  /**
   * Callback methods
   */
  public class Callback {

    /**
     * callback method to restart the robot
     */
    public void restartRobot() {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(context, "Restarting Robot", Toast.LENGTH_SHORT).show();
        }
      });

      // this call might be coming from the event loop, so we need to start
      // switch contexts before proceeding
      Thread t = new Thread() {
        @Override
        public void run() {
          try { Thread.sleep(1500); } catch (InterruptedException ignored) { }
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              requestRobotRestart();
            }
          });

        }
      };
      t.start();
    }

    public void updateGamepadUi(final Gamepad[] gamepads) {
      if (gamepads.length != textGamepad.length) {
        DbgLog.msg("Unable to update gamepad UI");
      }

      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          for (int i = 0; i < textGamepad.length; i++) {
            if (gamepads[i].id == Gamepad.ID_UNASSOCIATED) {
              textGamepad[i].setText(" "); // for some reason "" isn't working, android won't redraw the UI element
            } else {
              textGamepad[i].setText(gamepads[i].toString());
            }
          }
        }
      });
    }
  }

  protected SharedPreferences preferences;

  protected Callback callback = new Callback();
  protected Context context;
  private Utility utility;
  private String activeFilename;
  private boolean launched;

  protected TextView textWifiDirectStatus;
  protected TextView textRobotStatus;
  protected TextView[] textGamepad = new TextView[NUM_GAMEPADS];

  protected FtcRobotControllerService controllerService;

  protected EventLoop eventLoop;

  protected ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      FtcRobotControllerBinder binder = (FtcRobotControllerBinder) service;
      onServiceBind(binder.getService());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      controllerService = null;
    }
  };

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (UsbManager.ACTION_USB_ACCESSORY_ATTACHED.equals(intent.getAction())) {
      // a new USB device has been attached
      DbgLog.msg("USB Device attached; app restart may be needed");
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_ftc_controller);

    context = this;
    utility = new Utility(this);

    textWifiDirectStatus = (TextView) findViewById(R.id.textWifiDirectStatus);
    textRobotStatus = (TextView) findViewById(R.id.textRobotStatus);
    textGamepad[0] = (TextView) findViewById(R.id.textGamepad1);
    textGamepad[1] = (TextView) findViewById(R.id.textGamepad2);

    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);

    // save 4MB of logcat to the SD card
    RobotLog.writeLogcatToDisk(this, 4 * 1024);

    launched = false;
  }

  @Override
  protected void onStart() {
    super.onStart();

    Intent intent = new Intent(this, FtcRobotControllerService.class);
    bindService(intent, connection, Context.BIND_AUTO_CREATE);

    utility.updateHeader(Utility.NO_FILE, R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (controllerService != null) unbindService(connection);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.ftc_robot_controller, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_restart_robot:
        Toast.makeText(context, "Restarting Robot", Toast.LENGTH_SHORT).show();
        requestRobotRestart();
        return true;
      case R.id.action_pair_with_driver_station:
        startActivity(new Intent(getBaseContext(), FtcPairWithDriverStationActivity.class));
        return true;
      case R.id.action_settings:
        startActivity(new Intent(getBaseContext(), FtcRobotControllerSettingsActivity.class));
        return true;
      case R.id.action_about:
        startActivity(new Intent(getBaseContext(), AboutActivity.class));
        return true;
      case R.id.action_configuration:
        startActivity(new Intent(getBaseContext(), FtcConfigurationActivity.class));
        return true;
      case R.id.action_load:
          startActivity(new Intent(getBaseContext(), FtcLoadFileActivity.class));
          return true;
      case R.id.action_autoconfigure:
        startActivity(new Intent(getBaseContext(), AutoConfigureActivity.class));
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // don't destroy assets on screen rotation
  }

  public void onServiceBind(FtcRobotControllerService service) {
    DbgLog.msg("Bound to Ftc Controller Service");
    controllerService = service;

    wifiDirectUpdate(controllerService.getWifiDirectStatus());
    robotUpdate(controllerService.getRobotStatus());
    requestRobotSetup();
  }

  @Override
  public void wifiDirectUpdate(final WifiDirectAssistant.Event event) {
    String status = "Wifi Direct - ";

    switch (event) {
      case DISCONNECTED:
        status += "disconnected";
        break;
      case DISCOVERING_PEERS:
      case PEERS_AVAILABLE:
        status += String.format("looking for driver station (MAC address %s)",
            controllerService.getDriverStationMac());
        break;
      case CONNECTING:
        status += String.format("connecting to driver station (MAC address %s)",
            controllerService.getDriverStationMac());
        break;
      case CONNECTED_AS_PEER:
        status += String.format("connected to driver station (MAC address %s)",
            controllerService.getDriverStationMac());
        break;
      case ERROR:
        status += "ERROR";
        break;
      default:
        status += "unexpected state: " + event.name();
        break;
    }

    DbgLog.msg(status);
    final String finalStatus = status;
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textWifiDirectStatus.setText(finalStatus);
      }
    });
  }

  @Override
  public void robotUpdate(final String status) {
    DbgLog.msg(status);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        textRobotStatus.setText(status);
      }
    });
  }

  private void requestRobotSetup() {
    if (controllerService == null) return;

    boolean hasConfigFile = preferences.contains(getString(R.string.pref_hardware_config_filename));
    activeFilename = utility.getFilenameFromPrefs(R.string.pref_hardware_config_filename, Utility.NO_FILE);
    if (!launched) {
      if (!hasConfigFile ||
          activeFilename.equalsIgnoreCase(Utility.NO_FILE) ||
          activeFilename.toLowerCase().contains(Utility.UNSAVED.toLowerCase())) {
        utility.saveToPreferences(Utility.NO_FILE, R.string.pref_hardware_config_filename);
        activeFilename = Utility.NO_FILE;
        DbgLog.msg("No default config file, so launching Hardware Wizard");
        launched = true;
        startActivity(new Intent(getBaseContext(), FtcLoadFileActivity.class));
        return;
      }
    }

    utility.updateHeader(Utility.NO_FILE, R.string.pref_hardware_config_filename, R.id.active_filename, R.id.included_header);
    activeFilename = utility.getFilenameFromPrefs(R.string.pref_hardware_config_filename, Utility.NO_FILE).replaceFirst("[.][^.]+$", "");

    final String filename = Utility.CONFIG_FILES_DIR
        + utility.getFilenameFromPrefs(R.string.pref_hardware_config_filename, Utility.NO_FILE) + Utility.FILE_EXT;

    FileInputStream fis;
    try {
      fis = new FileInputStream(filename);
    } catch (FileNotFoundException e) {
      String msg = "Cannot open robot configuration file - " + filename;
      utility.complainToast(msg, context);
      DbgLog.msg(msg);
      return;
    }

    HardwareFactory factory;

    if (USE_MOCK_HARDWARE_FACTORY) {
      // TODO: temp testing code. This will be removed in a future release
      try {
        DeviceManager dm = new MockDeviceManager(null, null);
        DcMotorController mc = dm.createUsbDcMotorController(new SerialNumber("MC"));
        ServoController sc = dm.createUsbServoController(new SerialNumber("SC"));

        HardwareMap hwMap = new HardwareMap();
        hwMap.dcMotor.put("left", new DcMotor(mc, 1));
        hwMap.dcMotor.put("right", new DcMotor(mc, 2));
        hwMap.servo.put("a", new Servo(sc, 1));
        hwMap.servo.put("b", new Servo(sc, 2));
        factory = new MockHardwareFactory(hwMap);
      } catch (RobotCoreException e) {
        DbgLog.logStacktrace(e);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        return;
      } catch (InterruptedException e) {
        DbgLog.logStacktrace(e);
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        return;
      }
  } else {
      // HiTechnic Factory for use with HiTechnic hardware
      HiTechnicHardwareFactory hitechnicFactory = new HiTechnicHardwareFactory(context);
      hitechnicFactory.setXmlInputStream(fis);
      factory = hitechnicFactory;
    }

    eventLoop = new FtcEventLoop(factory, callback);

    controllerService.setCallback(this);
    controllerService.setupRobot(eventLoop);
  }

  private void requestRobotShutdown() {
    if (controllerService == null) return;
    controllerService.shutdownRobot();
  }

  private void requestRobotRestart() {
    requestRobotShutdown();
    requestRobotSetup();
  }
}
