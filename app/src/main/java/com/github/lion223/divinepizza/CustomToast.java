package com.github.lion223.divinepizza;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    private Toast toast;
    private int bgColor;
    private int textColor;
    private Activity activity;

    public CustomToast(int bgColor, int textColor, Activity activity, Toast toast) {
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.activity = activity;
        this.toast = toast;
    }

    public void show(String message) {
        toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        View view = toast.getView();
        view.getBackground().setColorFilter(bgColor,PorterDuff.Mode.SRC_IN);
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(textColor);
        toast.show();
    }
}
