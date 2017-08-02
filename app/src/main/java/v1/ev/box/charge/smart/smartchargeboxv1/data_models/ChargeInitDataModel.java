package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

/**
 * Created by Deividas on 2017-04-29.
 */

public class ChargeInitDataModel {
    private String chargeSessionId;
    private String userId;
    private String stationId;
    private long startTime;
    private long endTime;

    public String getChargeSessionId() {
        return chargeSessionId;
    }

    public void setChargeSessionId(String insertedID) {
        this.chargeSessionId = insertedID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
