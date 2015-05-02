package com.bambazu.fireup.Model;

/**
 * Created by blackxcorpio on 02/05/2015.
 */
public class Service {
    private String serviceIcon;
    private String serviceName;

    public Service(String serviceIcon, String serviceName){
        this.serviceIcon = serviceIcon;
        this.serviceName = serviceName;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
