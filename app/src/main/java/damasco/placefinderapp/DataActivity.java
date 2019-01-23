package damasco.placefinderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import damasco.placefinderapp.controller.FasilitasController;
import damasco.placefinderapp.controller.KategoriController;
import damasco.placefinderapp.controller.SubKategoriController;

public class DataActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_fasilitas:
                setTitle("Data Fasilitas");
                switchFragment(FasilitasController.newInstance());
                return true;
            case R.id.navigation_kategori:
                setTitle("Data Kategori");
                switchFragment(KategoriController.newInstance());
                return true;
            case R.id.navigation_sub_kategori:
                setTitle("Data Sub Kategori");
                switchFragment(SubKategoriController.newInstance());
                return true;
        }
        return false;
    };

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.data_parent_fragment, fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        switchFragment(FasilitasController.newInstance());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DataActivity.this, MapsActivity.class));
        finish();
    }
}
