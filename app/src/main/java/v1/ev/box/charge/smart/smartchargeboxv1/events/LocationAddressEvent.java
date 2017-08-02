package v1.ev.box.charge.smart.smartchargeboxv1.events;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deividas on 2017-04-10.
 */

public class LocationAddressEvent {
    public String address;

    public LocationAddressEvent(String data) {
        Log.d("SDSDGISDSD", data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            if(jsonObject.has("locationDetail")) {
                JSONObject locationDetail = jsonObject.getJSONObject("locationDetail");
                if(locationDetail.has("address")) {
                    address = locationDetail.getString("address");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
