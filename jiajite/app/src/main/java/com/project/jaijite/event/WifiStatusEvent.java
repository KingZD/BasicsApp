package com.project.jaijite.event;

public class WifiStatusEvent {
    private int wifiState;

    public WifiStatusEvent(int wifiState) {
        this.wifiState = wifiState;
    }

    public int getWifiState() {
        return wifiState;
    }

    public void setWifiState(int wifiState) {
        this.wifiState = wifiState;
    }
}
