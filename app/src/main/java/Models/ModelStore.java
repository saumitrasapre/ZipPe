package Models;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class ModelStore {
    private String mainImageUrl;
    private String name;
    private String rating;
    private String category;
    private List<String> otherImages;
    private String address;
    private GeoPoint location;
    private String postalCode;

    public ModelStore() {
    }

    public ModelStore(String mainImageUrl, String name, String rating, String category, List<String> otherImages, String address, GeoPoint location, String postalCode) {
        this.mainImageUrl = mainImageUrl;
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.otherImages = otherImages;
        this.address = address;
        this.location = location;
        this.postalCode = postalCode;
    }

    public ModelStore(String mainImageUrl, String name, String rating, String category, String address, GeoPoint location, String postalCode) {
        this.mainImageUrl = mainImageUrl;
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.address = address;
        this.location = location;
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getOtherImages() {
        return otherImages;
    }

    public void setOtherImages(List<String> otherImages) {
        this.otherImages = otherImages;
    }
}
