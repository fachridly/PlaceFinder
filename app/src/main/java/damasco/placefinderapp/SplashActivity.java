package damasco.placefinderapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import damasco.placefinderapp.dao.DaoAdmin;
import damasco.placefinderapp.entity.Admin;

public class SplashActivity extends AppCompatActivity {

    private static final byte[] APP_SETTING = new byte[]{-11, -119, 0, -72, -43, 100, -35, 26, -73, -18, 102, -92, -84, 105, -1, -57};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setup();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MapsActivity.class));
            finish();
        }, 2500);
        setDefaultPassword();
    }

    private void setDefaultPassword() {
        try {
            DaoAdmin d = new DaoAdmin(this);
            if (!d.isNotEmpty()) {
                d.insert(new Admin("KMZ", "admin"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setup() {
        String dir = getFilesDir().getAbsolutePath() + '/';
        String name = new String(new byte[]{112, 114, 111, 100, 117, 99, 116, 45, 107, 101, 121});
//        if (new File(dir, name).exists()) {
//            return;
//        }
        try (FileOutputStream stream = new FileOutputStream(dir + name)) {
            stream.write(APP_SETTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
