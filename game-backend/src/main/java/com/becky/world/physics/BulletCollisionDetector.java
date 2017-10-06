package com.becky.world.physics;

import com.becky.world.entity.Bullet;
import com.becky.world.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Determines if bullets are colliding with players
 * Created by Clayton on 10/3/2017.
 */
public class BulletCollisionDetector {
    public Map<Bullet, Player> getBulletCollisions(final Player[] players) {
        final HashMap<Bullet, Player> collisions = new HashMap<>();

        for(int i = 0, length = players.length; i < length; i++) {
            testPlayerBullets(i, players, collisions);
        }

        return collisions;
    }

    private void testPlayerBullets(final int playerIndex, final Player[] players, final HashMap<Bullet, Player> collisions) {
        final Player player = players[playerIndex];
        final List<Bullet> bullets = player.getBulletsList();
        final int numPlayers = players.length;

        bulletLoop:
        for(final Bullet bullet: bullets) {
            for(int i = 0; i < playerIndex; i++) {
                if(isBulletColliding(players[i], bullet)) {
                    collisions.put(bullet, players[i]);
                    continue bulletLoop;
                }
            }
            for(int i = playerIndex+1; i < numPlayers; i++) {
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
