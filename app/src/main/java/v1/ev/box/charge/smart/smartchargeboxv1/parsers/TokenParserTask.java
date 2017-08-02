package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deividas on 2017-04-16.
 */

//todo pakeisti nes cia tokena reikia parsinti
public class TokenParserTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... strings) {
        try {
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if(dataObject.has("name")) {
                    String name = dataObject.getString("name");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


    }
}
