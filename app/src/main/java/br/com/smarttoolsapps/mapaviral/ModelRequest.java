package br.com.smarttoolsapps.mapaviral;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelRequest {

    @SerializedName("error")
    private Integer error;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private List<Object> data;

    public ModelRequest(Integer error, String msg, List<Object> data) {

        this.error = error;
        this.msg = msg;
        this.data = data;
    }

    public Integer getError() {

        return this.error;
    }

    public String getMsg() {

        return this.msg;
    }

    public List<Object> getData() {

        return this.data;
    }
}
