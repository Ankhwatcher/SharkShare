package ie.appz.sharkshare.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by rory on 16/09/14.
 */
public class SongDetail {

    @SerializedName("Url")
    private String url;
    @SerializedName("SongID")
    private Long songId;
    @SerializedName("SongName")
    private String songName;
    @SerializedName("ArtistID")
    private Long artistId;
    @SerializedName("ArtistName")
    private String artistName;
    @SerializedName("AlbumID")
    private Long albumId;
    @SerializedName("AlbumName")
    private String albumName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
