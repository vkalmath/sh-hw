package hello.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Listen {

    @SerializedName("subtopic")
    @Expose
    private String subtopic;
    @SerializedName("listenDate")
    @Expose
    private String listenDate;
    @SerializedName("user")
    @Expose
    private String user;

    public String getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(String subtopic) {
        this.subtopic = subtopic;
    }

    public String getListenDate() {
        return listenDate;
    }

    public void setListenDate(String listenDate) {
        this.listenDate = listenDate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}