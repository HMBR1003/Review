package com.example.qwexo.reviewinsert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by qwexo on 2017-08-23.
 */

public class ReviewManagerListAdapter extends BaseAdapter {
    Context context;
    StorageReference storageReference;
    DatabaseReference fireDB;

    ReviewSave reviewSave;
    double scoreSum = 0;
    String score;

    private ArrayList<ReviewManagerListItem> list;

    public ReviewManagerListAdapter(ArrayList<ReviewManagerListItem> itemlist) {
        if (itemlist == null) {
            list = new ArrayList<>();
        } else {
            list = itemlist;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        final ReviewManagerView view = new ReviewManagerView(context);

        final ReviewManagerListItem item = list.get(position);

        storageReference = FirebaseStorage.getInstance().getReference().child("market").child("review").child(item.getReviewID() + ".jpg");
//        storageReference.getDownloadUrl()
//                .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//
//                        Glide
//                                .with(context)
//                                .load(uri)
//                                .thumbnail(Glide.with(context).load(R.drawable.loading))
//                                .override(300, 300)
//                                .signature(new StringSignature(String.valueOf(item.getTime())))
//                                .into(view.imageView);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        view.imageView.setVisibility(View.GONE);
//                    }
//                });
        if (item.getImageBool().equals("true")) {
            view.imageView.setVisibility(View.VISIBLE);
            try {
                Glide
                        .with(context)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .thumbnail(Glide.with(context).load(R.drawable.loading))
                        .override(300, 300)
                        .signature(new StringSignature(item.getTime()))
                        .into(view.imageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            view.imageView.setVisibility(View.GONE);
        }
        String date = new SimpleDateFormat("yyyy년MM월dd일 HH:mm").format(new Date(Long.parseLong(item.getTime())));

        view.marketNameText.setText(item.getMarketName());
        view.timeText.setText(date);
//        if (item.getBody().length() > 16) {     //글자수가 많으면 잘라서 표시
//            String temp = item.getBody().substring(0, 16) + "...";
//            view.bodyText.setText(temp);
//        } else {
        view.bodyText.setText(item.getBody());
//        }

        switch (Integer.parseInt(item.getScore())) {     //평점 표시
            case 5:
                view.scoreImage5.setImageDrawable(view.getResources().getDrawable(R.drawable.fillstar));
            case 4:
                view.scoreImage4.setImageDrawable(view.getResources().getDrawable(R.drawable.fillstar));
            case 3:
                view.scoreImage3.setImageDrawable(view.getResources().getDrawable(R.drawable.fillstar));
            case 2:
                view.scoreImage2.setImageDrawable(view.getResources().getDrawable(R.drawable.fillstar));
            case 1:
                view.scoreImage1.setImageDrawable(view.getResources().getDrawable(R.drawable.fillstar));
                break;
        }

        view.updateButton.setTag(position);
        view.updateButton.setOnClickListener(new View.OnClickListener() {       //수정버튼 리스너
            @Override
            public void onClick(View v) {
                String tag = String.valueOf(v.getTag());
                ReviewManagerListItem item = (ReviewManagerListItem) getItem(Integer.parseInt(tag));

                Intent intent = new Intent(context, ReviewInsertActivity.class);
                intent.putExtra("marketName", item.getMarketName());
                intent.putExtra("body", item.getBody());
                intent.putExtra("reviewID", item.getReviewID());
                intent.putExtra("marketID", item.getMarketID());
                intent.putExtra("score", item.getScore());
                intent.putExtra("imageBool", item.getImageBool());
                context.startActivity(intent);
            }
        });
        view.deleteButton.setTag(position);
        view.deleteButton.setOnClickListener(new View.OnClickListener() {   //삭제버튼 리스너
            @Override
            public void onClick(View v) {
                final String tag = String.valueOf(v.getTag());
                Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("리뷰를 삭제하시겠습니까?")
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fireDB = FirebaseDatabase.getInstance().getReference().child("review").child(item.getReviewID());
                                fireDB.removeValue();
                                storageReference = FirebaseStorage.getInstance().getReference().child("market").child("review").child(item.getReviewID() + ".jpg");
                                storageReference.delete();
                                list.remove(Integer.parseInt(tag));
                                FirebaseDatabase.getInstance().getReference().child("review").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int i = 0;
                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            reviewSave = data.getValue(ReviewSave.class);
                                            if (reviewSave.marketID.equals(item.getMarketID())) {
                                                scoreSum += Integer.parseInt(reviewSave.score);
                                                Log.d("marketID", String.valueOf(item.getMarketID()));
                                                Log.d("scoreSum", String.valueOf(scoreSum));
                                                i++;
                                            }
                                        }
                                        scoreSum /= i;
                                        score = String.valueOf(scoreSum).substring(0, 3);
                                        scoreSum = 0;
                                        FirebaseDatabase.getInstance().getReference().child("market").child(item.getMarketID()).child("score").setValue(score);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }).show();
            }
        });

        return view;
    }

    public void clear() {
        list.clear();
    }

    public void addItem(String marketName, String time, String body, String reviewID, String marketID, String score, String imageBool) {
        ReviewManagerListItem item = new ReviewManagerListItem();

        item.setMarketName(marketName);
        item.setTime(time);
        item.setBody(body);
        item.setReviewID(reviewID);
        item.setMarketID(marketID);
        item.setScore(score);
        item.setImageBool(imageBool);

        list.add(item);
    }
}
