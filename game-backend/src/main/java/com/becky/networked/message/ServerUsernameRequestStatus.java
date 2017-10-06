package com.becky.networked.message;

import org.json.JSONObject;

public class ServerUsernameRequestStatus {
    private String status;
    private String message;

    public ServerUsernameRequestStatus() {}

    public ServerUsernameRequestStatus(final String json) {
        final int jsonStartIndex = json.indexOf('{');
        if(jsonStartIndex == -1) {
            throw new IllegalArgumentException("Invalid JSON Object");
        }
        if(!json.startsWith(ServerUsernameRequestStatus.class.getSimpleName())) {
            throw new IllegalArgumentException("Json string does not define a valid ServerUsernameRequestStatus object!");
        }

        final JSONObject obj = new JSONObject(json.substring(jsonStartIndex));
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
