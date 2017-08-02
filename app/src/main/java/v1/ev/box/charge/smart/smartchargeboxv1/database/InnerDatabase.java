package v1.ev.box.charge.smart.smartchargeboxv1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Deividas on 2017-04-22.
 */

public class InnerDatabase extends SQLiteOpenHelper {
    public static final String DB_NAME = "smart_charge_box.db";
    public static int DB_VERSION = 1;
    public static InnerDatabase instance;

    private InnerDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static InnerDatabase getInstance(Context context) {
        if(instance == null) {
            instance = new InnerDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String pendingRefTable = "CREATE TABLE " + ConstVals.pending_intents_table
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + ConstVals.intent_id
                + " INTEGER, " + ConstVals.reservationId + " TEXT, " + ConstVals.stationId + " TEXT, "
                + ConstVals.startTime + " LONG, " + ConstVals.endTime + " LONG, " + ConstVals.action + " STRING);";

        db.execSQL(pendingRefTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " +  ConstVals.pending_intents_table);
    }
}
