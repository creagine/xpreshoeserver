package com.creaginetech.expreshoesserver.Model;

public class Service {

    private String serviceName, serviceImage, description, price;

    public Service() {
    }

//    public Service(String serviceName, String serviceImage) {
//        this.serviceName = serviceName;
//        this.serviceImage = serviceImage;
//    }

    public Service(String serviceName, String serviceImage, String description, String price) {
        this.serviceName = serviceName;
        this.serviceImage = serviceImage;
        this.description = description;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
