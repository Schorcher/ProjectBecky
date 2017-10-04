package com.becky;

import org.java_websocket.WebSocket;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Player implements GameEntity {
    public static final float MAX_VELOCITY = 600.0f;
    public static final float ACCELERATION = 1800.0f;
    private final Point2D.Float position = new Point2D.Float(0.0f, 0.0f);
    private final Point2D.Float velocity = new Point2D.Float(0.0f, 0.0f);
    private final Point2D.Float acceleration = new Point2D.Float(0.0f, 0.0f);
    private String playerUsername;
    private final WebSocket connection;
    private final String authenticationString;
    private boolean usernameFinal = false;
    private float angles;
    private Gun playerGun = new DefaultGun(this);
    private final List<Bullet> bulletsList = new ArrayList<>();
    private boolean firingWeapon = false;
    private int collisionRadius = 32;
    private int health = 10;
    private int score = 0;

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
    public float getXPosition() {
        return this.position.x;
    }

    @Override
    public void setXPosition(final float position) {
        this.position.x = position;
    }

    @Override
    public float getYPosition() {
        return this.position.y;
    }

    @Override
    public void setYPosition(final float position) {
        this.position.y = position;
    }

    @Override
    public float getXVelocity() {
        return this.velocity.x;
    }

    @Override
    public void setXVelocity(final float velocity) {
        this.velocity.x = velocity;
    }

    @Override
    public float getYVelocity() {
        return velocity.y;
    }

    @Override
    public void setYVelocity(final float velocity) {
        this.velocity.y = velocity;
    }

    @Override
    public float getXAcceleration() {
        return this.acceleration.x;
    }

    @Override
    public void setXAcceleration(final float accelaration) {
        this.acceleration.x = accelaration;
    }

    @Override
    public float getYAcceleration() {
        return this.acceleration.y;
    }

    @Override
    public void setYAcceleration(final float accelaration) {
        this.acceleration.y = accelaration;
    }

    @Override
    public void setAngles(final float angles) {
        this.angles = angles;
    }

    @Override
    public float getAngles() {
        return this.angles;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(final int health) {
        this.health = Math.max(health, 0);
    }

    public int getCollisionRadius() {
        return this.collisionRadius;
    }

    public void setCollisionRadius(final int radius) {
        this.collisionRadius = radius;
    }

    public void setUsernameFinal() {
        this.usernameFinal = true;
    }

    public boolean isUsernameFinal() {
        return this.usernameFinal;
    }

    public Gun getGun() {
        return this.playerGun;
    }

    public void setGun(final Gun gun) {
        this.playerGun = gun;
    }

    public List<Bullet> getBulletsList() {
        return new ArrayList<>(this.bulletsList);
    }

    public void addBullet(final Bullet bullet) {
        this.bulletsList.add(bullet);
    }

    public void removeBullet(final Bullet bullet) {
        this.bulletsList.remove(bullet);
    }

    public void setFiringWeapon(final boolean firing) {
        this.firingWeapon = firing;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(final int amt) {
        this.score += amt;
    }

    @Override
    public void tick(final long elapsedTime) {
        tickVelocity(elapsedTime);
        tickShooting();
        tickBullets(elapsedTime);
    }

    private void tickBullets(final long elapsedTime) {
        for(final Bullet bullet: bulletsList) {
            bullet.tick(elapsedTime);
        }
    }

    private void tickShooting() {
        if(firingWeapon) {
            playerGun.fire();
        }
    }

    private void tickVelocity(final long elapsedTime) {
        final float fraction = elapsedTime/1000.0f;

        //floats don't do well with ==
        //x component
        if(Math.abs(this.acceleration.x) < 0.05f) {
            //decelerating
            if(this.velocity.x > 0.0f) {
                this.velocity.x -= Player.ACCELERATION * fraction;
                if(this.velocity.x < 0.0f) {
                    this.velocity.x = 0.0f;
                }
            }
            else {
                this.velocity.x += Player.ACCELERATION * fraction;
                if(this.velocity.x > 0.0f) {
                    this.velocity.x = 0.0f;
                }
            }
        }
        else {
            //accelerating
            this.velocity.x += this.acceleration.x * fraction;
        }

        //y component
        if(Math.abs(this.acceleration.y) < 0.5f) {
            //decelerating
            if(this.velocity.y > 0) {
                this.velocity.y -= Player.ACCELERATION * fraction;
                if(this.velocity.y < 0.0f) {
                    this.velocity.y = 0.0f;
                }
            }
            else {
                this.velocity.y += Player.ACCELERATION * fraction;
                if(this.velocity.y > 0.0f) {
                    this.velocity.y = 0.0f;
                }
            }
        }
        else {
            //accelerating
            this.velocity.y += this.acceleration.y * fraction;
        }

        capVelocity();
        this.position.x += this.velocity.x * fraction;
        this.position.y += this.velocity.y * fraction;
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
