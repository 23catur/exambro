package id.sch.smkn2cikbar.exambrowser;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import androidx.fragment.app.DialogFragment;

/**
 * Created by robby on 21/11/17.
 */

public class MyDialogFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tentang");
        builder.setMessage("Programmed by\nOktavianto Vian\n Catur Wira Syahputra Muchtar\n\nCopyright © 2023\nOm_Bot");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}
