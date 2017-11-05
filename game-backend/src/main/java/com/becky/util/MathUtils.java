package com.becky.util;

import com.becky.world.entity.GameEntity;

public class MathUtils {
    public static float distance(final GameEntity first, final GameEntity second) {
        return MathUtils.distance(first.getXPosition(), first.getYPosition(), second.getXPosition(), second.getYPosition());
    }

    public static float distance(final float x1, final float y1, final float x2, final float y2) {
        return (float)Math.sqrt((x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1));
    }

    public static float getAngleBetweenEntities(final GameEntity first, final GameEntity second) {
        final float deltaX = first.getXPosition() - second.getXPosition();
        final float deltaY = first.getYPosition() - second.getYPosition();
        return (float)StrictMath.atan2(deltaY, deltaX);
    }

    public static float normalizeAngle(final float angle) {
        if(angle < 0) {
            return angle + 2*(float)Math.PI;
        }
        else {
            return angle - 2*(float)Math.PI;
        }
    }
}
