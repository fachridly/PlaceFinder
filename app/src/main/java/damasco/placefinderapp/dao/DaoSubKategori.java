package damasco.placefinderapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.habibmustofa.simple.SQLite;

import java.util.ArrayList;
import java.util.List;

import damasco.placefinderapp.entity.SubKategori;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */

public class DaoSubKategori extends SQLite<SubKategori, String> {

    private static final String TAG = DaoSubKategori.class.getSimpleName();

    public DaoSubKategori(Context context) {
        super(context, new DBHelper(context), SubKategori.class);
    }

    public List<SubKategori> findAllSubKategori() {
        List<SubKategori> data = new ArrayList<>();
        try (Cursor c = findAll()) {
            if (c.moveToFirst()) {
                do {
                    data.add(new SubKategori(c.getString(c.getColumnIndex("Id")),
                            c.getString(c.getColumnIndex("Name")), c.getString(c.getColumnIndex("Kategori"))));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "findAllSubKategori: " + e.getLocalizedMessage());
        }
        return data;
    }

    public List<SubKategori> findAllSubKategori(String kategori) {
        List<SubKategori> data = new ArrayList<>();
        try (SQLiteDatabase db = getWritableDatabase();
             Cursor c = db.rawQuery("SELECT * FROM " + getTableName() + " WHERE kategori=?" , new String[]{kategori});) {
            if (c.moveToFirst()) {
                do {
                    data.add(new SubKategori(c.getString(c.getColumnIndex("Id")),
                            c.getString(c.getColumnIndex("Name")), c.getString(c.getColumnIndex("Kategori"))));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "findAllSubKategori: " + e.getLocalizedMessage());
        }
        return data;
    }
}
