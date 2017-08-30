package com.example.qwexo.reviewinsert;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    DatabaseReference fireDB;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClicked(View v) {
        switch (v.getId()) {
            case R.id.button:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setNegativeButton("리뷰작성", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ReviewInsertActivity.class);
                        startActivity(intent);
                    }
                }).setPositiveButton("리뷰목록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ReviewManagerActivity.class);
                        startActivity(intent);
                    }
                }).setNeutralButton("리뷰초기화", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fireDB = FirebaseDatabase.getInstance().getReference().child("review");
                        fireDB.removeValue();
                        storageReference = FirebaseStorage.getInstance().getReference().child("market").child("review");
                        storageReference.delete();
                    }
                })
                .show();
                break;
        }
    }
}
