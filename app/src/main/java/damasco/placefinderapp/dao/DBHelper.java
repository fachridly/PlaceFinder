package damasco.placefinderapp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.habibmustofa.simple.SQLiteTable;

import java.util.List;

import damasco.placefinderapp.entity.Admin;
import damasco.placefinderapp.entity.Fasilitas;
import damasco.placefinderapp.entity.Kategori;
import damasco.placefinderapp.entity.SubKategori;

/**
 * Created by Habib Mustofa on 11/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    DBHelper(Context context) {
        super(context, "Database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteTable.queryCreate(Admin.class));
        db.execSQL(SQLiteTable.queryCreate(Fasilitas.class));
        db.execSQL(SQLiteTable.queryCreate(Kategori.class));
        db.execSQL(SQLiteTable.queryCreate(SubKategori.class));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQLiteTable.queryDrop(Admin.class));
        db.execSQL(SQLiteTable.queryDrop(Fasilitas.class));
        db.execSQL(SQLiteTable.queryDrop(Kategori.class));
        db.execSQL(SQLiteTable.queryDrop(SubKategori.class));
        onCreate(db);
    }
}