package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

/**
 * Created by Deividas on 2017-04-27.
 */

public class ReservationCollapsesDataModel {
    private String reservationId;
    private long startTime;
    private long endTime;
    private String stationId;
    private String userId;

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime - 10800000;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime - 10800000;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
