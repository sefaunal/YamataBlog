package com.example.yamatablog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.yamatablog.Adapters.MessageAdapter;
import com.example.yamatablog.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private static final String ServerKey = "key=AAAA6CH9u3Y:APA91bEdGSVnHIpKE3A_RdP1E2GuF3U5mH1_CRah8kRCbZFZhZ9wRwYq68El1Sqax4MMzt8iu32mmdWKHxS32Bex0pK-5QteZ_rTM66USWppOj7KJQF1F4_SOhaCXIZL_Rr0wCVuSpqr";
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("Messages");
    DatabaseReference uRef;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    EditText messageText;
    ImageButton messageSendButton;
    String ReceiverID, ReceiverName, ReceiverImage, SenderID, UserStatus;

    MessageAdapter messageAdapter;
    List<Message> mList;
    RecyclerView messageRV;

    ImageView ReceiverUpperImage;
    TextView ReceiverUpperName, messageLastSeen;

    ValueEventListener seenListener;

    String ReceiverToken, SentMessage, SenderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageRV = findViewById(R.id.messageRV);
        messageRV.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRV.setLayoutManager(linearLayoutManager);

        getSupportActionBar().hide();

        messageSendButton = findViewById(R.id.message_send);
        messageText = findViewById(R.id.message_text);
        messageLastSeen = findViewById(R.id.show_user_rate_status);


        ReceiverUpperName = findViewById(R.id.show_user_rate_name);
        ReceiverUpperImage = findViewById(R.id.show_user_rate_image);
        ReceiverID = getIntent().getExtras().getString("MessageReceiverID");
        ReceiverName = getIntent().getExtras().getString("MessageReceiverName");
        ReceiverImage = getIntent().getExtras().getString("MessageReceiverImage");
        UserStatus = getIntent().getExtras().getString("MessageReceiverStatus");
        SenderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ReceiverToken = getIntent().getExtras().getString("MessageReceiverToken");

        messageLastSeen.setText(UserStatus);

        ReceiverUpperName.setText(ReceiverName);
        Glide.with(this).load(ReceiverImage).into(ReceiverUpperImage);

        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(messageText.getText().toString())){
                    messageText.setError("Boş mesaj gönderemezsiniz");
                    messageText.requestFocus();
                }
                else{
                    SentMessage = messageText.getText().toString();
                    SendMessage(SenderID, ReceiverID, messageText.getText().toString());
                }
            }
        });

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    Message message = snap.getValue(Message.class);
                    if ((message.getReceiverID().equals(ReceiverID) && message.getSenderID().equals(SenderID))
                            || (message.getReceiverID().equals(SenderID) && message.getSenderID().equals(ReceiverID))){
                        mList.add(message);

                    }
                }
                messageAdapter = new MessageAdapter(getApplicationContext(), mList);
                messageRV.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SeenMessage(ReceiverID);

        uRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(SenderID);

        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SenderName = snapshot.child("userName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SeenMessage(String userID){
        seenListener = mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    Message message = snap.getValue(Message.class);
                    if (message.getReceiverID().equals(mUser.getUid()) && message.getSenderID().equals(userID)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("messageStatus", true);
                        snap.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendMessage(String SenderID, String ReceiverID, String MessageText){
        Message newMessage = new Message(SenderID, ReceiverID, MessageText, false);
        mReference.push().setValue(newMessage);
        messageText.setText("");
        sendNotification();


        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(SenderID)
                .child(ReceiverID);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(ReceiverID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatRefForReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(ReceiverID)
                .child(SenderID);

        chatRefForReceiver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRefForReceiver.child("id").setValue(SenderID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        mReference.removeEventListener(seenListener);
    }

    private void sendNotification(){
        JSONObject to = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("title", SenderName);
            data.put("body", SentMessage);

            to.put("to", ReceiverToken);
            to.put("notification", data);
            
            initiateNotification(to);
        }catch (JSONException e){
            //
        }
    }

    private void initiateNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", to, response -> {

        },error->{

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("Authorization", ServerKey);
                map.put("Content-Type", "application/json");
                return map;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

}