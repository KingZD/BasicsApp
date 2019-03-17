package com.project.jaijite.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id = null;
    private String showName;//显示名称 提供用户修改
    private String name;//wifi名称
    private String pwd;//wifi密码
    private String ip;
    private String mac;
    private String deviceName;//设备名称
    private String server;
    private int port;
    private Boolean check = false;

    @Generated(hash = 1005351940)
    public DeviceInfo(Long id, String showName, String name, String pwd, String ip,
            String mac, String deviceName, String server, int port, Boolean check) {
        this.id = id;
        this.showName = showName;
        this.name = name;
        this.pwd = pwd;
        this.ip = ip;
        this.mac = mac;
        this.deviceName = deviceName;
        this.server = server;
        this.port = port;
        this.check = check;
    }
    @Generated(hash = 2125166935)
    public DeviceInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getShowName() {
        return this.showName;
    }
    public void setShowName(String showName) {
        this.showName = showName;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPwd() {
        return this.pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getDeviceName() {
        return this.deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getServer() {
        return this.server;
    }
    public void setServer(String server) {
        this.server = server;
    }
    public int getPort() {
        return this.port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public Boolean getCheck() {
        return this.check;
    }
    public void setCheck(Boolean check) {
        this.check = check;
    }

  
    
}
