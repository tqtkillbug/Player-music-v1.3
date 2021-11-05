package vn.tqt.player.music.services.jsonFile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import vn.tqt.player.music.repository.KeyData;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Read {
    public static void readJson(List<KeyData> list) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("data/keymail.json"));
        JSONArray array = (JSONArray) obj;
        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            String key = (String) jsonObject.get("key");
            String email = (String) jsonObject.get("email");
            KeyData newKey = new KeyData(key,email);
            list.add(newKey);
        }
    }
}
