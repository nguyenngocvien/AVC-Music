package com.example.musicplayer.Model;

import java.io.Serializable;

public class Baihat implements Serializable {
    private String Idbaihat;
    private String Tenbaihat;
    private String Hinhanhbaihat;
    private String Casi;
    private String Linkbaihat;

    public Baihat(String idbaihat, String tenbaihat, String hinhanhbaihat, String casi, String linkbaihat) {
        Idbaihat = idbaihat;
        Tenbaihat = tenbaihat;
        Hinhanhbaihat = hinhanhbaihat;
        Casi = casi;
        Linkbaihat = linkbaihat;
    }

    public String getIdbaihat() {
        return Idbaihat;
    }

    public void setIdbaihat(String idbaihat) {
        Idbaihat = idbaihat;
    }

    public String getTenbaihat() {
        return Tenbaihat;
    }

    public void setTenbaihat(String tenbaihat) {
        Tenbaihat = tenbaihat;
    }

    public String getHinhanhbaihat() {
        return Hinhanhbaihat;
    }

    public void setHinhanhbaihat(String hinhanhbaihat) {
        Hinhanhbaihat = hinhanhbaihat;
    }

    public String getCasi() {
        return Casi;
    }

    public void setCasi(String casi) {
        Casi = casi;
    }

    public String getLinkbaihat() {
        return Linkbaihat;
    }

    public void setLinkbaihat(String linkbaihat) {
        Linkbaihat = linkbaihat;
    }
}
