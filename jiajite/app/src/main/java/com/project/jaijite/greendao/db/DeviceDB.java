package com.project.jaijite.greendao.db;

import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.greendao.helper.DbManager;

import java.util.List;

public class DeviceDB {
    /**
     * 插入设备
     *
     * @param info
     */
    public static void insertDeviceData(DeviceInfo info) {
        DbManager.getDaoSession().getDeviceInfoDao().insert(info);
    }

    public static List<DeviceInfo> getAllDeviceData() {
        return DbManager.getDaoSession().getDeviceInfoDao().queryBuilder().list();
    }

    public static void updateLight(DeviceInfo info){
        DbManager.getDaoSession().getDeviceInfoDao().update(info);
    }

    public static void delLight(DeviceInfo info){
        DbManager.getDaoSession().getDeviceInfoDao().delete(info);
    }
}
