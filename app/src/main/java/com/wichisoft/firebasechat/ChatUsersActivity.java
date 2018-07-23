package com.wichisoft.firebasechat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.wichisoft.firebasechat.model.User;
import com.wichisoft.firebasechat.util.FirebaseDatabaseUpdater;
import com.wichisoft.firebasechat.util.UsersAdapter;

public class ChatUsersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView tvMyPosition;
    private DatabaseReference mUsersDBRef;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private UsersAdapter adapter;
    private List<User> mUsersList = new ArrayList<>();
    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);

        tvMyPosition = findViewById(R.id.textViewMyPosition);

        mAuth = FirebaseAuth.getInstance();
        mUsersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerView = (RecyclerView)findViewById(R.id.usersRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            FirebaseDatabaseUpdater.updateLocation(0.0,0.0);    // sync with server
        } else {
            int permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission == PackageManager.PERMISSION_GRANTED) {
                startTrackerService();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTrackerService() {
        startService(new Intent(this, TrackingService.class));
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
    }

    private void populaterecyclerView(){
        adapter = new UsersAdapter(mUsersList, this);
        mRecyclerView.setAdapter(adapter);
    }

    private void queryUsersAndAddthemToList(){
        mUsersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvMyPosition.setText("My pos: " + TrackingService.latitude + "," + TrackingService.longitude);
                mUsersList.clear();
                if(dataSnapshot.getChildrenCount() > 0){
                    for(DataSnapshot snap: dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        try {
                            if(!user.getUserId().equals(mAuth.getCurrentUser().getUid())){
                                mUsersList.add(user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                populaterecyclerView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfUserIsSignIn();

        /**query users and add them to a list**/
        queryUsersAndAddthemToList();
    }



    private void checkIfUserIsSignIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            /**go to login user first**/
            goToSignIn();
        }
    }

    private void goToSignIn(){
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logOutuser();
                return true;
            case R.id.userProfile:
                goToUpdateUserProfile();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOutuser(){
        FirebaseAuth.getInstance().signOut();
        //now send user back to login screen
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void goToUpdateUserProfile(){
        startActivity(new Intent(this, UpdateProfileActivity.class));
    }

}
