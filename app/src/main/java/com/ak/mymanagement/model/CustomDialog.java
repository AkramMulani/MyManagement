package com.ak.mymanagement.model;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ak.mymanagement.R;

public class CustomDialog {

    public interface DialogListener {
        void onDialogPositiveClick(String name, String model, String quantity, String quality, String price, String category);
    }

    @SuppressLint("MissingInflatedId")
    public static void showDialog(Context context, final DialogListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        final EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        final EditText qualityEditText = dialogView.findViewById(R.id.qualityEditText);
        final EditText priceEditText = dialogView.findViewById(R.id.priceEditText);
        final EditText modelEditText = dialogView.findViewById(R.id.modelEditText);
        final EditText categoryEditText = dialogView.findViewById(R.id.categoryEditText);

        builder.setTitle("Update Component")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = nameEditText.getText().toString();
                        String quantity = quantityEditText.getText().toString();
                        String quality = qualityEditText.getText().toString();
                        String price = priceEditText.getText().toString();
                        String model = modelEditText.getText().toString();
                        String category = categoryEditText.getText().toString();
                        listener.onDialogPositiveClick(name, model, quantity, quality, price, category);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "Process cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

