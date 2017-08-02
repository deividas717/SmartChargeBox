package v1.ev.box.charge.smart.smartchargeboxv1;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;

/**
 * Created by Deividas on 2017-05-02.
 */

public class OCPI {
    private static OCPI ocpi;
    private WeakReference<Context> contextWeakReference;

    public static OCPI getInstance(Context context) {
        if(ocpi == null) {
            ocpi = new OCPI(context);
        }
        return ocpi;
    }

    private OCPI(Context context) {
        contextWeakReference = new WeakReference<Context>(context);
    }

    public String reserveNow(String auth_id, long startDate, long expireDate, String locationId) {
        String result = null;
        try {
            InputStream in_s = contextWeakReference.get().getAssets().open("reservenow.json");
            byte[] b;
            try {
                b = new byte[in_s.available()];
                in_s.read(b);
                String str = new String(b);
                JSONObject obj = new JSONObject(str);
                JSONObject token = obj.getJSONObject("token");
                token.put("auth_id", auth_id);
                obj.put("start_date", startDate);
                obj.put("expiry_date", expireDate);
                obj.put("location_id", locationId);
                result = obj.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
