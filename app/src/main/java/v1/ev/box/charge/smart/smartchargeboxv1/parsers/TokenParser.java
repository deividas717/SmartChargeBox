package v1.ev.box.charge.smart.smartchargeboxv1.parsers;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.events.AuthTokenEvent;

/**
 * Created by Deividas on 2017-04-17.
 */

public class TokenParser extends AsyncTask<String, Void, AuthTokenEvent> {
    @Override
    protected AuthTokenEvent doInBackground(String... strings) {
        AuthTokenEvent authToken = new AuthTokenEvent();
        try {
            Log.d("sfusffdgdfg", strings[0]);
            JSONObject jsonObject = new JSONObject(strings[0]);
            if(jsonObject.has("name")) {
                String name = jsonObject.getString("name");
                authToken.setName(name);
            }
            if(jsonObject.has("token")) {
                String token = jsonObject.getString("token");
                authToken.setToken(token);
            }
        } catch (JSONException e) {
            Log.d("SDSGDSD", e.getMessage());
            //EventBus.getDefault().post(authToken);
            e.printStackTrace();
        }
        return authToken;
    }

    @Override
    protected void onPostExecute(AuthTokenEvent authTokenEvent) {
        super.onPostExecute(authTokenEvent);

        EventBus.getDefault().post(authTokenEvent);
    }
}
