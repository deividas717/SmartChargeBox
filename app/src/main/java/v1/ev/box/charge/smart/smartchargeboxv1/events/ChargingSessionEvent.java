package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-04-29.
 */

public class ChargingSessionEvent {
    private String id;

    public ChargingSessionEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
