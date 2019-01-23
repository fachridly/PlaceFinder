package damasco.placefinderapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.habibmustofa.simple.SQLite;

import damasco.placefinderapp.entity.Admin;

/**
 * Created by Habib Mustofa on 09/11/2017.
 */

public class DaoAdmin extends SQLite<Admin, String> {

    public DaoAdmin(Context context) {
        super(context, new DBHelper(context), Admin.class);
    }

    public boolean granted(String password) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE Password=?";
        try(SQLiteDatabase db = getWritableDatabase(); Cursor c = db.rawQuery(sql, new String[]{password})) {
            if(c.moveToFirst()) {
                return true;
            }
        }
        return false;
    }

    public boolean isNotEmpty() {
        String sql = "SELECT * FROM " + getTableName();
        try(SQLiteDatabase db = getWritableDatabase(); Cursor c = db.rawQuery(sql, null)) {
            if(c.moveToFirst()) {
                return true;
            }
        }
        return false;
    }
}
