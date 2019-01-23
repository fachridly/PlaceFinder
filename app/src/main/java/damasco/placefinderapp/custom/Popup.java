package damasco.placefinderapp.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by habib on 13/08/17.
 */

public class Popup {

    public static AlertDialog.Builder create(Context context) {
        return new AlertDialog.Builder(context);
    }

    public static void show(Context context, String title, String message) {
        create(context)
                .setTitle(title).setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
    }

}
