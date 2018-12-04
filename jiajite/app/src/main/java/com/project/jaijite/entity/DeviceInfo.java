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
    private String name;
    private String pwd;
    @Generated(hash = 1149050850)
    public DeviceInfo(Long id, String name, String pwd) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
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
    
}
