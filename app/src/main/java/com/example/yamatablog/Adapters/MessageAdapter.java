package com.example.yamatablog.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Models.Message;
import com.example.yamatablog.Models.UserDetails;
import com.example.yamatablog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference uRef;
    String SenderID, ReceiverID;


    private Context mContext;
    private List<Message> mData;

    public MessageAdapter(Context mContext, List<Message> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_chat_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        holder.messageContent.setText(mData.get(position).getMessageContent());
        holder.messageTime.setText(ServerValueToTime((long) mData.get(position).getMessageTime()));
        SenderID = mData.get(position).getSenderID();
        ReceiverID = mData.get(position).getReceiverID();

        uRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(SenderID);
        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails user = snapshot.getValue(UserDetails.class);
                Glide.with(mContext).load(user.getUserPhoto()).into(holder.messageImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (position == mData.size()-1){
            if (mData.get(position).isMessageStatus()){
                holder.messageStatus.setText("Görüldü");
            } else {
                holder.messageStatus.setText("Gönderildi");
            }
        } else{
            holder.messageStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageContent, messageTime;
        ImageView messageImage;
        TextView messageStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageContent = itemView.findViewById(R.id.chat_text);
            messageTime = itemView.findViewById(R.id.chat_time);
            messageImage = itemView.findViewById(R.id.chat_image);
            messageStatus = itemView.findViewById(R.id.chat_seen_status);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getSenderID().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    private String ServerValueToTime(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("HH:mm",calendar).toString();
        return date;
    }
}
