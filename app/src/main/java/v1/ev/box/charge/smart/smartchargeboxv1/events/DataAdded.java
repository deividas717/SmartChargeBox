package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-03-25.
 */

public class DataAdded {
    public String collectionName;
    public String documentID;
    public String newValuesJson;

    public DataAdded(String collectionName, String documentID, String newValuesJson) {
        this.collectionName = collectionName;
        this.documentID = documentID;
        this.newValuesJson = newValuesJson;
    }
}
