package com.arcsoft.arcfacedemo.Dialog;

import android.app.AlertDialog;
import android.view.View;

public class DialogObject {
    private AlertDialog dialog;
    private View diagView;

    public AlertDialog getDialog() {
        return dialog;
    }

    public void setDialog(AlertDialog dialog) {
        this.dialog = dialog;
    }

    public View getDiagView() {
        return diagView;
    }

    public void setDiagView(View diagView) {
        this.diagView = diagView;
    }
}
