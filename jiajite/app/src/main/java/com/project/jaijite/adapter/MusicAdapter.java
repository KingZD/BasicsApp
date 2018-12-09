package com.project.jaijite.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.entity.MusicInfo;


public class MusicAdapter extends BaseQuickAdapter<MusicInfo, BaseViewHolder> {
    public MusicAdapter() {
        super(R.layout.item_music);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicInfo item) {
        helper.setText(R.id.tvMusicName, item.getTitle());
        helper.setText(R.id.tvAuthor, item.getArtist());
        helper.setText(R.id.tvDetail, item.getAlbum());
    }
}
