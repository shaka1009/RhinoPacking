package com.rhinopacking.providers;


import com.google.firebase.firestore.FirebaseFirestore;

public class UserProvider {
    private FirebaseFirestore db;

    public UserProvider() {
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
