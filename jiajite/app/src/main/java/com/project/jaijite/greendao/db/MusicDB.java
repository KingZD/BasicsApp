package com.project.jaijite.greendao.db;

import com.project.jaijite.entity.MusicInfo;
import com.project.jaijite.greendao.helper.DbManager;

import java.util.List;

public class MusicDB {
    /**
     * 插入设备
     *
     */
    public static List<MusicInfo> getAllMusic() {
        return DbManager.getDaoSession().getMusicInfoDao().queryBuilder().list();
    }

    public static void clearMusic(){
        DbManager.getDaoSession().getMusicInfoDao().deleteAll();
    }

    public static void insert(MusicInfo info){
        DbManager.getDaoSession().getMusicInfoDao().insert(info);
    }

    public static void clearMusic(MusicInfo info){
        DbManager.getDaoSession().getMusicInfoDao().delete(info);
    }

    public static void addAllMusic(List<MusicInfo> musicInfos){
        DbManager.getDaoSession().getMusicInfoDao().insertInTx(musicInfos);
    }
}
