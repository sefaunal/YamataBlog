package com.example.yamatablog.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yamatablog.Adapters.UserAdapter;
import com.example.yamatablog.Models.ChatList;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageList extends Fragment {
    RecyclerView MessageListRV;
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference chatRef;
    DatabaseReference uRef = FirebaseDatabase.getInstance().getReference("UserDetails");

    UserAdapter userAdapter;
    List<UserDetails> mUsers;

    List<ChatList> usersList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessageList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageList.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageList newInstance(String param1, String param2) {
        MessageList fragment = new MessageList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_message_list, container, false);
        MessageListRV = fragmentView.findViewById(R.id.RVMessageList);
        MessageListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        MessageListRV.setHasFixedSize(true);



        chatRef = FirebaseDatabase.getInstance().getReference("ChatList").child(mUser.getUid());
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList = new ArrayList<>();

                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    ChatList chatList = snap.getValue(ChatList.class);
                    usersList.add(chatList);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return fragmentView;
    }

    private void chatList() {
        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    UserDetails user = snap.getValue(UserDetails.class);
                    for (ChatList chatList : usersList){
                        if (user.getUserID().equals(chatList.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers);
                MessageListRV.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}