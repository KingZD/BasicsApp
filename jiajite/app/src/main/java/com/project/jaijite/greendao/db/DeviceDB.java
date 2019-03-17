package com.project.jaijite.greendao.db;

import android.text.TextUtils;

import com.project.jaijite.entity.DeviceInfo;
import com.project.jaijite.greendao.dao.DeviceInfoDao;
import com.project.jaijite.greendao.helper.DbManager;

import java.util.List;

public class DeviceDB {

    public static DeviceInfo isExistsDevice(String mac) {
        return DbManager.getDaoSession().getDeviceInfoDao().queryBuilder()
                .where(DeviceInfoDao.Properties.Mac.eq(mac)).build()
                .unique();
    }

    public static DeviceInfo isExistsDevice(Long id) {
        return DbManager.getDaoSession().getDeviceInfoDao().queryBuilder()
                .where(DeviceInfoDao.Properties.Id.eq(id)).build()
                .unique();
    }

    public static List<DeviceInfo> getAllDeviceData() {
        return DbManager.getDaoSession().getDeviceInfoDao().queryBuilder().list();
    }

    public static void updateAll(List<DeviceInfo> deviceInfos) {
        DbManager.getDaoSession().getDeviceInfoDao().updateInTx(deviceInfos);
    }

    public static void updateOrInsert(DeviceInfo info) {
        //如果通过设备mac查到设备 则更新设备信息
        DeviceInfo di = TextUtils.isEmpty(info.getMac()) ?
                null : DbManager.getDaoSession().getDeviceInfoDao().queryBuilder()
                .where(DeviceInfoDao.Properties.Mac.eq(info.getMac()))
                .build()
                .unique();
        if (di != null) {
            info.setId(di.getId());
            DbManager.getDaoSession().getDeviceInfoDao().update(info);
        } else {
            DbManager.getDaoSession().getDeviceInfoDao().insert(info);
        }
    }

    public static void delLight(DeviceInfo info) {
        DbManager.getDaoSession().getDeviceInfoDao().delete(info);
    }
}
