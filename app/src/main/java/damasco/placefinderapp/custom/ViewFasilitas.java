package damasco.placefinderapp.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import damasco.placefinderapp.R;
import damasco.placefinderapp.dao.DaoFasilitas;
import damasco.placefinderapp.dao.DaoKategori;
import damasco.placefinderapp.dao.DaoSubKategori;
import damasco.placefinderapp.entity.Fasilitas;
import damasco.placefinderapp.entity.Kategori;
import damasco.placefinderapp.entity.SubKategori;

/**
 * Created by Habib Mustofa on 12/11/2017.
 */

public class ViewFasilitas extends AlertDialog.Builder {

    public interface ViewFasilitasListener {
        void onCompleted();
    }

    private static final String TAG = ViewFasilitas.class.getSimpleName();
    private Context mContext;
    private DaoFasilitas dao;
    private Fasilitas mFasilitas;
    private ViewFasilitasListener listener;
    private Spinner mKategori, mSubKategori;
    private EditText mNamaFasilitas, mLatitude, mLongitude;
    private Dialog dialog;
    private DaoKategori daoKategori;
    private DaoSubKategori daoSubKategori;

    public ViewFasilitas(Context context, DaoFasilitas daoFasilitas, Fasilitas fasilitas, ViewFasilitasListener listener) {
        super(context);
        this.mContext = context;
        this.dao = daoFasilitas;
        this.mFasilitas = fasilitas;
        this.listener = listener;
        try {
            daoKategori = new DaoKategori(getContext());
            daoSubKategori = new DaoSubKategori(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setup();
    }

    private void setup() {
        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.view_add_edit_fasilitas, null, false);
        TextView mTitle = view.findViewById(R.id.fasilitas_title);
        mTitle.setText(mFasilitas != null ? "Edit Fasilitas" : "Tambah Fasilitas");
        mKategori = view.findViewById(R.id.fasilitas_input_kategori);
        mKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillSubKategori(((Kategori) parent.getItemAtPosition(position)).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSubKategori = view.findViewById(R.id.fasilitas_input_sub_kategori);
        mNamaFasilitas = view.findViewById(R.id.fasilitas_input_nama);
        mLatitude = view.findViewById(R.id.fasilitas_input_latitude);
        mLongitude = view.findViewById(R.id.fasilitas_input_longitude);
        Button mSimpan = view.findViewById(R.id.fasilitas_btn_save);
        TextView mBatal = view.findViewById(R.id.fasilitas_text_close);
        setView(view);
        setCancelable(false);
        fillKategori();
        fillSubKategori(null);
        if (mFasilitas != null) setFasilitas();
        dialog = this.create();
        mSimpan.setOnClickListener(v -> {
            simpan();
            Log.d(TAG, "setup: Button Save Clicked!");
        });
        mBatal.setOnClickListener(v -> {
            Log.d(TAG, "setup: Button Cancel Clicked!");
            dialog.dismiss();
        });
        dialog.show();
    }

    private Fasilitas getFasilitas() {
        Fasilitas f = new Fasilitas();
        f.setKategori(((Kategori)mKategori.getSelectedItem()).getName());
        f.setSubKategori(((SubKategori)mSubKategori.getSelectedItem()).getName());
        f.setNama(mNamaFasilitas.getText().toString());
        f.setLatitude(Double.parseDouble(mLatitude.getText().toString()));
        f.setLongitude(Double.parseDouble(mLongitude.getText().toString()));
        return f;
    }

    private void setFasilitas() {
        mKategori.setSelection(getKategoriIndex(mFasilitas.getKategori()));
        mSubKategori.setSelection(getSubKategoriIndex(mFasilitas.getKategori(), mFasilitas.getSubKategori()));
        mNamaFasilitas.setText(mFasilitas.getNama());
        mLatitude.setText(String.valueOf(mFasilitas.getLatitude()));
        mLongitude.setText(String.valueOf(mFasilitas.getLongitude()));
    }

    private int getKategoriIndex(String name) {
        List<Kategori> d = daoKategori.findAllKategori();
        for (int i = 0; i < d.size(); i++) {
            if (d.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return 0;
    }

    private int getSubKategoriIndex(String kategoriName, String name) {
        List<SubKategori> d = daoSubKategori.findAllSubKategori(kategoriName);
        for (int i = 0; i < d.size(); i++) {
            if(d.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return 0;
    }

    private void fillKategori() {
        List<Kategori> data = daoKategori.findAllKategori();
        SpinnerKategoriAdapter adapter = new SpinnerKategoriAdapter(getContext(), data);
        mKategori.setAdapter(adapter);
    }

    private void fillSubKategori(String kategori) {
        List<SubKategori> dataSub;
        if (kategori == null) {
            dataSub = daoSubKategori.findAllSubKategori();
        } else {
            dataSub = daoSubKategori.findAllSubKategori(kategori);
        }
        SpinnerSubKategoriAdapter adapterSub = new SpinnerSubKategoriAdapter(getContext(), dataSub);
        mSubKategori.setAdapter(adapterSub);
    }

    private boolean isEmpty() {
        if (mNamaFasilitas.getText().toString().isEmpty()) {
            mNamaFasilitas.setError("Tidak boleh kosong");
            return true;
        } else if (mLatitude.getText().toString().isEmpty()) {
            mLatitude.setError("Tidak boleh kosong");
            return true;
        } else if (mLongitude.getText().toString().isEmpty()) {
            mLongitude.setError("Tidak boleh kosong");
            return true;
        }
        return false;
    }

    private void simpan() {
        if (!isEmpty()) {
            try {
                Fasilitas fasilitas = getFasilitas();
                if (mFasilitas==null) {
                    fasilitas.setId(UUID.randomUUID().toString());
                    dao.insert(fasilitas);
                } else {
                    fasilitas.setId(mFasilitas.getId());
                    dao.update(fasilitas, "Id=?", new String[]{mFasilitas.getId()});
                }
                dialog.dismiss();
                if (listener != null) listener.onCompleted();
            } catch (Exception e) {
                Toast.makeText(getContext(),
                        "Terjadi kesalahan! Data tidak tersimpan. " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

}
