package app.anish.com.tapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by anish_khattar25 on 11/28/17.
 */

public class PeopleMetSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_PEOPLE_MET_INFO = "people_met_info";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_DATE_MET = "date_met";
    public static final String COLUMN_FACEBOOK_ID = "facebook_id";
    public static final String COLUMN_LINKEDIN_ID = "linkedin_id";

    // Database Details
    private static final String DATABASE_NAME = "people_met.db";
    private static final int DATABASE_VERSION = 1;


    private static final String DATABASE_CREATE =
            String.format("create table %s ( %s text not null, %s text " +
                    "primary key, %s text, %s text , %s text, %s text )",
                    TABLE_PEOPLE_MET_INFO, COLUMN_NAME, COLUMN_PHONE_NUMBER,
                    COLUMN_EMAIL, COLUMN_DATE_MET, COLUMN_FACEBOOK_ID, COLUMN_LINKEDIN_ID);


    public PeopleMetSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PeopleMetSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE_MET_INFO);
        onCreate(db);
        // TODO: save old data and then copy into new database
    }




}
