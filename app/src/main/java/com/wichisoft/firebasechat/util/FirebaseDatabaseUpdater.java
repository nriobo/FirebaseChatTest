package com.wichisoft.firebasechat.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FirebaseDatabaseUpdater {

    public FirebaseDatabaseUpdater () {}

    public static void updateLocation (double lat, double lon) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" +
                FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> result = new HashMap<>();
            result.put("latitude", lat);
            result.put("longitude", lon);
            ref.updateChildren(result);
    }
}
