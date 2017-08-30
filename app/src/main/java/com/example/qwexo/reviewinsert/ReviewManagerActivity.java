package com.example.qwexo.reviewinsert;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.qwexo.reviewinsert.databinding.ActivityReviewManagerBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dmax.dialog.SpotsDialog;


public class ReviewManagerActivity extends AppCompatActivity {
    ActivityReviewManagerBinding managerBinding;
    DatabaseReference fireDB;
    ReviewSave reviewSave;
    String userID,reviewID;
    ReviewManagerListAdapter adapter;
    ArrayList<ReviewManagerListItem> itemList;
    SpotsDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        managerBinding = DataBindingUtil.setContentView(this, R.layout.activity_review_manager);

        managerBinding.toolbar.setTitle("리뷰 관리 목록");
        managerBinding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(managerBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        itemList = new ArrayList<>();
        adapter = new ReviewManagerListAdapter(itemList);
        managerBinding.listView.setAdapter(adapter);

        //유저 정보 가져오기
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        for(UserInfo profile : user.getProviderData()){
//            userID = profile.getUid();
//        }
        userID = "dpqpqpqpqp"; //임시 유저아이디

        dialog = new SpotsDialog(ReviewManagerActivity.this, "데이터를 불러오는 중입니다...", R.style.ProgressBar);
        dialog.setCancelable(false);
        dialog.show();
    }
    private Comparator<ReviewManagerListItem> comparator = new Comparator<ReviewManagerListItem>() {
        @Override
        public int compare(ReviewManagerListItem o1, ReviewManagerListItem o2) {
            int sor;
            if(Long.parseLong(o1.getTime()) < Long.parseLong(o2.getTime())){
                sor = 1;
            }else if(Long.parseLong(o1.getTime()) == Long.parseLong(o2.getTime())){
                sor = 0;
            }else{
                sor = -1;
            }
            return sor;
        }
    };
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            adapter.clear();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                reviewSave = data.getValue(ReviewSave.class);
                if(reviewSave.userID.equals(userID)){
                    reviewID = data.getKey();
                    adapter.addItem(reviewSave.marketName,reviewSave.time,reviewSave.body,reviewID,
                            reviewSave.marketID,reviewSave.score,reviewSave.imageBool);
                    adapter.notifyDataSetChanged();
                }
            }
            Collections.sort(itemList,comparator);
            dialog.dismiss();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fireDB = FirebaseDatabase.getInstance().getReference().child("review");
        fireDB.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fireDB.removeEventListener(valueEventListener);
    }
    private class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ReviewManagerListItem item = (ReviewManagerListItem)adapter.getItem(position);

            Intent intent = new Intent(getApplicationContext(),ReviewInsertActivity.class);
            intent.putExtra("marketName",item.getMarketName());
            intent.putExtra("body",item.getBody());
            intent.putExtra("reviewID",item.getReviewID());
            intent.putExtra("marketID",item.getMarketID());
            intent.putExtra("score",item.getScore());
            intent.putExtra("imageBool",item.getImageBool());
            startActivity(intent);
        }
    }
}
