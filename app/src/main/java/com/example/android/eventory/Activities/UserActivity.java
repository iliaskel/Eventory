package com.example.android.eventory.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.eventory.R;
import com.example.android.eventory.Signingformation.UserInformation;
import com.example.android.eventory.Utils.BottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ikelasid on 10/1/2017.
 * This Activity displays the user section, displaying the name, his personal photo
 * and the events he is attending
 */

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private static final int ACTIVITY_NUMBER=2;
    private final Context mContext = UserActivity.this;
    private final int PICK_IMAGE_REQUEST = 71;


    private String mUserId;

    private TextView usernameTextView;
    private CircleImageView profileImageView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;
    private String userId;









    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: entered");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        setUpBottomNavigationView();
        findViewsById();
        setUpFireBase();


        //Pop up menu for profile image functions
        setUpPopUpMenu();

        setProfilePicture();



    }

    private void setProfilePicture() {
        Bitmap profileImage=null;

        try {
            File filePath = getFileStreamPath(mUserId);
            Log.d(TAG, "setProfilePicture: onActivity FILEPATH :::: " +filePath.getAbsolutePath());
            FileInputStream fi = new FileInputStream(filePath);
            profileImage = BitmapFactory.decodeStream(fi);
            profileImageView.setImageBitmap(profileImage);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "setProfilePicture: exception catched :::: " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void setUpPopUpMenu() {
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: profile Image pressed");
                PopupMenu popupMenu = new PopupMenu(mContext,profileImageView);

                popupMenu.getMenuInflater().inflate(R.menu.profile_image_pop_up_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId() == R.id.menu_image_upload){
                            chooseProfilePicture();
                        }
                        else if(item.getItemId() == R.id.menu_image_delete){

                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void chooseProfilePicture() {


        Intent choosePictureIntent = new Intent();
        choosePictureIntent.setType("image/*");
        choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Profile Picture"), PICK_IMAGE_REQUEST);

    }

    /** Changing the profile picture and store it afterwards asynchronously **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {

            Uri imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            new StoreProfileImage().execute(imageUri);

        }
    }


    /** TODO: We could store the username once he logs in and after that chech only the userId
     * to decide whether to keep the same username(ie its the same user) or to query the logged in
     *  users user name from FireBase**/
    private void setUpFireBase() {
        mAuth= FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUserId = mCurrentUser.getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mCurrentUser =mAuth.getCurrentUser();
        userId= mCurrentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("users/"+userId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " +dataSnapshot.getValue());
                UserInformation userInformation=new UserInformation();
                userInformation.setName(dataSnapshot.getValue(UserInformation.class).getName());
                Log.d(TAG, "onDataChange: "+userInformation.getName());
                usernameTextView.setText(userInformation.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private class StoreProfileImage extends AsyncTask<Uri,Void,Void>{

        @Override
        protected Void doInBackground(Uri... uris) {

            Uri imageUri = uris[0];
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                FileOutputStream fos = openFileOutput(mUserId,Context.MODE_PRIVATE);
                selectedImage.compress(Bitmap.CompressFormat.PNG,10,fos);
                Log.d(TAG, "onActivityResult: SAVED");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeToast("Image successfully saved");
        }
    }

    private void findViewsById(){
        usernameTextView=findViewById(R.id.tv_user_user_name);
        profileImageView =findViewById(R.id.profile_image);

    }

    private void setUpBottomNavigationView(){
        Log.d(TAG, "setUpBottomNavigationView: setting up bottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(UserActivity.this,bottomNavigationViewEx);
        Menu menu=bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void makeToast(String msg){
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }




    private void nonNeededCode(){
/**=================================NON NEEDED===============================**/

    //ChooseImageToUpload()







    //uploadProfilePicture()
    /** upladProfilePicture()
     *
     private void uploadProfilePicture(Uri filePathUri) {

     if(filePathUri != null)
     {
     final ProgressDialog progressDialog = new ProgressDialog(this);
     progressDialog.setTitle("Uploading...");
     progressDialog.show();

     StorageReference ref = mStorageRef.child("images/"+ mUserId);
     Log.d(TAG, "uploadProfilePicture: Path: images/"+mUserId);
     ref.putFile(filePathUri)
     .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
    progressDialog.dismiss();
    Toast.makeText(mContext, "Uploaded", Toast.LENGTH_SHORT).show();
    }
    })
     .addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
    progressDialog.dismiss();
    Toast.makeText(mContext, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    })
     .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
    .getTotalByteCount());
    progressDialog.setMessage("Uploaded "+(int)progress+"%");
    }
    });
     }

     }
     **/

    /**
     private void downloadProfilePicture() {
     try {
     File localFile = null;
     StorageReference pathReference = mStorageRef.child("images/"+mUserId);
     Log.d(TAG, "onCreate: Download Path: "+pathReference.toString());
     localFile = File.createTempFile("images", "jpeg");
     final File finalLocalFile = localFile;

     pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
    @Override
    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
    // Local temp file has been created
    Log.d(TAG, "onSuccess: File downloaded");
    String localPath = finalLocalFile.getPath();
    Log.d(TAG, "onSuccess: File downloaded - LocalPath: "+localPath.toString());
    Bitmap bMap = BitmapFactory.decodeFile(localPath);
    profileImageView.setImageBitmap(bMap);
    }
    }).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception exception) {
    // Handle any errors
    Log.d(TAG, "onFailure: File downloading Error" +exception.getMessage());

    }
    });


     } catch (IOException e) {
     e.printStackTrace();
     }
     }
     **/

    }
}
