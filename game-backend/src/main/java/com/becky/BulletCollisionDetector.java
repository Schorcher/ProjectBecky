package com.becky;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Determines if bullets are colliding with players
 * Created by Clayton on 10/3/2017.
 */
public class BulletCollisionDetector {
    private final Player[] players;
    private int numPlayers;

    /**
     * Creates a new collision detector for bullets
     * @param maxPlayers The maximum number of players the detector should support
     */
    public BulletCollisionDetector(final int maxPlayers) {
        players = new Player[maxPlayers];
    }

    public void setPlayers(final Collection<Player> players) {
        if(players.size() >= this.players.length) {
            throw new IllegalArgumentException("Provided list contains too many players. Construct a new BulletCollisionDetector instance with a higher maxPlayers value to avoid this.");
        }

        synchronized (this.players) {
            players.toArray(this.players);
            this.numPlayers = players.size();
        }
    }

    public Map<Bullet, Player> getBulletCollisions() {
        final HashMap<Bullet, Player> collisions = new HashMap<>();

        synchronized (this.players) {
            for(int i = 0; i < numPlayers; i++) {
                testPlayerBullets(i, collisions);
            }
        }

        return collisions;
    }

    private void testPlayerBullets(final int playerIndex, final HashMap<Bullet, Player> collisions) {
        final Player player = players[playerIndex];
        final List<Bullet> bullets = player.getBulletsList();

        bulletLoop:
        for(final Bullet bullet: bullets) {
            for(int i = 0; i < playerIndex; i++) {
                if(isBulletColliding(players[i], bullet)) {
                    collisions.put(bullet, players[i]);
                    continue bulletLoop;
                }
            }
            for(int i = playerIndex+1; i < this.numPlayers; i++) {
                if(isBulletColliding(players[i], bullet)) {
                    collisions.put(bullet, players[i]);
                    continue bulletLoop;
                }
            }
        }
    }

    private boolean isBulletColliding(final Player player, final Bullet bullet) {
        final float delta = distance(player.getXPosition(), player.getYPosition(), bullet.getXPosition(), bullet.getYPosition());
        final float collisionDistance = player.getCollisionRadius() + bullet.getCollisionRadius();
        return delta <= collisionDistance;
    }

    private float distance(final float x1, final float y1, final float x2, final float y2) {
        return (float)StrictMath.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }
}
