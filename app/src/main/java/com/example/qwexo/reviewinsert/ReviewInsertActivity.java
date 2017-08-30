package com.example.qwexo.reviewinsert;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.qwexo.reviewinsert.databinding.ActivityReviewInsertBinding;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import dmax.dialog.SpotsDialog;

import static android.view.KeyCharacterMap.load;

public class ReviewInsertActivity extends AppCompatActivity {

    private static final int GET_MARKET_IMAGE = 100;
    private static final int REQUEST_CROP = 200;
    ActivityReviewInsertBinding insertBinding;

    DatabaseReference fireDB;
    StorageReference mStorageRef;
    FirebaseUser user;
    private String marketID, marketName;
    ReviewSave reviewSave;
    String userID;
    String userName;
    String time;
    String reviewID;
    String body;
    String score;
    String imageBool;
    double scoreSum = 0;
    boolean updateBool = false;

    Uri tempImageUri, imageCropUri;
    File tempFile;
    Bitmap bitmap;
    SpotsDialog uploadDialog;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insertBinding = DataBindingUtil.setContentView(this, R.layout.activity_review_insert);

        insertBinding.toolbar.setTitle("리뷰 작성");
        insertBinding.toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(insertBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        insertBinding.lengthText.setText("글자 수 : 0 / 80자");
        insertBinding.lengthText.setTextColor(Color.rgb(50,50,50));
        insertBinding.reviewText.addTextChangedListener(watcher);

        insertBinding.okButton.setButtonColor(getResources().getColor(R.color.buttonColor));
        insertBinding.okButton.setCornerRadius(15);

        builder = new AlertDialog.Builder(this);

        tempFile = getTempFile();

        Intent intent = getIntent();
//        marketID = intent.getStringExtra("marketID"); //마켓아이디
//        marketName = intent.getStringExtra("marketName"); //마켓네임
        score = intent.getStringExtra("score"); //평점
        reviewID = intent.getStringExtra("reviewID");   //기존에 저장된 리뷰 키값(수정 시 사용)
        body = intent.getStringExtra("body");      //리뷰 본문
        imageBool = intent.getStringExtra("imageBool");

        marketID = "slwVsecqtTO3RDjzPxBWrFekbEd2";  //임시 마켓아이디
        marketName = "네네치킨"; //임시 마켓네임
        insertBinding.marketNameText.setText(marketName);

        if (body != null) {      //본문이 있을 시 수정목록으로 표시
            insertBinding.toolbar.setTitle("리뷰 수정");

            updateBool = true;
            insertBinding.reviewText.setText(body);
            insertBinding.okButton.setText("수정완료");
            switch (Integer.parseInt(score)) {
                case 5:
                    insertBinding.scoreImage5.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
                case 4:
                    insertBinding.scoreImage4.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
                case 3:
                    insertBinding.scoreImage3.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
                case 2:
                    insertBinding.scoreImage2.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
                case 1:
                    insertBinding.scoreImage1.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
                    break;
            }
            if(imageBool != null) {
                if(imageBool.equals("true")) {
                    mStorageRef = FirebaseStorage.getInstance().getReference().child("market").child("review").child(reviewID + ".jpg");
                    mStorageRef.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Glide
                                            .with(getApplicationContext())
                                            .load(uri)
                                            .thumbnail(Glide.with(getApplicationContext()).load(R.drawable.loading))
                                            .override(300, 300)
                                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                            .into(insertBinding.imageView);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        }

        //유저 정보 가져오기
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        for(UserInfo profile : user.getProviderData()){
//            userID = profile.getUid();
//            userName = profile.getDisplayName();
//        }

        //임시 아이디,이름
        userID = "dpqpqpqpqp";
        userName = "김모시기";

        //유저네임 뒷부분 가려주기
        if (userName.length() > 4) {
            String name = userName.substring(0, 3);
            for (int i = 3; i < userName.length(); i++) {
                name += "*";
            }
            userName = name;
        } else if (userName.length() >= 3) {
            userName = userName.substring(0, 2) + "**";
        } else if (userName.length() == 2) {
            userName = userName.substring(0, 1) + "*";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void OnClicked(View v) {
        switch (v.getId()) {
            case R.id.imageView:
                builder.setTitle("업로드 할 이미지 선택")
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("사진선택", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doTakeAlbumAction();
                            }
                        })
                        .show();
                break;
            case R.id.okButton: //작성완료(수정완료) 버튼
                final String reviewText = insertBinding.reviewText.getText().toString();
                time = String.valueOf(System.currentTimeMillis());
                if (reviewText.length() >= 10) {
                    if (score != null) {
                        builder.setTitle("리뷰 등록")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(bitmap != null)
                                            imageBool = "true";
                                        else
                                            imageBool = "false";
                                        reviewSave = new ReviewSave(userID, userName, reviewText, time, marketID, marketName, score, imageBool);
                                        if (updateBool) {
                                            fireDB = FirebaseDatabase.getInstance().getReference().child("review").child(reviewID);
                                            fireDB.setValue(reviewSave);
                                        } else {
                                            fireDB = FirebaseDatabase.getInstance().getReference().child("review").push();
                                            fireDB.setValue(reviewSave);
                                        }
                                        FirebaseDatabase.getInstance().getReference().child("review").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int i = 0;
                                                for(DataSnapshot data : dataSnapshot.getChildren()){
                                                    reviewSave = data.getValue(ReviewSave.class);
                                                    if(reviewSave.marketID.equals(marketID)){
                                                        scoreSum += Integer.parseInt(reviewSave.score);
                                                        Log.d("marketID",String.valueOf(marketID));
                                                        Log.d("scoreSum",String.valueOf(scoreSum));
                                                        i++;
                                                    }
                                                }
                                                scoreSum /= i;
                                                score = String.valueOf(scoreSum).substring(0,3);
                                                FirebaseDatabase.getInstance().getReference().child("market").child(marketID).child("score").setValue(score);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        uploadImage();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(this, "평점을 등록하여 주십시오.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "10자 이상 작성하여 주십시오.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.scoreImage5:      //평점
                insertBinding.scoreImage5.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
            case R.id.scoreImage4:
                insertBinding.scoreImage4.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
            case R.id.scoreImage3:
                insertBinding.scoreImage3.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
            case R.id.scoreImage2:
                insertBinding.scoreImage2.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
            case R.id.scoreImage1:
                if (v.getId() == R.id.scoreImage5) {
                    score = "5";
                } else if (v.getId() == R.id.scoreImage4) {
                    insertBinding.scoreImage5.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    score = "4";
                } else if (v.getId() == R.id.scoreImage3) {
                    insertBinding.scoreImage4.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    insertBinding.scoreImage5.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    score = "3";
                } else if (v.getId() == R.id.scoreImage2) {
                    insertBinding.scoreImage3.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    insertBinding.scoreImage4.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    insertBinding.scoreImage5.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    score = "2";
                } else if (v.getId() == R.id.scoreImage1) {
                    insertBinding.scoreImage2.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    insertBinding.scoreImage3.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    insertBinding.scoreImage4.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    insertBinding.scoreImage5.setImageDrawable(getResources().getDrawable(R.drawable.nullstar));
                    score = "1";
                }
                insertBinding.scoreImage1.setImageDrawable(getResources().getDrawable(R.drawable.fillstar));
                break;
        }
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            insertBinding.lengthText.setText("글자 수 : " + s.length() + " / 80자");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 앨범에서 이미지 가져오기
     */
    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GET_MARKET_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //매장 대표사진 설정에서 결과를 받아왔을 경우
        if (requestCode == GET_MARKET_IMAGE && resultCode == RESULT_OK) {
            imageCropUri = data.getData(); //인텐트에서 이미지에 대한 데이터 추출
            cropImage();
        } else if (requestCode == REQUEST_CROP && resultCode == RESULT_OK) {
//            File tempFile = getTempFile();
            if (tempFile.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(tempFile.getPath(), options);
                int imageWidth = options.outWidth;

                if (imageWidth > 1000 && imageWidth < 2000) {
                    options.inSampleSize = 2;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(tempFile.getPath(), options);
                } else if (imageWidth >= 2000 && imageWidth < 3000) {
                    options.inSampleSize = 4;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(tempFile.getPath(), options);
                } else if (imageWidth >= 3000) {
                    options.inSampleSize = 6;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(tempFile.getPath(), options);
                } else {
                    bitmap = BitmapFactory.decodeFile(tempFile.toString());
                }
                insertBinding.imageView.setImageBitmap(bitmap);
            }
        } else if (requestCode == REQUEST_CROP && resultCode != RESULT_OK) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, GET_MARKET_IMAGE);
        }
    }

    private File getTempFile() {
        File file = new File(getExternalCacheDir(), "menuTmpImage.jpg");
        try {
            file.createNewFile();
        } catch (Exception e) {
            Log.e("파일 생성", "실패");
        }
        return file;
    }

    public void cropImage() {
        tempImageUri = Uri.fromFile(tempFile);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageCropUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); //Bitmap 형태로 받기 위해 해당 작업 진행
        startActivityForResult(intent, REQUEST_CROP);
    }

    public void uploadImage() {
        if (bitmap != null) {
            //데이터 저장하는 중이라고 알림창 띄우기
            uploadDialog = new SpotsDialog(ReviewInsertActivity.this, "데이터를 저장하는 중입니다...", R.style.ProgressBar);
            uploadDialog.setCancelable(false);
            uploadDialog.show();

            //저장소에 대한 참조 만들기
            mStorageRef = FirebaseStorage.getInstance().getReference();
            //실제로 이미지가 저장될 곳의 참조
            StorageReference mountainsRef = mStorageRef.child("market").child("review").child(fireDB.getKey() + ".jpg");

            //비트맵을 jpg로 변환시켜서 변수에 저장
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //jpg형식으로 저장된 변수를 저장소에 업로드하는 함수
            UploadTask uploadTask = mountainsRef.putBytes(data);
            //성공했을 시와 실패했을 시를 받아오는 리스너 부착
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    uploadDialog.dismiss();
                    Toast.makeText(ReviewInsertActivity.this, "제출 실패.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploadDialog.dismiss();
                    finish();
                }
            });
        } else {
            finish();
        }
    }
}
