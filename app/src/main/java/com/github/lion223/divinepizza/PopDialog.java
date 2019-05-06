package com.github.lion223.divinepizza;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PopDialog extends AppCompatDialogFragment {

    private EditText editTextName;
    private PopDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);
        builder.setView(view)
                .setCancelable(false)
                .setTitle("Введіть ваше ім'я")
                .setPositiveButton("Продовжити", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editTextName.getText().toString();
                        listener.applyName(name);
                        listener.addToDb();
                    }
                });

        editTextName = view.findViewById(R.id.dialog_name);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (PopDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
            + "must implement PopDialogListener");
        }
    }

    public interface PopDialogListener{
        void applyName(String name);
        void addToDb();
    }
}

