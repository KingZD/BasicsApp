package com.project.jaijite.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LightInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id(autoincrement = true)
    private Long id = null;
    private String name;
    private int light_level = 70;
    private int color_temp = 6500;
    private String time_on = "00:00";
    private String time_off = "00:00";
    private String delay = "00:00";
    private int jump = 0;
    private int water = 0;
    private int touch = 0;
    private int gflash = 0;
    private int bflash = 0;
    private int warming = 0;
    private int led_state = 0;
    private int night_lamp_state = 0;
    private int isDelete = 0;    //是否移除了该类型的灯 0-正常 1-移除
    @Transient
    private boolean isCheck; //自定义字段

    @Generated(hash = 298312183)
    public LightInfo(Long id, String name, int light_level, int color_temp, String time_on,
            String time_off, String delay, int jump, int water, int touch, int gflash,
            int bflash, int warming, int led_state, int night_lamp_state, int isDelete) {
        this.id = id;
        this.name = name;
        this.light_level = light_level;
        this.color_temp = color_temp;
        this.time_on = time_on;
        this.time_off = time_off;
        this.delay = delay;
        this.jump = jump;
        this.water = water;
        this.touch = touch;
        this.gflash = gflash;
        this.bflash = bflash;
        this.warming = warming;
        this.led_state = led_state;
        this.night_lamp_state = night_lamp_state;
        this.isDelete = isDelete;
    }

    public LightInfo(String name) {
        this.name = name;
    }

    @Generated(hash = 347492895)
    public LightInfo() {
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

    public int getLight_level() {
        return this.light_level;
    }

    public void setLight_level(int light_level) {
        this.light_level = light_level;
    }

    public int getColor_temp() {
        return this.color_temp;
    }

    public void setColor_temp(int color_temp) {
        this.color_temp = color_temp;
    }

    public String getTime_on() {
        return this.time_on;
    }

    public void setTime_on(String time_on) {
        this.time_on = time_on;
    }

    public String getTime_off() {
        return this.time_off;
    }

    public void setTime_off(String time_off) {
        this.time_off = time_off;
    }

    public String getDelay() {
        return this.delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public int getJump() {
        return this.jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

    public int getWater() {
        return this.water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    public int getTouch() {
        return this.touch;
    }

    public void setTouch(int touch) {
        this.touch = touch;
    }

    public int getGflash() {
        return this.gflash;
    }

    public void setGflash(int gflash) {
        this.gflash = gflash;
    }

    public int getBflash() {
        return this.bflash;
    }

    public void setBflash(int bflash) {
        this.bflash = bflash;
    }

    public int getWarming() {
        return this.warming;
    }

    public void setWarming(int warming) {
        this.warming = warming;
    }

    public int getLed_state() {
        return this.led_state;
    }

    public void setLed_state(int led_state) {
        this.led_state = led_state;
    }

    public int getNight_lamp_state() {
        return this.night_lamp_state;
    }

    public void setNight_lamp_state(int night_lamp_state) {
        this.night_lamp_state = night_lamp_state;
    }

    public int getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}
