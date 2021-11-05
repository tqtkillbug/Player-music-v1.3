package vn.tqt.player.music.services.jsonFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteJson {
  public static void setJsonFile(String jsonString) throws IOException {
      File jsonFile = new File("data/keymail.json");
      FileWriter jsonWriter = new FileWriter(jsonFile);
      jsonWriter.write(String.valueOf(jsonString));
      jsonWriter.close();
  }
}
