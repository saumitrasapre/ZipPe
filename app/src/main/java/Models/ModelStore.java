package Models;

import java.util.List;

public class ModelStore {
    private String mainImageUrl;
    private String name;
    private String rating;
    private String category;
    private List<String> otherImages;

    public ModelStore() {
    }

    public ModelStore(String mainImageUrl, String name, String rating, String category, List<String> otherImages) {
        this.mainImageUrl = mainImageUrl;
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.otherImages = otherImages;
    }

    public ModelStore(String mainImageUrl, String name, String rating, String category) {
        this.mainImageUrl = mainImageUrl;
        this.name = name;
        this.rating = rating;
        this.category = category;
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
