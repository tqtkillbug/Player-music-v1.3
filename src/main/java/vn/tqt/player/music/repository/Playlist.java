package vn.tqt.player.music.repository;


public class Playlist {
    private String name;
    private String path;

    public Playlist(){

    }

    public Playlist(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
