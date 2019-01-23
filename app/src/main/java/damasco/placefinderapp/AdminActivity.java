package damasco.placefinderapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import damasco.placefinderapp.dao.DaoAdmin;
import damasco.placefinderapp.entity.Admin;

public class AdminActivity extends AppCompatActivity {

    private EditText mOldPassword, mNewPassword, mRepeatPassword;
    private DaoAdmin dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        try {
            dao = new DaoAdmin(this);
            findViewById(R.id.admin_btn_change).setOnClickListener(v -> changePassword());
            mOldPassword = findViewById(R.id.admin_old_password);
            mNewPassword = findViewById(R.id.admin_new_password);
            mRepeatPassword = findViewById(R.id.admin_repeat_password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changePassword() {
        String err = "Tidak boleh kosong!";
        if (mOldPassword.getText().toString().isEmpty()) {
            mOldPassword.setError(err);
        } else if (mNewPassword.getText().toString().isEmpty()) {
            mNewPassword.setError(err);
        } else if (mRepeatPassword.getText().toString().isEmpty()) {
            mRepeatPassword.setError(err);
        } else if (!dao.granted(mOldPassword.getText().toString())) {
            mOldPassword.setError("Password lama salah!");
        } else if (!mNewPassword.getText().toString().equals(mRepeatPassword.getText().toString())) {
            mRepeatPassword.setError("Pengulangan password tidak valid!");
        } else {
            try {
                dao.update(new Admin("KMZ", mRepeatPassword.getText().toString()), "Id=?", new String[]{"KMZ"});
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal ubah password! " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
