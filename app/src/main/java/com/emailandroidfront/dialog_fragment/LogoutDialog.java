package com.emailandroidfront.dialog_fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.emailandroidfront.LoginActivity;
import com.emailandroidfront.ProfileActivity;
import com.emailandroidfront.R;

public class LogoutDialog extends AppCompatDialogFragment {
    ProfileActivity profileActivity ;
//    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Do you want Logout?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
        return builder.create();
    }

    //za prikaz nekog teksta iz dialoga u aktivnost al sad nam ovo ne treba
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            listener = (ExampleDialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() +
//                    "must implement ExampleDialogListener");
//        }
//    }
//    public interface ExampleDialogListener {
//        void applyTexts(String username, String password);
//    }

}
