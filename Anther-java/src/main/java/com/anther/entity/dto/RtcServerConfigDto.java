package com.anther.entity.dto;

import java.io.Serializable;
import java.util.List;

public class RtcServerConfigDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> stunUrls;
    private List<String> turnUrls;
    private String turnUsername;
    private String turnCredential;

    public List<String> getStunUrls() {
        return stunUrls;
    }

    public void setStunUrls(List<String> stunUrls) {
        this.stunUrls = stunUrls;
    }

    public List<String> getTurnUrls() {
        return turnUrls;
    }

    public void setTurnUrls(List<String> turnUrls) {
        this.turnUrls = turnUrls;
    }

    public String getTurnUsername() {
        return turnUsername;
    }

    public void setTurnUsername(String turnUsername) {
        this.turnUsername = turnUsername;
    }

    public String getTurnCredential() {
        return turnCredential;
    }

    public void setTurnCredential(String turnCredential) {
        this.turnCredential = turnCredential;
    }
}
