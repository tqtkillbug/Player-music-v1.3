package vn.tqt.player.music.playerController;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;
import vn.tqt.player.music.repository.Playlist;
import vn.tqt.player.music.repository.Song;

public class PlayerController implements Initializable {

    @FXML
    private TextField namePlaylist;
    @FXML
    private ImageView logoSong;
    @FXML
    private Pane pane;
    @FXML
    private Label singerName;
    @FXML
    private Label songName;
    @FXML
    private Label songTime;
    @FXML
    private Button playButton, pauseButton, nextButton, perviousButton, loopButton, randomButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private ComboBox<String> playlistBox;
    @FXML
    private Slider volumeBar;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private TableView<Song> table;
    @FXML
    private TableColumn<Song, Integer> idColumn;
    @FXML
    private TableColumn<Song, String> nameColumn;
    private ObservableList<Song> songList;
    private ObservableList<Playlist> playlistList;

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    private File musicDirectory;
    @FXML
    private File allMusicDirectory;
    @FXML
    private File imageDirectory;
    private File[] musicFiles;
    private File[] allMusicFiles;
    private File[] imageFiles;
    private ArrayList<File> songs;
    private ArrayList<File> allSong;
    private ArrayList<File> images;
    private int songNumber;
    private String songTitle;
    private int[] speeds = {75, 100, 125, 150, 175, 500000};
    private Timer timer;
    private TimerTask task;
    private boolean running;
    private boolean playbtnstatus;
    private boolean randombtnstatus;
    private boolean loopbtnstatus;
    private String sourcePathMusic = "music";
    private final File playlistDirectory = new File("playlist");
    private String[] namePlaylists;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        importMusicDirectory();
        importImagesDirectory();
        initSpeedBox();
        initVolumeBar();
        initTableviewSong();
        showPlaylistName();
        showPlaylistOnComBox();
        try {
            setLogoSong();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        getSongTitle();
        try {
            initMedia(songNumber);
        } catch (TikaException | IOException | SAXException e) {
            e.printStackTrace();
        }
        try {
            addSongToTbView();
        } catch (TikaException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void importMusicDirectory() {
        songs = new ArrayList<>();
        musicDirectory = new File(sourcePathMusic);
        musicFiles = musicDirectory.listFiles();
        if (musicFiles != null) {
            for (File file : musicFiles) {
                songs.add(file);
                System.out.println(file);
            }
        }
    }

    public void importImagesDirectory() {
        images = new ArrayList<>();
        imageDirectory = new File("image");
        imageFiles = imageDirectory.listFiles();
        if (imageFiles != null) {
            for (File file : imageFiles) {
                images.add(file);
                System.out.println(file);
            }
        }
    }

    public void initSpeedBox() {
        for (int i = 0; i < speeds.length; i++) {
            speedBox.getItems().add(Integer.toString(speeds[i]));
        }
        speedBox.setOnAction(this::changeSpeed);
    }

    public void initVolumeBar() {
        volumeBar.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeBar.getValue() * 0.01);
            }
        });
    }

    public void initTableviewSong() {
        songList = FXCollections.observableArrayList();
        idColumn.setCellValueFactory(new PropertyValueFactory<Song, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Song, String>("songName"));
        table.setItems(songList);
        playlistList = FXCollections.observableArrayList();
        namePlaylist.setPromptText("Enter Name Playlist Want Creat");
    }


    public void showAllSong() throws TikaException, IOException, SAXException {
        allSong = new ArrayList<>();
        allMusicDirectory = new File("music");
        allMusicFiles = allMusicDirectory.listFiles();
        if (allMusicFiles != null) {
            for (File file : allMusicFiles) {
                allSong.add(file);
                System.out.println(file);
            }
        }
        for (int i = 0; i < allSong.size(); i++) {
            String songName = getTitleSong(i, allSong);
            Song newSong = new Song();
            newSong.setId(i + 1);
            newSong.setSongName(songName);
            newSong.setSongPath(allSong.get(i).getPath());
            songList.add(newSong);
        }

    }

    public void addSongToTbView() throws TikaException, IOException, SAXException {
        for (int i = 0; i < songs.size(); i++) {
            String songName = getTitleSong(i, songs);
            Song newSong = new Song();
            newSong.setId(i + 1);
            newSong.setSongName(songName);
            newSong.setSongPath(songs.get(i).getPath());
            songList.add(newSong);
        }
    }

    public void showPlaylistOnComBox() {
        playlistBox.getItems().clear();
        playlistBox.setPromptText("Select PlayList");
        for (int j = 0; j < playlistList.size(); j++) {
            playlistBox.getItems().add(playlistList.get(j).getName());
        }
    }

    public String getSongTitle() {
        return songs.get(songNumber).getName();
    }

    public void getSongInfo(int songIndex) {
        String fileLocation = songs.get(songIndex).toPath().toString();
        try {
            InputStream input = new FileInputStream(fileLocation);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
            songName.setText(metadata.get("title"));
            singerName.setText(metadata.get("xmpDM:artist"));
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
    }

    public String getTitleSong(int indexSong, ArrayList<File> list) throws IOException, TikaException, SAXException {
        String titleSong;
        try {
            String fileLocation = list.get(indexSong).toPath().toString();
            InputStream input = new FileInputStream(fileLocation);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
            titleSong = metadata.get("title");
        } catch (Exception e) {
            e.printStackTrace();
            titleSong = " !Error Song";
        }
        return titleSong;
    }


    public void initMedia(int songIndex) throws TikaException, IOException, SAXException {
        media = new Media(songs.get(songIndex).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        getSongInfo(songNumber);
        setLogoSong();
    }

    public String getRelativeName() {
        String songNamePath = getSongTitle();
        String relativeSongName = songNamePath.substring(0, songNamePath.length() - 4);
        return relativeSongName + ".jpg";
    }

    public void setLogoSong() throws MalformedURLException {
        File file = new File("src/main/resources/vn/tqt/player/music/image/" + getRelativeName());
        String localUrl = file.toURI().toURL().toString();
        Image image = new Image(localUrl);
        logoSong.setImage(image);
    }

    public void playSong() throws MalformedURLException {
//        Song song = new Song("a", "b", "c");
//        String json = JacksonParser.INSTANCE.toJson(song);
//        System.out.println(json);
//        Song s1 = JacksonParser.INSTANCE.toObject(json, Song.class);
        if (playbtnstatus) {
            mediaPlayer.pause();
            cancelTimer();
            playbtnstatus = false;
            playButton.setText("Play");
        } else {
            mediaPlayer.play();
            getSongInfo(songNumber);
            beginTimer();
            playbtnstatus = true;
            playButton.setText("Pause");
        }
    }


    public void nextSong() throws IOException, TikaException, SAXException {
        if (songNumber < songs.size() - 1) {
            if (playbtnstatus) {
                playbtnstatus = false;
                songNumber++;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            } else {
                songNumber++;
                mediaPlayer.stop();
                initMedia(songNumber);
            }

        }
    }

    public void perviousSong() throws IOException, TikaException, SAXException {
        if (songNumber > 0) {
            if (playbtnstatus) {
                playbtnstatus = false;
                songNumber--;
                mediaPlayer.stop();
                initMedia(songNumber);
                playSong();
            } else {
                songNumber--;
                mediaPlayer.stop();
                initMedia(songNumber);
            }
        }
    }


    public void loopSong() {
        if (!loopbtnstatus) {
            loopbtnstatus = true;
            mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
            loopButton.setText("looping");
            if (randombtnstatus) {
                randombtnstatus = false;
                randomButton.setText("random");
            }
        } else {
            loopbtnstatus = false;
            loopButton.setText("loop");
        }

    }

    public void randomSong() {
        if (!randombtnstatus) {
            randombtnstatus = true;
            randomButton.setText("randoming");
            if (loopbtnstatus) {
                loopbtnstatus = false;
                loopButton.setText("loop");
            }
        } else {
            randombtnstatus = false;
            randomButton.setText("random");
        }
    }

    public int randomSongNumber() {
        if (randombtnstatus) {
            int max = songs.size() - 1;
            return (int) Math.floor(Math.random() * (max + 1));
        }
        return -1;
    }

    public void playRandomSong() throws TikaException, IOException, SAXException {
        mediaPlayer.stop();
        songNumber = randomSongNumber();
        initMedia(songNumber);
        mediaPlayer.play();
        getSongInfo(songNumber);
        beginTimer();
    }

    public void changeSpeed(ActionEvent event) {
        mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                running = true;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        double current = mediaPlayer.getCurrentTime().toSeconds();
                        double end = media.getDuration().toSeconds();
                        int second = (int) current % 60;
                        int minute = (int) (current / 60) % 60;
                        String minutes = String.valueOf(minute);
                        String seconds = String.valueOf(second);
                        songTime.setText(minutes + ":" + seconds);
                        songProgressBar.setProgress(current / end);
                        if (current / end == 1) {
                            if (randombtnstatus) {
                                try {
                                    playRandomSong();
                                } catch (TikaException | IOException | SAXException e) {
                                    e.printStackTrace();
                                }
                            }
                            cancelTimer();
                            try {
                                nextSong();
                            } catch (IOException | TikaException | SAXException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void cancelTimer() {
        running = false;
        timer.cancel();
    }

    public void createPlaylist() throws Exception {
        String forderPlaylistName = namePlaylist.getText();
        String sourcePath = playlistDirectory.getPath();
        File file = new File(sourcePath + "\\" + forderPlaylistName);
        Playlist newPlaylist = new Playlist();
        newPlaylist.setName(forderPlaylistName);
        playlistList.add(newPlaylist);
        namePlaylist.setText("");
        int sizePlaylist = playlistList.size();
        playlistBox.getItems().add(playlistList.get(sizePlaylist - 1).getName());
        boolean bool = file.mkdir();
        if (!bool) {
            throw new Exception("Error creat file");
        }
    }
    public void showPlaylistName() {
        String[] directories = playlistDirectory.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        for (String namePlayList : directories) {
            Playlist newPlaylist = new Playlist();
            newPlaylist.setName(namePlayList);
            playlistList.add(newPlaylist);
        }
    }

    public void playThisList() throws MalformedURLException {
        mediaPlayer.stop();
        sourcePathMusic = playlistDirectory.getPath() + "/" + playlistBox.getValue();
        initialize(null, null);
        playSong();
    }


    public void addToPlayList() {
        Song selected = table.getSelectionModel().getSelectedItem();
        String songPath = selected.getSongPath();
        String playlistName = playlistBox.getValue();
        copyMusicToDirectory(songPath, playlistName);

    }

    public void copyMusicToDirectory(String songPath, String playlistName) {
        File source = new File(songPath);
        File dest = new File("playlist/" + playlistName);
        try {
            FileUtils.copyFileToDirectory(source, dest);
            System.out.println("Xong");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void selectPlaylist(ActionEvent event) {

    }
}