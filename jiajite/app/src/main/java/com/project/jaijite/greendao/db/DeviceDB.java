package com.project.jaijite.greendao.db;

import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.greendao.dao.DeviceInfoDao;
import com.project.jaijite.greendao.helper.DbManager;

import java.util.List;

public class DeviceDB {

    public static List<DeviceInfo> getAllDeviceData() {
        return DbManager.getDaoSession().getDeviceInfoDao().queryBuilder().list();
    }

    public static void updateOrInsert(DeviceInfo info) {
        //如果通过设备名称查到设备 则更新设备信息
        DeviceInfo di = DbManager.getDaoSession().getDeviceInfoDao().queryBuilder()
                .where(DeviceInfoDao.Properties.Name.eq(info.getName()))
                .build()
                .unique();
        if (di != null) {
            di.setName(info.getName());
            di.setPwd(info.getPwd());
            DbManager.getDaoSession().getDeviceInfoDao().update(di);
        } else {
            DbManager.getDaoSession().getDeviceInfoDao().insert(info);
        }
    }

    public static void delLight(DeviceInfo info) {
        DbManager.getDaoSession().getDeviceInfoDao().delete(info);
    }
}
