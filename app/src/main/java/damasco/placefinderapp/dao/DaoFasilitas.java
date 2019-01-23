package damasco.placefinderapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.habibmustofa.simple.SQLite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import damasco.placefinderapp.entity.Fasilitas;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */

public class DaoFasilitas extends SQLite<Fasilitas, String> {

    private static final String TAG = DaoFasilitas.class.getSimpleName();

    public DaoFasilitas(Context context) {
        super(context, new DBHelper(context), Fasilitas.class);
    }

    public List<Fasilitas> findAllFasilitas() {
        List<Fasilitas> data = new ArrayList<>();
        try (Cursor c = findAll()) {
            if (c.moveToFirst()) {
                do {
                    Fasilitas fasilitas = new Fasilitas();
                    fasilitas.setId(c.getString(c.getColumnIndex("Id")));
                    fasilitas.setNama(c.getString(c.getColumnIndex("Nama")));
                    fasilitas.setKategori(c.getString(c.getColumnIndex("Kategori")));
                    fasilitas.setSubKategori(c.getString(c.getColumnIndex("SubKategori")));
                    fasilitas.setLatitude(c.getDouble(c.getColumnIndex("Latitude")));
                    fasilitas.setLongitude(c.getDouble(c.getColumnIndex("Longitude")));
                    data.add(fasilitas);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "findAllFasilitas: " + e.getLocalizedMessage());
        }
        return data;
    }

    public List<Fasilitas> findAllFasilitas(String subKategori) {
        List<Fasilitas> data = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " WHERE SubKategori=?";
        try (SQLiteDatabase db = getWritableDatabase(); Cursor c = db.rawQuery(sql, new String[]{subKategori})) {
            if (c.moveToFirst()) {
                do {
                    Fasilitas fasilitas = new Fasilitas();
                    fasilitas.setId(c.getString(c.getColumnIndex("Id")));
                    fasilitas.setNama(c.getString(c.getColumnIndex("Nama")));
                    fasilitas.setKategori(c.getString(c.getColumnIndex("Kategori")));
                    fasilitas.setSubKategori(c.getString(c.getColumnIndex("SubKategori")));
                    fasilitas.setLatitude(c.getDouble(c.getColumnIndex("Latitude")));
                    fasilitas.setLongitude(c.getDouble(c.getColumnIndex("Longitude")));
                    data.add(fasilitas);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "findAllFasilitas: " + e.getLocalizedMessage());
        }
        return data;
    }

    public List<Fasilitas> findAllFasilitasById(String[] ids) {
        List<Fasilitas> data = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName() + " WHERE";
        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        for (int i = 0; i < ids.length; i++) {
            sb.append(" Id=? ");
            if (i != ids.length -1) {
                sb.append("OR");
            }
        }
        Log.w(TAG, "findAllFasilitasById: QUERY:" + sb.toString() + " IDS:" + Arrays.toString(ids));
        try (SQLiteDatabase db = getWritableDatabase(); Cursor c = db.rawQuery(sb.toString(), ids)) {
            if (c.moveToFirst()) {
                do {
                    Fasilitas fasilitas = new Fasilitas();
                    fasilitas.setId(c.getString(c.getColumnIndex("Id")));
                    fasilitas.setNama(c.getString(c.getColumnIndex("Nama")));
                    fasilitas.setKategori(c.getString(c.getColumnIndex("Kategori")));
                    fasilitas.setSubKategori(c.getString(c.getColumnIndex("SubKategori")));
                    fasilitas.setLatitude(c.getDouble(c.getColumnIndex("Latitude")));
                    fasilitas.setLongitude(c.getDouble(c.getColumnIndex("Longitude")));
                    data.add(fasilitas);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "findAllFasilitas: " + e.getLocalizedMessage());
        }
        return data;
    }
}
