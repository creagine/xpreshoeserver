package com.creaginetech.expreshoesserver.Model;

public class Shop {

    private String shopName,shopImage, shopAddress, shopPhone, shopLatlng, shopAddressDetail;

    public Shop() {
    }

    public Shop(String shopName, String shopImage, String shopAddress, String shopPhone, String shopLatlng, String shopAddressDetail) {
        this.shopName = shopName;
        this.shopImage = shopImage;
        this.shopAddress = shopAddress;
        this.shopPhone = shopPhone;
        this.shopLatlng = shopLatlng;
        this.shopAddressDetail = shopAddressDetail;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getShopLatlng() {
        return shopLatlng;
    }

    public void setShopLatlng(String shopLatlng) {
        this.shopLatlng = shopLatlng;
    }

    public String getShopAddressDetail() {
        return shopAddressDetail;
    }

    public void setShopAddressDetail(String shopAddressDetail) {
        this.shopAddressDetail = shopAddressDetail;
    }
}
