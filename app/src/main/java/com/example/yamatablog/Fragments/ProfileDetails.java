package com.example.yamatablog.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yamatablog.Models.UserDetails;
import com.example.yamatablog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileDetails#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileDetails extends Fragment {

    EditText profileName, profileMail;
    AutoCompleteTextView profileDepartment;
    ImageView profileImage;
    Button profileButton;
    ProgressBar profileBar;
    Uri pickedImageUri;
    String profileImageOld, profileImageNew;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(mAuth.getCurrentUser().getUid());

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileDetails() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileDetails newInstance(String param1, String param2) {
        ProfileDetails fragment = new ProfileDetails();
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
        setHasOptionsMenu(true);
    }

    @Override
    public void setHasOptionsMenu(boolean hasMenu) {
        super.setHasOptionsMenu(hasMenu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);
        profileName = view.findViewById(R.id.profile_name);
        profileMail = view.findViewById(R.id.profile_mail);
        profileDepartment = view.findViewById(R.id.profile_department);
        profileImage = view.findViewById(R.id.profile_image);
        profileButton = view.findViewById(R.id.profile_button);
        profileBar = view.findViewById(R.id.profile_progress);

        profileBar.setVisibility(View.INVISIBLE);

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileName.setText(snapshot.child("userName").getValue(String.class));
                profileMail.setText(snapshot.child("userMail").getValue(String.class));
                profileDepartment.setText(snapshot.child("userDepartment").getValue(String.class));

                if (ProfileDetails.this.getActivity() != null) {
                    Glide.with(getActivity()).load(snapshot.child("userPhoto").getValue(String.class)).into(profileImage);
                    setDepartment();
                }

                profileImageOld = snapshot.child("userPhoto").getValue(String.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });
        profileDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileDepartment.showDropDown();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        return view;
    }

    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    pickedImageUri = data.getData();
                    profileImage.setImageURI(pickedImageUri);
                }
            }
        });
    private void openGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryLauncher.launch(galleryIntent);
    }


    private void updateUserInfo(){
        if(TextUtils.isEmpty(profileName.getText().toString()) || TextUtils.isEmpty(profileMail.getText().toString()) || TextUtils.isEmpty(profileDepartment.getText().toString())){
            Toast.makeText(getActivity(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
        }
        else {
            profileButton.setVisibility(View.INVISIBLE);
            profileBar.setVisibility(View.VISIBLE);
            if (pickedImageUri == null) {
                initiateUserUpdate(profileName.getText().toString(), profileMail.getText().toString(), profileDepartment.getText().toString(), profileImageOld);
            }
            else{
                StorageReference profilePicturePath = FirebaseStorage.getInstance().getReference().child("ProfilePhotos").child(mAuth.getCurrentUser().getUid()+"_profilePicture");
                profilePicturePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        profilePicturePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profileImageNew = uri.toString();
                                initiateUserUpdate(profileName.getText().toString(), profileMail.getText().toString(), profileDepartment.getText().toString(), profileImageNew);
                            }
                        });
                    }
                });
            }
        }
    }
    private void initiateUserUpdate(String updatedName, String updatedMail, String updatedDepartment, String updatedPhoto){
        UserDetails updatedUser = new UserDetails(mAuth.getCurrentUser().getUid(), updatedName, updatedMail, updatedDepartment, updatedPhoto, updatedName.toLowerCase(), "Çevrimdışı");

        mAuth.getCurrentUser().updateEmail(updatedMail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    profileRef.setValue(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() && ProfileDetails.this.getActivity() != null)
                                Toast.makeText(getActivity(), "Profiliniz Başarıyla Güncellendi", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(), "Hata: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                profileButton.setVisibility(View.VISIBLE);
                profileBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private static final String[] department = new String[]{"Bilgi Teknolojileri", "Lojistik", "Şantiye Şefi", "İnsan Kaynakları", "Teknik Ofis"};


    private void setDepartment(){
        ArrayAdapter<String>adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, department);
        profileDepartment.setAdapter(adapter);
    }
}