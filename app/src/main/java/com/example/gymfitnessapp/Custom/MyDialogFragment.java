package com.example.gymfitnessapp.Custom;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    private String title;
    private String message;
    private OnDialogButtonClickListener buttonClickListener;

    public interface OnDialogButtonClickListener {
        void onPositiveButtonClick();
        void onNegativeButtonClick();
    }

    public static MyDialogFragment newInstance(String title, String message) {
        MyDialogFragment fragment = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDialogButtonClickListener(OnDialogButtonClickListener listener) {
        this.buttonClickListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            title = args.getString(ARG_TITLE);
            message = args.getString(ARG_MESSAGE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (buttonClickListener != null) {
                            buttonClickListener.onPositiveButtonClick();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (buttonClickListener != null) {
                            buttonClickListener.onNegativeButtonClick();
                        }
                    }
                });
        return builder.create();
    }
}

