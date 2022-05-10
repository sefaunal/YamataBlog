package com.example.yamatablog.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yamatablog.Adapters.RatingAdapter;
import com.example.yamatablog.Models.UserRate;
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
 * Use the {@link RatingCurrentUser#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingCurrentUser extends Fragment {
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    RecyclerView RatingsCurrentRV;
    List<UserRate>RateList;
    RatingAdapter rateAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RatingCurrentUser() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RatingCurrentUser.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingCurrentUser newInstance(String param1, String param2) {
        RatingCurrentUser fragment = new RatingCurrentUser();
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
        View fragmentView =  inflater.inflate(R.layout.fragment_rating_current_user, container, false);
        RatingsCurrentRV = fragmentView.findViewById(R.id.RV_ratings_current_user);
        RatingsCurrentRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        RatingsCurrentRV.setHasFixedSize(true);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference RateRef = FirebaseDatabase.getInstance().getReference("UserRatings").child(mUser.getUid());

        RateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RateList = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    UserRate userRate = snap.getValue(UserRate.class);
                    RateList.add(userRate);
                }
                rateAdapter = new RatingAdapter(getActivity(), RateList);
                RatingsCurrentRV.setAdapter(rateAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}