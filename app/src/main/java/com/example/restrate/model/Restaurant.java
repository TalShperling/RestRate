package com.example.restrate.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Restaurant {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private String siteLink;
    private String imageURL;
    private Double rate;
    private Long costMeter;
    private Long lastUpdated;
    private Boolean isDeleted = false;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("description", description);
        result.put("address", address);
        result.put("phoneNumber", phoneNumber);
        result.put("siteLink", siteLink);
        result.put("imageURL", imageURL);
        result.put("rate", rate);
        result.put("costMeter", costMeter);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        result.put("isDeleted", isDeleted);
        return result;
    }

    public void fromMap(Map<String, Object> map) {
        id = (String) map.get("id");
        name = (String) map.get("name");
        description = (String) map.get("description");
        address = (String) map.get("address");
        phoneNumber = (String) map.get("phoneNumber");
        siteLink = (String) map.get("siteLink");
        imageURL = (String) map.get("imageURL");
        costMeter = (Long) map.get("costMeter");
        rate = (Double) map.get("rate");
        lastUpdated = ((Timestamp) map.get("lastUpdated")).getSeconds();
        isDeleted = (Boolean) map.get("isDeleted");
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Long getCostMeter() {
        return costMeter;
    }

    public void setCostMeter(Long costMeter) {
        this.costMeter = costMeter;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
