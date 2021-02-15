package com.example.restrate.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ModelFirebase {
    private final String RESTAURANT_DB_NAME = "restaurants";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    public void getAllRestaurants(Long lastUpdated, final GenericEventListenerWithParam listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Restaurant> restList = new LinkedList<Restaurant>();
        Timestamp ts = new Timestamp(lastUpdated, 0);

        db.collection(RESTAURANT_DB_NAME)
                .whereGreaterThanOrEqualTo("lastUpdated", ts)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Restaurant rest = new Restaurant();
                                rest.fromMap(document.getData());
                                restList.add(rest);
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                        listener.onComplete(restList);
                    }
                });
    }

    public void getRestaurantById(String id, GenericEventListenerWithParam<Restaurant> listener) {
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
                                rest = new Restaurant();
                                rest.fromMap(document.getData());
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

    public void upsertRestaurant(Restaurant restToAdd, GenericEventListenerWithParam<Restaurant> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docReference;

        if (restToAdd.getId() == null) {
            docReference = db.collection(RESTAURANT_DB_NAME).document();
            restToAdd.setId(docReference.getId());
        } else {
            docReference = db.collection(RESTAURANT_DB_NAME).document(restToAdd.getId());
        }

        docReference.set(restToAdd.toMap())
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        listener.onComplete(restToAdd);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding restaurant", e);
                        listener.onComplete(null);
                    }
                });

    }

    public void deleteRestaurant(Restaurant restaurant, GenericEventListenerWithNoParam listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        restaurant.setIsDeleted(true);

        db.collection(RESTAURANT_DB_NAME)
                .document(restaurant.getId())
                .set(restaurant.toMap())
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

    public void uploadImage(Bitmap imageBmp, GenericEventListenerWithParam<String> listener) {
        UUID id = UUID.randomUUID();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef = storage.getReference().child("images").child(id.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        listener.onComplete(downloadUrl.toString());
                    }
                });
            }
        });
    }

    public void login(String email, String password, GenericEventListenerWithNoParam listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            listener.onComplete();
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public void register(String email, String password, String fullName, GenericEventListenerWithParam<FirebaseUser> listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName).build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        listener.onComplete(user);
                                    } else {
                                        Log.w("TAG", "updateDisplayName:failure", task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public void signOut(GenericEventListenerWithNoParam listener) {
        FirebaseAuth.getInstance().signOut();
        listener.onComplete();
    }
}
