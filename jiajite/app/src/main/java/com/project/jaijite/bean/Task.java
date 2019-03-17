package com.project.jaijite.bean;


import android.text.TextUtils;

import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.service.MainService;
import com.project.jaijite.util.EasyLinkUtil;

public class Task {
    public static final int LIGHT_OPEN = 4;//灯控制开关 亮度
    public static final int LIGHT_COLOR_TEMPERATURE = 9;//色温
    public static final int LIGHT_DELAY = 14;//延时控制灯开关
    public static final int LIGHT_TIMER_OPEN = 12;//定时控制灯开
    public static final int LIGHT_TIMER_CLOSE = 13;//定时控制灯关
    public static final int LIGHT_Twinkle = 8;//灯闪烁
    public static final int LIGHT_RGB = 5;//RGB
    public static final int LIGHT_C = 6;//RGB
    public static final int LIGHT_DB = 7;//RGB

    private String head = "L:01";//固定头
    private String ledID;//灯序号
    private String groupID;//组
    private int function = LIGHT_OPEN;//指令类型
    private int attribute;//指令
    private String attributes;//指令
    private String taskResult;
    private boolean isOnlyAttribute = false;

    public String getLedID() {
        return ledID;
    }

    public void setLedID(String ledID) {
        this.ledID = ledID;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public int getAttribute() {
        return attribute;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public void setAttribute(String attribute) {
        this.attributes = attribute;
    }

    public String getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(String taskResult) {
        this.taskResult = taskResult;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public boolean isOnlyAttribute() {
        return isOnlyAttribute;
    }

    public void setOnlyAttribute(boolean onlyAttribute) {
        isOnlyAttribute = onlyAttribute;
    }

    public void startCommand(MainService.TaskListener listener) {
        //先设置选中的设备的IP信息
        DeviceInfo defaultCheckDevice = EasyLinkUtil.getDefaultCheckDevice();
        if (defaultCheckDevice == null) {
            listener.taskFailed("请先添加设备");
            return;
        }
        //设置地址
        Info.SERVER_ADDR = defaultCheckDevice.getIp();
        Info.PORT = defaultCheckDevice.getPort();
        MainService.newTask(this, true, listener);
    }

    public String getCommand() {
        if(isOnlyAttribute){
            return (TextUtils.isEmpty(attributes) ? attribute : attributes) + "\r";
        }
        return head + "," + ledID + groupID + "," + function + "," + (TextUtils.isEmpty(attributes) ? attribute : attributes) + "\r";//0D -> 16进制 3044
    }

//    public String getCommand() {
//        if (head.equals("L:")) {
//            if (type == SELECT_LEDID_TASK) {
//                command = head + mac + "," + ledID + "," + function + "," + attribute;
//            } else {
//                command = head + mac + "," + id + "," + function + "," + attribute;
//            }
//
//        } else if (head.equals("LEDGROUP")) {
//            return command;
//        } else {
//            command = head + old_paswd + "," + new_paswd;
//        }
//
//        return command;
//    }


}
