package vn.tqt.player.music;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import vn.tqt.player.music.repository.KeyData;
import vn.tqt.player.music.services.JacksonParser;
import vn.tqt.player.music.services.RandomKeyGenerated;
import vn.tqt.player.music.services.SendKeyToMail;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LoginView implements Initializable {

    @FXML
    private TextField loginKeyField;
    private List<KeyData> listKeyAndMail  = new ArrayList<>();
    public void showHomePage(ActionEvent event) throws IOException {
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
    }

    public void sendKeyToEmail(ActionEvent event) throws MessagingException, UnsupportedEncodingException {
        String key = RandomKeyGenerated.randomString();
        String email = loginKeyField.getText();
        SendKeyToMail.send(email,key);
        KeyData newKey = new KeyData(key,email);
        listKeyAndMail.add(newKey);
        System.out.println(JacksonParser.INSTANCE.toJson(listKeyAndMail));
    }

    public void login(ActionEvent event) {
        String textFieldValue = loginKeyField.getText();
        if (textFieldValue.equals("a")){
            loginKeyField.setText(null);
            loginKeyField.setPromptText("Enter Your Key To Login");

        }
    }
}
