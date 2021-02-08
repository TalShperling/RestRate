package com.example.restrate.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {
    private final String RESTAURANT_DB_NAME = "restaurants";

    public void getAllRestaurants(GenericRestaurantListenerWithParam<List<Restaurant>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Restaurant> restList = new LinkedList<Restaurant>();

        db.collection(RESTAURANT_DB_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Restaurant rest = document.toObject(Restaurant.class);
                                restList.add(rest);
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        listener.onComplete(restList);
                    }
                });

        listener.onComplete(restList);
    }

    public void getRestaurantById(String id, GenericRestaurantListenerWithParam<Restaurant> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(RESTAURANT_DB_NAME)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Restaurant rest = null;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        rest = document.toObject(Restaurant.class);
                        Log.d("TAG","Restaurant returned");
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
                listener.onComplete(rest);
            }
        });
    }

    public void upsertRestaurant(Restaurant restToAdd, GenericRestaurantListenerWithNoParam listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(RESTAURANT_DB_NAME)
                .document(restToAdd.getId())
                .set(restToAdd)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Log.d("TAG", "Restaurant added with ID: " + restToAdd.getId());
                        listener.onComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding restaurant", e);
                        listener.onComplete();
                    }
                });

    }

    public void deleteRestaurantById(String id, GenericRestaurantListenerWithNoParam listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(RESTAURANT_DB_NAME).document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        listener.onComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });

    }
}
