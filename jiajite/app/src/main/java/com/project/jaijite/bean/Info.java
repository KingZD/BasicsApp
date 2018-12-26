package com.project.jaijite.bean;

public class Info {
    public static final int LED_ON = 1;
    public static final int LED_OFF = 0;
	
    public static final int ON = 1;
    public static final int OFF = 0;

    /*public  static final String SERVER_ADDR = "192.168.2.1";
    public  static final int PORT = 7001;*/
    public static String SERVER_ADDR = "172.17.3.4";
    public static int PORT = 9999;

    //command function
    public static final int TURN_OFF = 3;
    public static final int TURN_ON = 4;

    //flash
    public static final int FLASH = 8;
    //bright
    public static final int BRIGHT = 5;
    //color
    public static final int COLOR = 9;
    //小夜灯
    public static final int NIGHT_LAMPSS = 5;
    //get led
    public static final int GET_LED = 11;

    public static final int LIGHT_PER = 70;

    public static final int UPDATE_PASWD = 13;
    public static final int NEW_PASWD = 14;

}
