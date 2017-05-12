package edu.csulb.android.restofit.pojos;

public class Review {

    private transient String id;
    private String title;
    private String description;
    private String photoUrl;
    private String photoTags;
    private String isPositive;
    private int rating;
    private double longitude, latitude;

    public Review() {
    }

    public Review(String title, String description, String photoUrl, int rating) {
        this.setTitle(title);
        this.setDescription(description);
        this.setPhotoUrl(photoUrl);
        this.setRating(rating);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIsPositive() {
        return isPositive;
    }

    public void setIsPositive(String isPositive) {
        this.isPositive = isPositive;
    }

    public String getPhotoTags() {
        return photoTags;
    }

    public void setPhotoTags(String photoTags) {
        this.photoTags = photoTags;
    }
}
