package com.example.yamatablog.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.yamatablog.Adapters.UserAdapter;
import com.example.yamatablog.Adapters.UserAdapterForRatings;
import com.example.yamatablog.Models.UserDetails;
import com.example.yamatablog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingUsersList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingUsersList extends Fragment {
    DatabaseReference uRef = FirebaseDatabase.getInstance().getReference("UserDetails");
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView RatingUsersRV;
    UserAdapterForRatings userAdapter;
    List<UserDetails> mUsers;
    EditText UserSearchWithEdittext;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RatingUsersList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingUsersList.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingUsersList newInstance(String param1, String param2) {
        RatingUsersList fragment = new RatingUsersList();
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
        View fragmentView = inflater.inflate(R.layout.fragment_rating_users_list, container, false);
        RatingUsersRV = fragmentView.findViewById(R.id.RVRatingUserList);
        RatingUsersRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        RatingUsersRV.setHasFixedSize(true);

        UserSearchWithEdittext = fragmentView.findViewById(R.id.rating_user_list_search_edittext);

        UserSearchWithEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        String UID = mUser.getUid();

        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new ArrayList<>();

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    UserDetails user = snap.getValue(UserDetails.class);
                    if (!user.getUserID().equals(UID)) {
                        mUsers.add(user);
                    }

                    userAdapter = new UserAdapterForRatings(getActivity(), mUsers);
                    RatingUsersRV.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("UserDetails").orderByChild("userNameSearch")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()){
                    UserDetails user = snap.getValue(UserDetails.class);

                    assert user!= null;
                    assert mUser != null;
                    if (!user.getUserID().equals(mUser.getUid())){
                        mUsers.add(user);
                    }
                }
                userAdapter = new UserAdapterForRatings(getContext(), mUsers);
                RatingUsersRV.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}