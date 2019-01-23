package damasco.placefinderapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.habibmustofa.gmapstools.util.PermissionHelper;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import damasco.placefinderapp.custom.Popup;
import damasco.placefinderapp.custom.ViewPlaceFinder;
import damasco.placefinderapp.dao.DaoAdmin;

@SuppressWarnings("deprecation")
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationChangeListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private PermissionHelper mPermissionHelper;
    private LatLng mSouthTangerang = new LatLng(-6.283331, 106.711233);
    private LatLng mMyLocation;
    private String[] mPermissions;
    private static final int LOC_PERMISSION_CODE = 200;
    private boolean mMapReady = false;
    private DaoAdmin dao;
    private Dialog dialogAkses;
    private boolean pressed = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
            dao = new DaoAdmin(this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mendapatkan lokasi anda...");
        findViewById(R.id.maps_image_button_search).setOnClickListener(v -> {
            if (mMapReady && mMyLocation != null) {
                new ViewPlaceFinder(MapsActivity.this, mMap, mMyLocation).start();
            } else {
                progressDialog.show();
            }
        });
        mPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.requestPermissions(mPermissions, LOC_PERMISSION_CODE);
    }

    @Override
    public void onBackPressed() {
        if (pressed) {
            super.onBackPressed();
            return;
        }
        this.pressed = true;
        Toast.makeText(this, "Tekan sekali lagi untuk keluar aplikasi", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> pressed = false, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults.length <= 0) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission interrupted!");
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goSouthTangerang();
                } else {
                    Log.e(TAG, "onRequestPermissionsResult: Permission Denied!");
                }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mPermissionHelper.checkPermission(mPermissions)) {
            mMap.setMyLocationEnabled(true);
        }
        mMapReady = true;
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMyLocationChangeListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void goSouthTangerang() {
        if (mPermissionHelper.checkPermission(mPermissions)) {
            if (mPermissionHelper.isGpsEnabled()) {
                Log.d(TAG, "goSouthTangerang: GPS Enabled");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mSouthTangerang, 12f));
            } else {
                mPermissionHelper.showDialogLocationSetting();
                Log.e(TAG, "goSouthTangerang: GPS Disabled");
            }
        } else {
            mPermissionHelper.requestPermissions(mPermissions, LOC_PERMISSION_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_database:
                toDatabase();
                break;
            case R.id.menu_password:
                startActivity(new Intent(MapsActivity.this, AdminActivity.class));
                break;
            case R.id.menu_about:
                Popup.show(this, "Tentang", "Aplikasi pencarian lokasi fasilitas " +
                        "umum terdekat dengan rute tercepat menggunakan algoritma haversine bebasis android\n\n" +
                        "Oleh:\nWita Andriani\nFakultas Sains dan Teknologi\n\nUniversitas Islam Negeri Syarif Hidayatullah Jakarta");
                break;
            default:
                break;
        }
        return true;
    }

    private void toDatabase() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText pw = new EditText(this);
        pw.setHint("Password");
        pw.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final TextView btn = new TextView(this);
        btn.setText(R.string.btn_login_ok);
        btn.setTextColor(getResources().getColor(R.color.colorAccent));
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(40, 0, 40, 0);
        btn.setOnClickListener(v -> {
            String nameValue = pw.getText().toString();
            if (nameValue.isEmpty()) {
                pw.setError("Tidak boleh kosong!");
            } else {
                if (dao.granted(nameValue)) {
                    startActivity(new Intent(MapsActivity.this, DataActivity.class));
                    dialogAkses.dismiss();
                    finish();
                } else {
                    pw.setError("Password salah!");
                }
            }
        });
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(30, 10, 30, 20);
        layout.addView(pw, p);
        layout.addView(btn, p);
        dialogAkses = Popup.create(this).setTitle("Masukan Password").setView(layout).create();
        dialogAkses.show();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
        if(mMapReady) mMyLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onMyLocationChange(Location location) {
        if(location != null) {
            progressDialog.dismiss();
            mMyLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }
}