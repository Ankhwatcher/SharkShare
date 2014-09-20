package ie.appz.sharkshare.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ie.appz.sharkshare.R;
import ie.appz.sharkshare.models.SongDetail;

/**
 * Created by rory on 18/09/14.
 */
public class SongAdapter extends BaseAdapter {
    private ArrayList<SongDetail> list = new ArrayList<SongDetail>();
    private Random random = new Random();

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public SongDetail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getSongId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        SongDetail songDetail = getItem(position);
        SongDetailHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_song_detail, parent, false);

            holder = new SongDetailHolder();
            ButterKnife.inject(holder, convertView);
            if (convertView != null)
                convertView.setTag(R.id.tag_cell_holder, holder);
        } else {
            holder = (SongDetailHolder) convertView.getTag(R.id.tag_cell_holder);
        }

        holder.tvArtist.setText(songDetail.getArtistName());
        holder.tvSong.setText(songDetail.getSongName());
        holder.tvAlbum.setText(songDetail.getAlbumName());

        float[] hsv = new float[3];

        int color = generateRandomColour();
        Color.colorToHSV(color, hsv);

        hsv[2] *= 0.5;
        color = Color.HSVToColor(hsv);

        holder.ivAlbum.setColorFilter(color);


        return convertView;
    }

    public ArrayList<SongDetail> getList() {
        return list;
    }

    public void setList(ArrayList<SongDetail> list) {
        this.list = list;
    }

    public final int generateRandomColour() {

        return Color.argb(random.nextInt(), random.nextInt(), random.nextInt(), random.nextInt());
    }

    static class SongDetailHolder {
        @InjectView(R.id.tvArtist)
        TextView tvArtist;

        @InjectView(R.id.tvSong)
        TextView tvSong;

        @InjectView(R.id.tvAlbum)
        TextView tvAlbum;

        @InjectView(R.id.ivAlbum)
        ImageView ivAlbum;
    }
}
