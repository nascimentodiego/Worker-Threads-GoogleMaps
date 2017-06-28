package br.com.dfn.samplegoogleplaces.communication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.dfn.samplegoogleplaces.model.Place;

public class Parser {

    public static final String KEY_RESULTS = "results";

    public static final List<Place> parseBytesToObject(byte[] bytes) {
        JSONObject jsonObject;
        List<Place> places = new ArrayList<>();

        try {
            jsonObject = parseStrToJson(new String(bytes));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            JSONArray results = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                String name = item.getString("name");
                String address = item.getString("vicinity");

                JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                String type = item.getJSONArray("types").getString(0);

                places.add(new Place(name, type, address, lat, lng));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


        return places;
    }

    private static JSONObject parseStrToJson(String str) throws JSONException {
        return new JSONObject(str);
    }

}
