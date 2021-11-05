package vn.tqt.player.music;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vn.tqt.player.music.initWindow.InitAlertWindow;
import vn.tqt.player.music.repository.KeyData;
import vn.tqt.player.music.services.jsonFile.JacksonParser;
import vn.tqt.player.music.services.RandomKeyGenerated;
import vn.tqt.player.music.services.SendKeyToMail;
import vn.tqt.player.music.services.jsonFile.WriteJson;

import javax.mail.MessagingException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginView implements Initializable {
    @FXML
    private Label alertText;
    @FXML
    private TextField loginKeyField;
    public List<KeyData> listKeyAndMail  = new ArrayList<>();



    public void showHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(PlayerApp.class.getResource("player-view.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginKeyField.setPromptText("Enter Your Key To Login");
        try {
            readJson();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendKeyToEmail(ActionEvent event) throws MessagingException, IOException {
        String key = RandomKeyGenerated.randomString();
        String email = loginKeyField.getText();
        SendKeyToMail.send(email,key);
        KeyData newKey = new KeyData(key,email);
        listKeyAndMail.add(newKey);
        String jsonString = JacksonParser.INSTANCE.toJson(listKeyAndMail);
        WriteJson.setJsonFile(jsonString);
        String titleAlert = "Login Alert";
        String contentAlert = "Please check your email and get your key";
        InitAlertWindow.initAlert(titleAlert, contentAlert);
     }

   public void readJson() throws IOException, ParseException {
       JSONParser parser = new JSONParser();
           Object obj = parser.parse(new FileReader("data/keymail.json"));
           JSONArray array = (JSONArray) obj;
           for (int i = 0; i < array.size(); i++) {
               JSONObject jsonObject = (JSONObject) array.get(i);
               String key = (String) jsonObject.get("key");
               String email = (String) jsonObject.get("email");
               KeyData newKey = new KeyData(key,email);
               listKeyAndMail.add(newKey);
           }
   }
  public boolean checkKey(String key){
        for (int i = 0; i < listKeyAndMail.size() ; i++) {
         if (listKeyAndMail.get(i).getKey().equals(key)){
             return true;
         }
        }
        return false;
    }

    public void login(ActionEvent event) throws IOException {
        String inputKey = loginKeyField.getText();
      if (checkKey(inputKey)){
          Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(PlayerApp.class.getResource("player-view.fxml"));
          Parent parent = fxmlLoader.load();
          Scene scene = new Scene(parent);
          stage.setScene(scene);
          stage.show();
      } else {
          String titleAlert = "Login Fail";
          String contentAlert = "Key incorrect please re-enter or enter email to receive Key";
          InitAlertWindow.initAlert(titleAlert, contentAlert);
          alertText.setText("Key does not exist,Please enter your email to receive Key" );
      }
    }
}
