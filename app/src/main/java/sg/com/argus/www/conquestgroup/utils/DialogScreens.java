package sg.com.argus.www.conquestgroup.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sg.com.argus.www.conquestgroup.R;

public class DialogScreens {


    public static void showExitAlert(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_exit, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final Button no = (Button) promptView
                .findViewById(R.id.exit_no_btn);

        final Button yes = (Button) promptView
                .findViewById(R.id.exit_yes_btn);

        final TextView txt = (TextView) promptView
                .findViewById(R.id.exit_prompt_textview);

        txt.setText(R.string.exit_dialog_prompt);

        alertDialogBuilder.setView(promptView).setCancelable(true);

        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        no.setOnClickListener(v -> {
            // cancel dialog
            alert.cancel();
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                android.os.Process.killProcess(android.os.Process.myPid());
                // finish();

            }
        });

    }


}
