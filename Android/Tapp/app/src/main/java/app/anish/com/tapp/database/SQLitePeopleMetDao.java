package app.anish.com.tapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            PeopleMetSQLiteHelper.COLUMN_LINKEDIN_ID,
            PeopleMetSQLiteHelper.COLUMN_PERSON_IMAGE
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
            Log.i(LOG_TAG, "successfully saved " + personMet + " to database");
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
                // skip first cursor because it contains the column names
                if (cursor.isFirst())  {
                    PersonMet personMet = cursorToPersonMet(cursor);
                    peopleMet.add(personMet);
                }
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
        String name = cursor.getString(0);
        String phoneNumber = cursor.getString(1);
        String email = cursor.getString(2);
        Date dateMet = DATE_FORMAT.parse(cursor.getString(3));
        String facebookId = cursor.getString(4);
        String linkedIn = cursor.getString(5);

        //parse blob
        byte [] imgBytArr = cursor.getBlob(6);
        Bitmap bitmap = imgBytArr == null ? null : BitmapFactory.decodeByteArray(imgBytArr, 0, imgBytArr.length);
        return new PersonMet(name,phoneNumber,email, dateMet, facebookId, linkedIn, bitmap);
    }
}
