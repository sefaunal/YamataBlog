package com.example.yamatablog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yamatablog.MessageActivity;
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

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    
    private Context mContext;
    private List<UserDetails> mData;

    String theLastMessage;

    public UserAdapter(Context mContext, List<UserDetails> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_message_user, parent, false);
        
        return new UserViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        UserDetails user = mData.get(position);
        
        holder.userName.setText(user.getUserName());
        Glide.with(mContext).load(user.getUserPhoto()).into(holder.userImage);
        if (user.getUserStatus().equals("online")){
            holder.userOnline.setVisibility(View.VISIBLE);
            holder.userOffline.setVisibility(View.INVISIBLE);
        }
        else{
            holder.userOnline.setVisibility(View.INVISIBLE);
            holder.userOffline.setVisibility(View.VISIBLE);
        }

        lastMessage(mData.get(position).getUserID(), holder.LastMessage);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView userImage, userOnline, userOffline;
        TextView userName, LastMessage;
        
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.message_user_image);
            userName = itemView.findViewById(R.id.message_user_name);
            userOnline = itemView.findViewById(R.id.message_user_on);
            userOffline = itemView.findViewById(R.id.message_user_off);
            LastMessage = itemView.findViewById(R.id.message_last_message);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Intent messageIntent = new Intent(mContext, MessageActivity.class);
                    messageIntent.putExtra("MessageReceiverID", mData.get(position).getUserID());
                    messageIntent.putExtra("MessageReceiverName", mData.get(position).getUserName());
                    messageIntent.putExtra("MessageReceiverImage", mData.get(position).getUserPhoto());
                    messageIntent.putExtra("MessageReceiverStatus", mData.get(position).getUserLastSeen());
                    messageIntent.putExtra("MessageReceiverToken", mData.get(position).getUserToken());
                    mContext.startActivity(messageIntent);
                }
            });

        }
    }
    private void lastMessage(String UserID, TextView LastMessage){
        theLastMessage = "default";
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Messages");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if ((message.getReceiverID().equals(fUser.getUid()) && message.getSenderID().equals(UserID))
                            || (message.getSenderID().equals(fUser.getUid()) && message.getReceiverID().equals(UserID))){
                        theLastMessage = message.getMessageContent();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        LastMessage.setText("Mesaj Yok");
                        LastMessage.setVisibility(View.GONE);
                        break;

                    default:
                        LastMessage.setText(theLastMessage);
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
