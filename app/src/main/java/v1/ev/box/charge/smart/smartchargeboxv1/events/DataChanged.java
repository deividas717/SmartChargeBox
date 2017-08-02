package v1.ev.box.charge.smart.smartchargeboxv1.events;

/**
 * Created by Deividas on 2017-03-25.
 */

public class DataChanged {
    public String collectionName;
    public String documentID;
    public String updatedValuesJson;
    public String removedValuesJson;

    public DataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {
        this.collectionName = collectionName;
        this.documentID = documentID;
        this.updatedValuesJson = updatedValuesJson;
        this.removedValuesJson = removedValuesJson;
    }
}
