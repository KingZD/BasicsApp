package com.project.jaijite.bean;


public class Task {
    public static final int SELECT_LEDID_TASK = 0;
    private String command;
    private long id;
    private String ledID;
    private String head = "L:";
    private String mac = "E005C54DF500";
    private int function;
    private String attribute;
    private String new_paswd;
    private String old_paswd;
    private String taskResult;
    private int groupID;
    private int type;


    public int getType() {
        return type;
    }


    public void setType(int type) {
        this.type = type;
    }


    public String getLedID() {
        return ledID;
    }


    public void setLedID(String ledID) {
        this.ledID = ledID;
    }


    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getTaskResult() {
        return taskResult;
    }


    public void setTaskResult(String task_result) {
        this.taskResult = task_result;
    }


    public void setCommand(String command) {
        this.command = command;
    }

    public String getNew_paswd() {
        return new_paswd;
    }

    public void setNew_paswd(String new_paswd) {
        this.new_paswd = new_paswd;
    }

    public String getOld_paswd() {
        return old_paswd;
    }

    public void setOld_paswd(String old_paswd) {
        this.old_paswd = old_paswd;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getHead() {
        return head;
    }

    public void sethead(String head) {
        this.head = head;
    }

    public int getFunction() {
        return function;
    }

    public void setFunction(int function) {
        this.function = function;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getCommand() {
        if (head.equals("L:")) {
            if (type == SELECT_LEDID_TASK) {
                command = head + mac + "," + ledID + "," + function + "," + attribute;
            } else {
                command = head + mac + "," + id + "," + function + "," + attribute;
            }

        } else if (head.equals("LEDGROUP")) {
            return command;
        } else {
            command = head + old_paswd + "," + new_paswd;
        }

        return command;
    }


}
