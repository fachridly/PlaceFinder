package damasco.placefinderapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.habibmustofa.simple.SQLite;

import java.util.ArrayList;
import java.util.List;

import damasco.placefinderapp.entity.Kategori;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */

public class DaoKategori extends SQLite<Kategori, String> {

    private static final String TAG = DaoKategori.class.getSimpleName();

    public DaoKategori(Context context) {
        super(context, new DBHelper(context), Kategori.class);
        Log.d(TAG, "DaoKategori: Created!");
    }

    public List<Kategori> findAllKategori() {
        List<Kategori> data = new ArrayList<>();
        try (Cursor c = findAll()) {
            if (c.moveToFirst()) {
                do {
                    data.add(new Kategori(c.getString(c.getColumnIndex("Id")),
                            c.getString(c.getColumnIndex("Name"))));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "findAllKategori: " + e.getLocalizedMessage());
        }
        return data;
    }
}
