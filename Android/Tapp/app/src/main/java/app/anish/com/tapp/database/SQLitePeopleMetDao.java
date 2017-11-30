package app.anish.com.tapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish_khattar25 on 11/28/17.
 */

public class SQLitePeopleMetDao implements PeopleMetDao{
    private static final String LOG_TAG = SQLitePeopleMetDao.class.getSimpleName();


    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private PeopleMetSQLiteHelper dbHelper;


    private static final String [] ALL_COLUMNS = {
            PeopleMetSQLiteHelper.COLUMN_NAME,
            PeopleMetSQLiteHelper.COLUMN_PHONE_NUMBER,
            PeopleMetSQLiteHelper.COLUMN_EMAIL,
            PeopleMetSQLiteHelper.COLUMN_DATE_MET,
            PeopleMetSQLiteHelper.COLUMN_FACEBOOK_ID,
            PeopleMetSQLiteHelper.COLUMN_LINKEDIN_ID
    };

    public SQLitePeopleMetDao(Context context) {
        dbHelper = new PeopleMetSQLiteHelper(context);
    }


    @Override
    public void createPerson(PersonMet personMet) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            ContentValues contentValues = personToContentValues(personMet);
            database.insert(PeopleMetSQLiteHelper.TABLE_PEOPLE_MET_INFO, null, contentValues);
        } finally {
            database.close();
        }
    }

    @Override
    public void deletePerson(PersonMet personMet) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            String phoneNumber = personMet.getPhoneNumber();
            String dWhereClause = String.format("%s = %s", PeopleMetSQLiteHelper.COLUMN_PHONE_NUMBER, phoneNumber);
            database.delete(PeopleMetSQLiteHelper.TABLE_PEOPLE_MET_INFO, dWhereClause, null);
        } finally {
            database.close();
        }
    }

    @Override
    public List<PersonMet> getAllPeopleMet() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        List<PersonMet> peopleMet = new ArrayList<>();
        try {
            Cursor cursor = database.query(PeopleMetSQLiteHelper.TABLE_PEOPLE_MET_INFO, ALL_COLUMNS, null, null, null, null,
                    PeopleMetSQLiteHelper.COLUMN_DATE_MET + " ASC");

            while(!cursor.isAfterLast()) {
                PersonMet personMet = cursorToPersonMet(cursor);
                peopleMet.add(personMet);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting all people met from database", e);
        } finally {
            database.close();
        }
        return peopleMet;
    }

    private ContentValues personToContentValues(PersonMet personMet) {
        ContentValues values = new ContentValues();
        values.put(PeopleMetSQLiteHelper.COLUMN_NAME, personMet.getName());
        values.put(PeopleMetSQLiteHelper.COLUMN_PHONE_NUMBER, personMet.getPhoneNumber());
        values.put(PeopleMetSQLiteHelper.COLUMN_EMAIL, personMet.getEmail());
        values.put(PeopleMetSQLiteHelper.COLUMN_DATE_MET, DATE_FORMAT.format(personMet.getDateMet()));
        values.put(PeopleMetSQLiteHelper.COLUMN_FACEBOOK_ID, personMet.getFacebookId());
        values.put(PeopleMetSQLiteHelper.COLUMN_LINKEDIN_ID, personMet.getLinkedInId());
        return values;
    }

    private PersonMet cursorToPersonMet(Cursor cursor) throws ParseException {
        return new PersonMet(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                DATE_FORMAT.parse(cursor.getString(3)),
                cursor.getString(4),
                cursor.getString(5)
        );
    }
}
