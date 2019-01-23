package damasco.placefinderapp.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import damasco.placefinderapp.R;
import damasco.placefinderapp.custom.ListSubKategoriAdapter;
import damasco.placefinderapp.custom.Popup;
import damasco.placefinderapp.custom.SpinnerKategoriAdapter;
import damasco.placefinderapp.dao.DaoKategori;
import damasco.placefinderapp.dao.DaoSubKategori;
import damasco.placefinderapp.entity.Kategori;
import damasco.placefinderapp.entity.SubKategori;

public class SubKategoriController extends Fragment {

    private static final String TAG = SubKategoriController.class.getSimpleName();
    private ListView mListView;
    private DaoSubKategori dao;
    private DaoKategori daoKategori;

    public static SubKategoriController newInstance() {
        Log.d(TAG, "newInstance: SubKategoriController");
        return new SubKategoriController();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_kategori_controller, container, false);
        try {
            dao = new DaoSubKategori(this.getActivity());
            daoKategori = new DaoKategori(getActivity());
            mListView = view.findViewById(R.id.sub_kategori_list);
            mListView.setOnItemLongClickListener(delete);
            loadSubKategori();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.data_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_new:
                if (daoKategori.findAllKategori().size() > 0) {
                    addSubKategori();
                } else {
                    Popup.show(getContext(), "Peringatan","Harap tambahkan data Kategori terlebih dahulu!");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSubKategori() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText name = new EditText(getContext());
        final Spinner kategori = new Spinner(getContext());
        name.setHint("Nama Sub Kategori");
        List<Kategori> data = daoKategori.findAllKategori();
        SpinnerKategoriAdapter adapter = new SpinnerKategoriAdapter(getContext(), data);
        kategori.setAdapter(adapter);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(30, 20, 30, 0);
        layout.addView(name, p);
        layout.addView(kategori, p);
        Popup.create(getContext()).setTitle("Tambah Sub Kategori").setView(layout)
                .setCancelable(false)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String nameValue = name.getText().toString();
                    String kategoriValue = ((Kategori) kategori.getSelectedItem()).getName();
                    if (nameValue.isEmpty() || kategoriValue.isEmpty()) {
                        Popup.show(getContext(), "Peringatan", "Data tidak boleh kosong!");
                    } else {
                        try {
                            dao.insert(new SubKategori(UUID.randomUUID().toString(), nameValue, kategoriValue));
                            loadSubKategori();
                            Toast.makeText(getContext(), "Data tersimpan", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Gagal di simpan! " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss()).show();
    }

    private AdapterView.OnItemLongClickListener delete = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
            Popup.create(getContext()).setTitle("Hapus")
                    .setMessage("Anda yakin ingin menghapus data terpilih?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        try {
                            SubKategori subKategori = (SubKategori) parent.getAdapter().getItem(position);
                            dao.delete("Id=?", new String[]{subKategori.getId()});
                            loadSubKategori();
                            Toast.makeText(getContext(), "Data telah di hapus", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Gagal di hapus! " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss()).show();
            return true;
        }
    };

    private void loadSubKategori() {
        List<SubKategori> data = dao.findAllSubKategori();
        mListView.setAdapter(new ListSubKategoriAdapter(this.getContext(), data));
    }
}
