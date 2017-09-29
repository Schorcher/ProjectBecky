package com.becky.networked;

import org.json.JSONObject;

public class InputStateChange
{
    private String username;
    private String authenticationString;
    private String inputName;
    private boolean flag;
    private float angle;

    public InputStateChange() {}

    public InputStateChange(final String json) {
        if(!json.startsWith(InputStateChange.class.getSimpleName())) {
            throw new IllegalArgumentException("Json String does not define a InputStateChange object.");
        }

        final JSONObject obj = new JSONObject(json);
        this.username = obj.getString("username");
        this.authenticationString = obj.getString("authenticationString");
        this.inputName = obj.getString("inputName");
        this.flag = obj.getBoolean("flag");
        this.angle = (float)obj.getDouble("angle");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getAuthenticationString() {
        return authenticationString;
    }

    public void setAuthenticationString(final String authenticationString) {
        this.authenticationString = authenticationString;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(final String inputName) {
        this.inputName = inputName;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(final boolean flag) {
        this.flag = flag;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(final float angle) {
        this.angle = angle;
    }

    public String jsonSerialize() {
        return InputStateChange.class.getSimpleName() + ":" + new JSONObject(this).toString();
    }
}
