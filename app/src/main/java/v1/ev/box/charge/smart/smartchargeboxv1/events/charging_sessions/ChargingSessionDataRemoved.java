package v1.ev.box.charge.smart.smartchargeboxv1.events.charging_sessions;

/**
 * Created by Deividas on 2017-04-29.
 */

public class ChargingSessionDataRemoved {
    public String collectionName;
    public String documentID;

    public ChargingSessionDataRemoved(String collectionName, String documentID) {
        this.collectionName = collectionName;
        this.documentID = documentID;
    }
}
