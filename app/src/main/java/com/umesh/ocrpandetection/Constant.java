package com.umesh.ocrpandetection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class Constant {

    public static void ShowAlertDialog(Context context, String msg, String title, String positiveButton, String negativeButton, DialogInterface.OnClickListener positiveOnClickListener, DialogInterface.OnClickListener negativeOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        if(title != null) builder.setTitle(title);
        if(msg != null) {
            View view = LayoutInflater.from(context).inflate( R.layout.msg_textview, null );
            TextView textView=view.findViewById(R.id.textViewMsg);
            textView.setText(msg);
            builder.setView(view);
        }
        if(positiveButton == null) positiveButton = "Done";
        builder.setPositiveButton(positiveButton,positiveOnClickListener);
        if(negativeButton != null) builder.setNegativeButton(negativeButton,negativeOnClickListener);
        AlertDialog dialog = builder.create();
        // ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();

    }
}
