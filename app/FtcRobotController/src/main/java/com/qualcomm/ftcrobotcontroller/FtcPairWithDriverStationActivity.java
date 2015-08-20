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

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.wifi.WifiDirectAssistant;

import java.util.List;

public class FtcPairWithDriverStationActivity extends Activity implements OnClickListener {

  private WifiDirectAssistant wifiDirect;
  private SharedPreferences sharedPref;
  private String driverStationMac;

  List<WifiP2pDevice> savedPeers;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ftc_controller_pair_with_driver_station);

    /*
     * Do not enable or disable WifiDirectAssistant in this app, FtcRobotController will manage
     * the Wifi Direct Assistant
     */
    wifiDirect = WifiDirectAssistant.getWifiDirectAssistant(this);
  }

  @Override
  public void onStart() {
    super.onStart();

    DbgLog.msg("Starting Pairing with Driver Station activity");

    sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    driverStationMac = sharedPref.getString(getString(R.string.pref_driver_station_mac), getString(R.string.pref_driver_station_mac_default));

    updateDevicesList(wifiDirect.getPeers());
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  @Override
  public void onClick(View view) {

    if (view instanceof RadioButton) {
      RadioButton button = ((RadioButton) view);

      if (button.getId() == 0) {
        driverStationMac = getString(R.string.pref_driver_station_mac_default);
      } else {
        driverStationMac = savedPeers.get(button.getId() - 1).deviceAddress;
      }

      SharedPreferences.Editor editor = sharedPref.edit();
      editor.putString(getString(R.string.pref_driver_station_mac), driverStationMac);
      editor.commit();

      DbgLog.msg("Setting Driver Station MAC address to " + driverStationMac);
    }
  }

  private void updateDevicesList(List<WifiP2pDevice> peers) {
    savedPeers = peers;

    RadioGroup rg = ((RadioGroup) findViewById(R.id.radioGroupDevices));
    rg.clearCheck();
    rg.removeAllViews();

    // add none option
    RadioButton b = new RadioButton(this);
    b.setId(0);
    b.setText("None\nDo not pair with any device");
    b.setPadding(0 , 0, 0, 24);
    b.setOnClickListener(this);
    if (driverStationMac.equalsIgnoreCase(getString(R.string.pref_driver_station_mac_default))) {
      b.setChecked(true);
    }
    rg.addView(b);

    // add devices
    int i = 1;
    for (WifiP2pDevice peer : peers) {
      b = new RadioButton(this);
      b.setId(i++);
      b.setText(peer.deviceName + "\n" + peer.deviceAddress);
      b.setPadding(0, 0, 0, 24);

      if (peer.deviceAddress.equalsIgnoreCase(driverStationMac)) {
        b.setChecked(true);
      }

      b.setOnClickListener(this);

      rg.addView(b);
    }
  }
}
