package com.creaginetech.xpreshoesshipper.Model;

public class Shipper {
    private String nameShipper,phoneShipper,passwordShipper;

    public Shipper() {
    }

    public Shipper(String nameShipper, String phoneShipper, String passwordShipper) {
        this.nameShipper = nameShipper;
        this.phoneShipper = phoneShipper;
        this.passwordShipper = passwordShipper;
    }

    public String getNameShipper() {
        return nameShipper;
    }

    public void setNameShipper(String nameShipper) {
        this.nameShipper = nameShipper;
    }

    public String getPhoneShipper() {
        return phoneShipper;
    }

    public void setPhoneShipper(String phoneShipper) {
        this.phoneShipper = phoneShipper;
    }

    public String getPasswordShipper() {
        return passwordShipper;
    }

    public void setPasswordShipper(String passwordShipper) {
        this.passwordShipper = passwordShipper;
    }
}
