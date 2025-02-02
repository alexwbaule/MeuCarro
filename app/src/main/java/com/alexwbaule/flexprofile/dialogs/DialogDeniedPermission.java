package com.alexwbaule.flexprofile.dialogs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.alexwbaule.flexprofile.R;
import com.alexwbaule.flexprofile.activity.BaseActivity;

/**
 * Created by alex on 14/05/16.
 */
public class DialogDeniedPermission extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.denied_permission)
                .setMessage(R.string.permission_explanation)
                .setPositiveButton(R.string.allow, clickListener);

        return builder.create();
    }

    private DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION}, BaseActivity.MY_PERMISSION_LIST);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dismiss();
                    break;
            }
        }
    };
}
