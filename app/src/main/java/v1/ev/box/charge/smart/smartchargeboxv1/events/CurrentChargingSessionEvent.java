package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-04-30.
 */

public class CurrentChargingSessionEvent {
    private boolean fromMenu;
    private String id;
    private String userId;
    private String stationId;
    private long startTime;
    private long endTime;
    private boolean actualCharging;
    private String reservationId;

    public String getReservationId() {
        return reservationId;
    }

    public void setFromMenu(boolean fromMenu) {
        this.fromMenu = fromMenu;
    }

    public boolean isFromMenu() {
        return fromMenu;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isActualCharging() {
        return actualCharging;
    }

    public void setActualCharging(boolean actualCharging) {
        this.actualCharging = actualCharging;
    }
}
