package com.project.jaijite.greendao.db;

import com.project.jaijite.entity.LightInfo;
import com.project.jaijite.greendao.dao.LightInfoDao;
import com.project.jaijite.greendao.helper.DbManager;

import java.util.List;

public class LightingDB {
    /**
     * 插入灯
     *
     * @param name
     */
    public static void insertLightData(String name) {
        DbManager.getDaoSession().getLightInfoDao().insert(new LightInfo(name));
    }

    /**
     * 是否有数据
     * @return
     */
    public static boolean isEmpty() {
        return DbManager.getDaoSession().getLightInfoDao().queryBuilder().count() <= 0;
    }

    /**
     * 获取所有正常灯的数据
     * @return
     */
    public static List<LightInfo> getAllNormalLightData() {
        DbManager.getDaoSession().clear();
       return DbManager
               .getDaoSession()
               .getLightInfoDao()
               .queryBuilder()
               .where(LightInfoDao.Properties.IsDelete.eq(0))
               .list();
    }

    /**
     * 获取所有移除灯的数据
     * @return
     */
    public static List<LightInfo> getAllDelLightData() {
        DbManager.getDaoSession().clear();
        return DbManager
                .getDaoSession()
                .getLightInfoDao()
                .queryBuilder()
                .where(LightInfoDao.Properties.IsDelete.eq(1))
                .list();
    }

    public static void updateLight(LightInfo info){
        DbManager
                .getDaoSession()
                .getLightInfoDao()
                .update(info);
    }

    public static void updateLight(List<LightInfo> info){
        DbManager
                .getDaoSession()
                .getLightInfoDao()
                .updateInTx(info);
    }

    public static void delLight(LightInfo info){
        info.setIsDelete(1);
        updateLight(info);
    }

    public static void addLight(LightInfo info){
        info.setIsDelete(0);
        updateLight(info);
    }
}
