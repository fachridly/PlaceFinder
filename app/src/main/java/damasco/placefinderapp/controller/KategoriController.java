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
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import damasco.placefinderapp.R;
import damasco.placefinderapp.custom.ListKategoriAdapter;
import damasco.placefinderapp.custom.Popup;
import damasco.placefinderapp.dao.DaoKategori;
import damasco.placefinderapp.entity.Kategori;

public class KategoriController extends Fragment {

    private static final String TAG = KategoriController.class.getSimpleName();
    private ListView mListView;
    private DaoKategori dao;

    public static KategoriController newInstance() {
        Log.d(TAG, "newInstance: KategoriController");
        return new KategoriController();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kategori_controller, container, false);
        try {
            dao = new DaoKategori(this.getActivity());
            mListView = view.findViewById(R.id.kategori_list);
            mListView.setOnItemLongClickListener(delete);
            loadKategori();
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
                addKategori();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadKategori() {
        List<Kategori> data = dao.findAllKategori();
        mListView.setAdapter(new ListKategoriAdapter(this.getContext(), data));
    }


    private void addKategori() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText name = new EditText(getContext());
        name.setHint("Nama Kategori");
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(30, 20, 30, 0);
        layout.addView(name, p);
        Popup.create(getContext()).setTitle("Tambah Kategori").setView(layout)
                .setCancelable(false)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String nameValue = name.getText().toString();
                    if (nameValue.isEmpty()) {
                        Popup.show(getContext(), "Peringatan","Data tidak boleh kosong!");
                    } else {
                        try {
                            dao.insert(new Kategori(UUID.randomUUID().toString(), nameValue));
                            loadKategori();
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
                            Kategori kategori = (Kategori) parent.getAdapter().getItem(position);
                            dao.delete("Id=?", new String[]{kategori.getId()});
                            loadKategori();
                            Toast.makeText(getContext(), "Data telah di hapus", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Gagal di hapus! " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Tidak", null).show();
            return true;
        }
    };

}
