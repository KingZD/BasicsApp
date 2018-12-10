package com.project.jaijite.adapter;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.project.jaijite.R;
import com.project.jaijite.entity.MusicInfo;


public class MusicAdapter extends BaseQuickAdapter<MusicInfo, BaseViewHolder> {
    private int playIndex = -1;
    private int leastIndex = -1;

    public MusicAdapter() {
        super(R.layout.item_music);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicInfo item) {
        helper.setText(R.id.tvMusicName, item.getTitle());
        helper.setText(R.id.tvAuthor, item.getArtist());
        helper.setText(R.id.tvDetail, item.getAlbum());
        helper.setText(R.id.tvIndex, String.valueOf(helper.getAdapterPosition() + 1));
        helper.setVisible(R.id.tvIndex, !(playIndex == helper.getAdapterPosition()));
        helper.setVisible(R.id.ivIcon, playIndex == helper.getAdapterPosition());
    }

    public void setPlayIndex(int playIndex) {
        if (playIndex < 0) {
            playIndex = 0;
        }
        this.playIndex = playIndex;
        notifyItemChanged(playIndex);
        notifyItemChanged(leastIndex);
        leastIndex = playIndex;
    }
}
