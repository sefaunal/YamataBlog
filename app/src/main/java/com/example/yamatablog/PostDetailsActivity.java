package com.example.yamatablog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Adapters.CommentAdapter;
import com.example.yamatablog.Adapters.PostImagesAdapter;
import com.example.yamatablog.Models.Comment;
import com.example.yamatablog.Models.Post;
import com.example.yamatablog.Models.PostImages;
import com.example.yamatablog.Models.UserRate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {
    ImageView postUserImage, currentUserImage, commentAddButton;
    TextView postTitle, postDateName, postDescription, postAddress, postDepartment, postStatus;
    EditText commentText;
    String postID, postUserID, postCreateDate, postFixDate, postFixedBy, currentUserDepartment, postAssign, postUserImageURI, currentUserImageURI;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    Button postFixBtn, postSendMessage;
    RecyclerView RVComment;
    List<Comment>Comments;
    CommentAdapter commentAdapter;
    String rateStatus;
    Button rateButton;
    Dialog popupRate;

    //ImageView postImage

    RecyclerView postImageRV;
    List<PostImages> postImagesList;
    PostImagesAdapter postImagesAdapter;
    List<String>stringImageList;

    EditText popupEdittext;
    RatingBar popupRatingBar;
    Button popupRatingButton;
    int RatingValue = 0;
    String RatingComment = "null";
    DatabaseReference RateRef;

    @Override
    protected void onPause() {
        super.onPause();
        //this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        getSupportActionBar().hide();

        postUserID = getIntent().getExtras().getString("userID");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserDetails");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postDateName.setText(snapshot.child(postUserID).child("userName").getValue(String.class) + " | " + postCreateDate + " tarihinde oluşturuldu");
                currentUserDepartment = snapshot.child(mUser.getUid()).child("userDepartment").getValue(String.class);
                postUserImageURI = snapshot.child(postUserID).child("userPhoto").getValue(String.class);
                currentUserImageURI = snapshot.child(mUser.getUid()).child("userPhoto").getValue(String.class);

                if (!PostDetailsActivity.this.isDestroyed()){
                    Glide.with(PostDetailsActivity.this).load(postUserImageURI).into(postUserImage);
                    Glide.with(PostDetailsActivity.this).load(currentUserImageURI).into(currentUserImage);
                }

                if (getIntent().getExtras().getString("postStatus").equals("Fixed")){
                    postStatus.setText("Durum: " + snapshot.child(postFixedBy).child("userName").getValue(String.class) + " tarafından " + postFixDate + " tarihinde çözüldü olarak işaretlendi");
                }

                if(getIntent().getExtras().getString("postStatus").equals("notFixed") && postAssign.equals(currentUserDepartment)){
                    postFixBtn.setVisibility(View.VISIBLE);
                }

                String postFixerName = snapshot.child(postFixedBy).child("userName").getValue(String.class);
                rateButton.setText(postFixerName + " kişisini değerlendir");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        RVComment = findViewById(R.id.post_detail_rv_comment);

        rateButton = findViewById(R.id.post_detail_rate_button);

        //postImage = findViewById(R.id.post_detail_img);
        postUserImage = findViewById(R.id.post_detail_user_img);
        currentUserImage = findViewById(R.id.post_detail_currentuser_img);

        postTitle = findViewById(R.id.post_detail_title);
        postDateName = findViewById(R.id.post_detail_date_name);
        postDescription = findViewById(R.id.post_detail_desc);
        postAddress = findViewById(R.id.post_detail_address);
        postDepartment = findViewById(R.id.post_detail_department);
        postStatus = findViewById(R.id.post_detail_status);

        commentText = findViewById(R.id.post_detail_comment);
        commentAddButton = findViewById(R.id.post_detail_comment_add);

        postFixBtn = findViewById(R.id.post_detail_fix_button);
        postFixBtn.setVisibility(View.INVISIBLE);

        //Glide.with(this).load(getIntent().getExtras().getString("postImage")).into(postImage);
        postTitle.setText(getIntent().getExtras().getString("postTitle"));
        postDescription.setText("Açıklama: " + getIntent().getExtras().getString("postDescription"));
        postAddress.setText("Adres: " + getIntent().getExtras().getString("postAddress"));

        postAssign = getIntent().getExtras().getString("postDepartment");
        postDepartment.setText("İlgilenecek Bölüm: " + postAssign);

        postFixedBy = getIntent().getExtras().getString("postFixedBy");
        if (getIntent().getExtras().getString("postStatus").equals("notFixed")) {
            postStatus.setText("Durum: Çözülmedi");

        }

        rateStatus = getIntent().getExtras().getString("userRated");

        postFixBtn.setOnClickListener(view -> initiatePostFix());
        postID = getIntent().getExtras().getString("postID");

        postCreateDate = ServerValueToStringDate(getIntent().getExtras().getLong("postCreateDate"));
        postFixDate = ServerValueToStringDate(getIntent().getExtras().getLong("postFixDate"));

        commentAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(commentText.getText().toString())){
                    Toast.makeText(PostDetailsActivity.this, "Lütfen yorumunuzu girin", Toast.LENGTH_SHORT).show();
                }
                else{
                    DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(postID).push();
                    String cContext = commentText.getText().toString();
                    Comment comment = new Comment(cContext, mUser.getUid());
                    commentRef.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PostDetailsActivity.this, "Yorumunuz paylaşıldı", Toast.LENGTH_SHORT).show();
                            commentText.setText("");
                        }
                    });
                }
            }
        });


        if (getIntent().getExtras().getString("postStatus").equals("Fixed") && rateStatus.equals("false") && postUserID.equals(mUser.getUid())){
            rateButton.setVisibility(View.VISIBLE);
        }
        else{
            rateButton.setVisibility(View.GONE);
        }

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupRate.show();

            }
        });

        initiateRVComment();
        initiatePopupRate();

        postImageRV = findViewById(R.id.post_detail_img_rv);
        postImageRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        postImageRV.setHasFixedSize(true);
        DatabaseReference photoRef = FirebaseDatabase.getInstance().getReference("PostPhotos").child(postID);
        photoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postImagesList = new ArrayList<>();
                stringImageList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()){
                    String newPhoto = snap.child("imageLink").getValue(String.class);
                    stringImageList.add(newPhoto);
                }

                postImagesAdapter = new PostImagesAdapter(PostDetailsActivity.this, stringImageList);
                postImageRV.setAdapter(postImagesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initiatePopupRate() {
        popupRate = new Dialog(PostDetailsActivity.this);
        popupRate.setContentView(R.layout.popup_rate_user);
        popupEdittext = popupRate.findViewById(R.id.popup_rate_user_comment);
        popupRatingBar = popupRate.findViewById(R.id.popup_rate_user_rate_bar);
        popupRatingButton = popupRate.findViewById(R.id.popup_rate_user_button);

        popupRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                RatingValue = (int) v;

            }
        });
        popupRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(popupEdittext.getText().toString())){
                    RatingComment = popupEdittext.getText().toString();
                }
                if (RatingValue == 0){
                    Toast.makeText(PostDetailsActivity.this, "Lütfen 1-5 arası bir puan girin", Toast.LENGTH_SHORT).show();
                } else{
                    initiateUserRate();
                }
            }
        });

    }

    private void initiateUserRate() {
        UserRate userRate = new UserRate(mUser.getUid(), RatingComment, RatingValue);
        RateRef = FirebaseDatabase.getInstance().getReference("UserRatings").child(postFixedBy).child(postID);
        RateRef.setValue(userRate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                DatabaseReference postUpdateRef = FirebaseDatabase.getInstance().getReference("Posts").child(postID);
                postUpdateRef.child("userRated").setValue("true");
                Toast.makeText(PostDetailsActivity.this, "Değerlendirmeniz başarıyla paylaşıldı", Toast.LENGTH_SHORT).show();
                popupRate.dismiss();
                finish();
            }
        });
    }

    private String ServerValueToStringDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }

    private void initiatePostFix(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(PostDetailsActivity.this);
        dialog.setMessage("İşlemi onaylıyor musunuz?");
        dialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Object postFixDate = ServerValue.TIMESTAMP;
                DatabaseReference postUpdateRef = FirebaseDatabase.getInstance().getReference("Posts").child(postID);
                postUpdateRef.child("postFixDate").setValue(postFixDate);
                postUpdateRef.child("postFixedBy").setValue(mUser.getUid());
                postUpdateRef.child("postStatus").setValue("Fixed");
                Toast.makeText(PostDetailsActivity.this, "İşleminiz başarıyla tamamlandı", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PostDetailsActivity.this, HomeActivity.class));
                finish();
            }
        });
        dialog.setNegativeButton("Hayır", (dialogInterface, i) -> { });
        dialog.show();
    }

    private void initiateRVComment() {
        RVComment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(postID);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Comments = new ArrayList<>();
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    Comments.add(comment);
                }
                commentAdapter = new CommentAdapter(getApplicationContext(), Comments);
                RVComment.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}