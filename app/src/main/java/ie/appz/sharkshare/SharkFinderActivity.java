package ie.appz.sharkshare;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import ie.appz.sharkshare.models.SongDetail;


public class SharkFinderActivity extends Activity {

    @InjectView(R.id.gvSongs)
    protected GridView gvSongs;

    private List<SongDetail> songDetailList = new ArrayList<SongDetail>();
    private SongAdapter songAdapter = new SongAdapter();
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shark_finder);
        ButterKnife.inject(this);

        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        gvSongs.setAdapter(songAdapter);


        if (getIntent().getAction().equals(Intent.ACTION_SEND) && getIntent().hasExtra(Intent.EXTRA_SUBJECT)) {
            String searchText = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);

            //Remove the "Check out " message from the start of Google Music links
            if (searchText.startsWith("Check out ")) {
                searchText = searchText.replace("Check out ", "");
            }

            Log.d(Constants.LOGTAG, "Searching for " + searchText);
            TinySharkApi.getInstance(this).performSearch(this, searchText, new Response.Listener<List<SongDetail>>() {
                @Override
                public void onResponse(List<SongDetail> response) {
                    songDetailList.clear();
                    songDetailList.addAll(response);
                    songAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.fillInStackTrace();
                }
            });
        }


    }

    @OnItemClick(R.id.gvSongs)
    void songClicked(int position) {
        String url = songAdapter.getItem(position).getUrl();
        ClipData clipData = ClipData.newPlainText("tinysong link", url);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(this, url + " added to the clipboard", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shark_finder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static class SongDetailHolder {
        @InjectView(R.id.tvArtist)
        TextView tvArtist;

        @InjectView(R.id.tvSong)
        TextView tvSong;

        @InjectView(R.id.tvAlbum)
        TextView tvAlbum;
    }

    public class SongAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songDetailList.size();
        }

        @Override
        public SongDetail getItem(int position) {
            return songDetailList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getSongId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SongDetail songDetail = getItem(position);
            SongDetailHolder holder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.cell_song_detail, parent, false);
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

            return convertView;
        }

    }
}
