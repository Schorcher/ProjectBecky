package com.becky;

public class WorldBorder {
    private final float maxX;
    private final float maxY;
    private float currentX;
    private float currentY;
    private float expansionAmount;
    private long expansionTime;

    public WorldBorder(final float maxX, final float maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.currentX = maxX;
        this.currentY = maxY;
    }

    public void shrink(final float amount) {
        currentX -= amount;
        currentY -= amount;
        if(currentX < 1.0f) {
            currentX = 1.0f;
        }
        if(currentY < 1.0f) {
            currentY = 1.0f;
        }
    }

    public void expand(final float amount) {
        currentX += amount;
        currentY += amount;
        if(currentX > maxX) {
            currentX = maxX;
        }
        if(currentY > maxY) {
            currentY = maxY;
        }
    }

    public void shrinkOverTime(final float totalShrinkage, final long numMilliseconds) {
        expansionTime = numMilliseconds;
        expansionAmount = -totalShrinkage/1000.0f;
    }

    public void expandOverTime(final float totalExpansion, final long numMilliseconds) {
        expansionTime = numMilliseconds;
        expansionAmount = totalExpansion/1000.0f;
    }

    public void tick(long elapsedTime) {
        if(expansionTime <= 0) {
            return;
        }

        if(elapsedTime > expansionTime) {
            elapsedTime = expansionTime;
        }

        final float change = expansionAmount * elapsedTime;
        expansionTime -= elapsedTime;
        expand(change);
    }

    public void keepEntityInBorder(final GameEntity entity) {
        final float xPosition = entity.getXPosition();
        if(xPosition < 0.0f) {
            entity.setXPosition(1.0f);
            if(entity.getXAcceleration() < 0.0f) {
                entity.setXAcceleration(0.0f);
            }
            if(entity.getXVelocity() < 0.0f) {
                entity.setXVelocity(0.0f);
            }
        }
        else if(xPosition > currentX) {
            entity.setXPosition(currentX - 1.0f);
            if(entity.getXAcceleration() > 0.0f) {
                entity.setXAcceleration(0.0f);
            }
            if(entity.getXVelocity() > 0.0f) {
                entity.setXVelocity(0.0f);
            }
        }

        final float yPosition = entity.getYPosition();
        if(yPosition < 0) {
            entity.setYPosition(1.0f);
            if(entity.getYAcceleration() < 0.0f) {
                entity.setYAcceleration(0.0f);
            }
            if(entity.getYVelocity() < 0.0f) {
                entity.setYVelocity(0.0f);
            }
        }
        else if(yPosition > currentY) {
            entity.setYPosition(currentY - 1.0f);
            if(entity.getYAcceleration() > 0.0f) {
                entity.setYAcceleration(0.0f);
            }
            if(entity.getYVelocity() > 0.0f) {
                entity.setYVelocity(0.0f);
            }
        }
    }
}
