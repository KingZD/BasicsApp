package com.project.jaijite.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.project.jaijite.entity.MusicInfo;

import java.util.ArrayList;
import java.util.List;

public class MusicUtil {
    public static List<MusicInfo> getMusicData(Context context) {
        return getMusicData(context, 0L);
    }

    public static List<MusicInfo> getMusicData(Context context, Long musicSize) {
        List<MusicInfo> list = new ArrayList<>();
        Cursor data = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (data != null) {
            while (data.moveToNext()) {
                MusicInfo music = new MusicInfo();

                long id = data.getLong(data.getColumnIndex(BaseColumns._ID));
                String title = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                String artist = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                long albumId = data.getLong(data.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                String path = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = data.getString(data.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
                long duration = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.DURATION));
                long fileSize = data.getLong(data.getColumnIndex(MediaStore.Audio.Media.SIZE));
                int isMusic = data.getInt(data.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                String prefix = data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));

                music.setSongId(id);
                music.setType(0);//本地
                music.setTitle(title);
                music.setArtist(artist);
                music.setAlbum(album);
                music.setAlbumId(albumId);
                music.setDuration(duration);
                music.setPath(path);
                music.setFileName(fileName);
                music.setFileSize(fileSize);
                // 注释部分是切割标题，分离出歌曲名和歌手 （本地媒体库读取的歌曲信息不规范）
//                    if (song.song.contains("-")) {
//                        val str = song.song.split("-")
//                        song.singer = str[0]
//                        song.song = str[1]
//                    }
                // 歌曲格式
                switch (prefix) {
                    case "audio/mpeg":
                        music.setPrefix("mp3");
                        break;
                    case "audio/x-ms-wma":
                        music.setPrefix("wma");
                        break;
                    default:
                        String[] split = path.split("[.]");
                        music.setPrefix(split[1]);
                }
                list.add(music);
            }
            // 释放资源
            data.close();
        }
        return list;
    }
}
