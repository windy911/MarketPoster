package com.rambo.marketposter.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.rambo.marketposter.R;

/**
 * Created by windy on 16/3/25.
 */
public class DialogUtils {
    private Context mContext;

    public DialogUtils(Context context) {
        this.mContext = context;
    }

    public static Dialog createDialog(Context context, String title, String content, String cancel, String confirm, final OnItemSelected onItemSelected) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.normal_dialog);
        Button btnCancel = (Button) window.findViewById(R.id.btnCancle);
        Button btnConfirm = (Button) window.findViewById(R.id.btnConfirm);
        TextView tvTitle = (TextView) window.findViewById(R.id.tvDialogTitle);
        TextView tvContent = (TextView) window.findViewById(R.id.tvDialogContent);
        btnCancel.setText(cancel);
        btnConfirm.setText(confirm);
        tvTitle.setText(title);
        tvContent.setText(content);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelected.ItemSelected(OnItemSelected.RESULT_CANCEL);
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelected.ItemSelected(OnItemSelected.RESULT_CONFIRM);
                dialog.dismiss();
            }
        });
        return dialog;
    }


    public interface OnItemSelected {
        int RESULT_CANCEL = 0;
        int RESULT_CONFIRM = 1;

        int ItemSelected(int seleced);
    }
}
