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
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import damasco.placefinderapp.R;
import damasco.placefinderapp.custom.ListFasilitasAdapter;
import damasco.placefinderapp.custom.Popup;
import damasco.placefinderapp.custom.ViewFasilitas;
import damasco.placefinderapp.dao.DaoFasilitas;
import damasco.placefinderapp.dao.DaoSubKategori;
import damasco.placefinderapp.entity.Fasilitas;

public class FasilitasController extends Fragment {

    private static final String TAG = FasilitasController.class.getSimpleName();
    private DaoFasilitas dao;
    private DaoSubKategori daoSubKategori;
    private ListView mListView;

    public static FasilitasController newInstance() {
        Log.d(TAG, "newInstance: FasilitasController");
        return new FasilitasController();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fasilitas_controller, container, false);
        try {
            dao = new DaoFasilitas(getActivity());
            daoSubKategori = new DaoSubKategori(getActivity());
            mListView = view.findViewById(R.id.fasilitas_list);
            mListView.setOnItemLongClickListener(action);
            loadFasilitas();
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
                if (daoSubKategori.findAllSubKategori().size() > 0) {
                    // Add Fasilitas
                    new ViewFasilitas(getActivity(), dao, null, this::loadFasilitas);
                } else {
                    Popup.show(getContext(), "Peringatan","Harap tambahkan data Sub Kategori terlebih dahulu!");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFasilitas() {
        List<Fasilitas> data = dao.findAllFasilitas();
        mListView.setAdapter(new ListFasilitasAdapter(this.getContext(), data));
    }

    private AdapterView.OnItemLongClickListener action = (parent, view, position, id) -> {
        Fasilitas fasilitas = (Fasilitas) parent.getAdapter().getItem(position);
        Popup.create(getContext()).setTitle("Opsi Data")
                .setPositiveButton("Edit", (dialog, which) ->
                        /*Edit Fasilitas*/ new ViewFasilitas(getActivity(), dao, fasilitas, this::loadFasilitas))
                .setNegativeButton("Hapus", (dialog, which) -> deleteFasilitas(fasilitas)).show();
        return true;
    };

    private void deleteFasilitas(Fasilitas fasilitas) {
        Popup.create(getContext()).setTitle("Hapus")
                .setMessage("Anda yakin ingin menghapus data terpilih?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    try {
                        dao.delete("Id=?", new String[]{fasilitas.getId()});
                        loadFasilitas();
                        Toast.makeText(getContext(), "Data telah di hapus", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Gagal di hapus! " + e.getLocalizedMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

}
