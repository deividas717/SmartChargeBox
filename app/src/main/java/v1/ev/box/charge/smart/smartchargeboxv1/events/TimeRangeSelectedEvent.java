package v1.ev.box.charge.smart.smartchargeboxv1.events;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import v1.ev.box.charge.smart.smartchargeboxv1.preferences.PreferencesManager;

/**
 * Created by Deividas on 2017-03-29.
 */

public class TimeRangeSelectedEvent {
    public String startHour;
    public String startMin;
    public String endHour;
    public String endMin;

    private String startDay, endDay;
    private String startMonth, endMonth;
    private String startYear, endYear;

    private JSONObject jsonObject;


    public TimeRangeSelectedEvent(String id, String startYear, String startMonth, String startDay,
                                  int startHour, int startMin, String endYear, String endMonth,
                                  String endDay, int endHour, int endMin) {
        this.startHour = getFormatNumber(Integer.toString(startHour));
        this.startMin = getFormatNumber(Integer.toString(startMin));
        this.endHour = getFormatNumber(Integer.toString(endHour));
        this.endMin = getFormatNumber(Integer.toString(endMin));

        this.startYear = startYear;
        this.startMonth = getFormatNumber(startMonth);
        this.startDay = getFormatNumber(startDay);

        this.endYear = endYear;
        this.endMonth = getFormatNumber(endMonth);
        this.endDay = getFormatNumber(endDay);

        try {
            jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("start_date", this.startYear + "-" + this.startMonth + "-" + this.startDay + "T" + this.startHour + ":" + this.startMin + ":00.000Z");
            jsonObject.put("expire_date", this.endYear + "-" + this.endMonth + "-" + this.endDay + "T" + this.endHour + ":" + this.endMin + ":00.000Z");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getFormatNumber(String number) {
        if(number.length() == 2) {
            return number;
        }
        return "0" + number;
    }

    public void setToken(String token) {
        try {
            jsonObject.put("user_token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getReserveString() {
        return jsonObject.toString();
    }
}