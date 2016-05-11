package com.rambo.marketposter.activity.Dialog;

import android.app.Dialog;
import android.content.Context;

public class BaseDialog extends Dialog {

    private Context context;

    public BaseDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public BaseDialog(Context context, int style) {
        super(context, style);
        this.context = context;
    }

}
