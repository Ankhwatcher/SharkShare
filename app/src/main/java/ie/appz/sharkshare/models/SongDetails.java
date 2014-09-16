package ie.appz.sharkshare.models;

import java.util.List;

/**
 * Created by rory on 16/09/14.
 */

public class SongDetails {
    private List<SongDetail> songDetailList;


    public List<SongDetail> getSongDetailList() {
        return songDetailList;
    }

    public void setSongDetailList(List<SongDetail> songDetailList) {
        this.songDetailList = songDetailList;
    }
}
