package com.becky.networked;

import org.json.JSONObject;

public class ServerUsernameRequestStatus {
    private String status;
    private String message;

    public ServerUsernameRequestStatus() {}

    public ServerUsernameRequestStatus(final String json) {
        if(!json.startsWith(ServerUsernameRequestStatus.class.getSimpleName())) {
            throw new IllegalArgumentException("Json string does not define a valid ServerUsernameRequestStatus object!");
        }

        final JSONObject obj = new JSONObject(json);
        this.status = obj.getString("status");
        this.message = obj.getString("message");
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public String jsonSerialize() {
        return ServerUsernameRequestStatus.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
