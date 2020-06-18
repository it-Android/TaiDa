package com.arcsoft.arcfacedemo.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class Dialog {
    private static AlertDialog dialog;
    private static DialogObject dialogObject;
    private static List<DialogObject> list=new ArrayList<>(  );
    public static List<DialogObject> dialog(Context context, int viewid){
        AlertDialog.Builder builder=new AlertDialog.Builder( context );
        dialog=builder.create();
        dialog.show();
        dialog.show();dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 然后弹出输入法

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // 接着添加view
        View dialogView= LayoutInflater.from( context ).inflate( viewid,null );
        dialog.setContentView( dialogView );
        dialogObject=new DialogObject();
        dialogObject.setDiagView( dialogView );
        dialogObject.setDialog( dialog );
        list.add( dialogObject );
        return list;
    }
}
