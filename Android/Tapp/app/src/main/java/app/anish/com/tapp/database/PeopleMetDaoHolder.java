package app.anish.com.tapp.database;

import android.content.Context;

/**
 * Created by anish_khattar25 on 11/30/17.
 */

public class PeopleMetDaoHolder {

    private static PeopleMetDao dao;

    public static void createDaoInstance(Context context) {
        if (dao == null) {
            dao = new SQLitePeopleMetDao(context);
        }
    }

    public static PeopleMetDao getDaoInstance() {
        return dao;
    }
}
