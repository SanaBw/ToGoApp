package com.example.sanida.togoapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;


public class ProfileFragment extends Fragment {

    ImageView profileImg;
    View view;
    Button signOutBtn, saveBtn, editBtn;
    EditText nameTxt, emailTxt, passwordTxt;

    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    static FirebaseAuth auth;
    DatabaseReference dbRef;
    FirebaseUser user;
    FirebaseDatabase database;
    String userPath;
    String username, email;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        userPath = user.getUid();
        dbRef = database.getReference("/users");


        profileImg = view.findViewById(R.id.profileImg);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        signOutBtn = view.findViewById(R.id.signOutBtn);
        saveBtn = view.findViewById(R.id.saveBtn);
        nameTxt = view.findViewById(R.id.nameTxt);
        emailTxt = view.findViewById(R.id.emailTxt);
        editBtn = view.findViewById(R.id.editBtn);
        passwordTxt = view.findViewById(R.id.passwordTxt);

        fetchUserData();


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nameTxt.isEnabled()) {
                    nameTxt.setEnabled(true);
                    emailTxt.setEnabled(true);
                    passwordTxt.setEnabled(true);
                } else {
                    nameTxt.setEnabled(false);
                    emailTxt.setEnabled(false);
                    passwordTxt.setEnabled(false);
                }

            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData(nameTxt.getText().toString(), emailTxt.getText().toString(), passwordTxt.getText().toString());
                fetchUserData();
                nameTxt.setEnabled(false);
                emailTxt.setEnabled(false);
                passwordTxt.setEnabled(false);
            }


        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                getActivity().onBackPressed();
                Intent i = new Intent(getContext(), LogReg.class);
                startActivity(i);
            }
        });

        return view;
    }

    private void updateUserData(String newUsername, final String newEmail, String newPw) {

        if (!newUsername.equals(username)) {
            dbRef.child(userPath).child("Name").setValue(newUsername);
        }

        if (!newEmail.equals(email)) {
            if (passwordTxt.getText().toString().equals("")){
                Toast.makeText(getContext(), "Provide a password in order to change your E-mail!", Toast.LENGTH_LONG).show();
            } else {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, passwordTxt.getText().toString()); // Current Login Credentials \\

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("SUCCESS:" ,"User re-authenticated.");

                                user.updateEmail(newEmail)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("SUCCESS", "User email address updated." + email + " " + passwordTxt.getText().toString());
                                                    Toast.makeText(getContext(), "E-mail changed! Sign in again, please.", Toast.LENGTH_LONG).show();
                                                    auth.signOut();
                                                    getActivity().onBackPressed();
                                                    Intent i = new Intent(getContext(), LogReg.class);
                                                    startActivity(i);
                                                } else {
                                                    try {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthUserCollisionException existEmail) {
                                                        Toast.makeText(getContext(), "Such E-mail already exists!", Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(getContext(), "Unespected error. Try again.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("ERROR:", e.getMessage());
                                    }
                                });

                            }
                        });
            }     } else {
                Toast.makeText(getContext(), "Such e-mail already exists!", Toast.LENGTH_LONG).show();
            }


        if (!newPw.isEmpty() && !newPw.equals("")) {
            user.updatePassword(newPw);
        }


    }

    static boolean exists;

    public static boolean userExists(String email){
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        exists = !task.getResult().getSignInMethods().isEmpty();


                    }
                });
    return exists;
    }


    private void fetchUserData() {
        dbRef.child(userPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("Name").getValue().toString();
                email = user.getEmail();
                if (!email.equals( dbRef.child(userPath).child("E-mail"))){
                    dbRef.child(userPath).child("E-mail").setValue(email);
                }
                nameTxt.setText(username);
                emailTxt.setText(email);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profileImg.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final String imgId = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child("images/" + imgId);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            addPhotoPath(imgId);
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void addPhotoPath(String imgId) {

        dbRef.child(userPath).child("Photo").setValue(imgId).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "There was an error adding your photo. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

    }


}
