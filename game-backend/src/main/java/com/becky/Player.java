package com.becky;

import org.java_websocket.WebSocket;
import java.awt.geom.Point2D;

public class Player implements GameEntity {
    public static final float MAX_VELOCITY = 200.0f;
    public static final float ACCELERATION = 150.0f;
    private final Point2D.Float position = new Point2D.Float(0.0f, 0.0f);
    private final Point2D.Float velocity = new Point2D.Float(0.0f, 0.0f);
    private final Point2D.Float acceleration = new Point2D.Float(0.0f, 0.0f);
    private boolean decelerating = true;
    private String playerUsername;
    private final WebSocket connection;
    private final String authenticationString;

    public Player(final String playerUsername, final String authenticationString, final WebSocket connection) {
        this.playerUsername = playerUsername;
        this.connection = connection;
        this.authenticationString = authenticationString;
    }

    public String getAuthenticationString() {
        return this.authenticationString;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public WebSocket getConnection() {
        return this.connection;
    }

    public void setPlayerUsername(final String playerUsername) {
        this.playerUsername = playerUsername;
    }

    @Override
    public int getX_position() {
        return Math.round(position.x);
    }

    @Override
    public void setX_position(final int x_position) {
        this.position.x = x_position;
    }

    @Override
    public int getY_position() {
        return Math.round(this.position.y);
    }

    @Override
    public void setY_position(final int y_position) {
        this.position.y = y_position;
    }

    @Override
    public float getX_velocity() {
        return this.velocity.x;
    }

    @Override
    public void setX_velocity(final float x_velocity) {
        this.velocity.x = x_velocity;
    }

    @Override
    public float getY_velocity() {
        return velocity.y;
    }

    @Override
    public void setY_velocity(final float y_velocity) {
        this.velocity.y = y_velocity;
    }

    @Override
    public float getX_acceleration() {
        return this.acceleration.x;
    }

    @Override
    public void setX_acceleration(final float accelaration) {
        this.acceleration.x = accelaration;
    }

    @Override
    public float getY_acceleration() {
        return this.acceleration.y;
    }

    @Override
    public void setY_acceleration(final float accelaration) {
        this.acceleration.y = accelaration;
    }

    @Override
    public void setDecelerating(final boolean decelerating) {
         this.decelerating = decelerating;
    }

    @Override
    public boolean isDecelerating() {
        return this.decelerating;
    }


    public void tick(final long elapsedTime) {
        if(decelerating) {
            final float timeMultiplier = elapsedTime / 1000.0f;
            final float absX = Math.abs(this.velocity.x) * timeMultiplier;
            final float absY = Math.abs(this.velocity.y) * timeMultiplier;
            if(this.velocity.x < 0) {
                this.velocity.x += absX;
                if(this.velocity.x > 0) {
                    this.velocity.x = 0;
                }
            }
            else {
                this.velocity.x -= absX;
                if(this.velocity.x < 0) {
                    this.velocity.x = 0;
                }
            }

            if(this.velocity.y < 0) {
                this.velocity.y += absY;
                if(this.velocity.y > 0) {
                    this.velocity.y = 0;
                }
            }
            else {
                this.velocity.y -= absY;
                if(this.velocity.y < 0) {
                    this.velocity.y = 0;
                }
            }

            this.position.x += timeMultiplier * this.velocity.x;
            this.position.y += timeMultiplier * this.velocity.y;
        }
        else {
            final float multiplier = elapsedTime / 1000.0f;
            this.velocity.x += multiplier * this.acceleration.x;
            this.velocity.y += multiplier * this.acceleration.y;
            capVelocity();
            this.position.x += multiplier * this.velocity.x;
            this.position.y += multiplier * this.velocity.y;
            System.out.println(this.velocity.x + " " + this.velocity.y);
        }
    }

    private void capVelocity() {
        if(this.velocity.x > MAX_VELOCITY) {
            this.velocity.x = MAX_VELOCITY;
        }
        else if(this.velocity.x < -MAX_VELOCITY) {
            this.velocity.x = -MAX_VELOCITY;
        }
        if(this.velocity.y > MAX_VELOCITY) {
            this.velocity.y = MAX_VELOCITY;
        }
        else if(this.velocity.y < -MAX_VELOCITY) {
            this.velocity.y = -MAX_VELOCITY;
        }
    }
}
