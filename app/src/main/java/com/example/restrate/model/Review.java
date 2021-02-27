package com.example.restrate.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity(primaryKeys = {"restaurantId", "userId"})
public class Review {
    @NonNull
    private String restaurantId;
    @NonNull
    private String userId;
    private String description;
    private String rate;
    private String costMeter;
    private Long lastUpdated;
    private Boolean isDeleted = false;

    public Review(@NonNull String restaurantId, @NonNull String userId) {
        this.restaurantId = restaurantId;
        this.userId = userId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("restaurantId", restaurantId);
        result.put("userId", userId);
        result.put("description", description);
        result.put("rate", rate);
        result.put("costMeter", costMeter);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        result.put("isDeleted", isDeleted);
        return result;
    }

    public void fromMap(Map<String, Object> map) {
        restaurantId = (String) Objects.requireNonNull(map.get("restaurantId"));
        userId = (String) Objects.requireNonNull(map.get("userId"));
        description = (String) map.get("description");
        costMeter = (String) map.get("costMeter");
        rate = (String) map.get("rate");
        lastUpdated = ((Timestamp) Objects.requireNonNull(map.get("lastUpdated"))).getSeconds();
        isDeleted = (Boolean) map.get("isDeleted");
    }

    @NonNull
    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(@NonNull String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCostMeter() {
        return costMeter;
    }

    public void setCostMeter(String costMeter) {
        this.costMeter = costMeter;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
