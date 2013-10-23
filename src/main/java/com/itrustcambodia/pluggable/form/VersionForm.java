package com.itrustcambodia.pluggable.form;

import java.io.Serializable;

public class VersionForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4607244136868093499L;

    private double version;

    private String description;

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
