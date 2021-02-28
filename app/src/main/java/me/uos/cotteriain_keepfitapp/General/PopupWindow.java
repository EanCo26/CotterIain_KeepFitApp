package me.uos.cotteriain_keepfitapp.General;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import me.uos.cotteriain_keepfitapp.R;

public class PopupWindow {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public PopupWindow(AlertDialog.Builder dialogBuilder, AlertDialog dialog) {
        this.dialogBuilder = dialogBuilder;
        this.dialog = dialog;
    }
    
    public void showWindow(){ dialog.show(); }
    public void closeWindow(){ dialog.dismiss(); }
}
