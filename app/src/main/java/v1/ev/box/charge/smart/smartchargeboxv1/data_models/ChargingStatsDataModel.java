package v1.ev.box.charge.smart.smartchargeboxv1.data_models;

import java.util.ArrayList;

/**
 * Created by Deividas on 2017-05-04.
 */

public class ChargingStatsDataModel {
    public ArrayList<ChargingStatsDataModelInnerClass> list;
    public static class ChargingStatsDataModelInnerClass {
        public int month;
        public int day;
        public long time;
    }
}
