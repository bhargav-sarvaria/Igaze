package com.igaze.Models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class Wish {

    @JsonField(name = "wishId")
    private String wishId;

    @JsonField(name = "wishPrefix")
    private String wishPrefix;

    @JsonField(name = "wish")
    private String wish;

    @JsonField(name = "targetDate")
    private String targetDate;

    @JsonField(name = "sharingType")
    private String sharingType;

    @JsonField(name = "latitude")
    private String latitude;

    @JsonField(name = "longitude")
    private String longitude;

    @JsonField(name = "types")
    private String types;

    @JsonField(name = "timestamp")
    private String timestamp;

    public String getWishId() {
        return wishId;
    }

    public void setWishId(String wishId) {
        this.wishId = wishId;
    }

    public String getWishPrefix() {
        return wishPrefix;
    }

    public void setWishPrefix(String wishPrefix) {
        this.wishPrefix = wishPrefix;
    }

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getSharingType() {
        return sharingType;
    }

    public void setSharingType(String sharingType) {
        this.sharingType = sharingType;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
