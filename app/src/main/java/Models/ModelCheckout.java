package Models;

public class ModelCheckout {

    String productName;
    String productPrice;
    int productQuantity;
    String productCode;
    String productImage;
    String productWeight;
    String storeId;
    String resultPrice;

    public ModelCheckout() {
    }

    public ModelCheckout(String productName, String productPrice, int productQuantity, String productCode, String productImage, String productWeight, String storeId, String resultPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.productCode = productCode;
        this.productImage = productImage;
        this.productWeight = productWeight;
        this.storeId = storeId;
        this.resultPrice = resultPrice;
    }

    public String getResultPrice() {
        return resultPrice;
    }

    public void setResultPrice(String resultPrice) {
        this.resultPrice = resultPrice;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
