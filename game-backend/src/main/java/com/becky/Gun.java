package com.becky;

/**
 * A gun fires bullets by creating them. Is simple.
 */
public interface Gun {
    /**
     * Gets the player this gun belongs to
     * @return
     */
    Player getWeilder();

    /**
     * Instructs the gun to fire. This means adding one or more rounds to the list of bullets owned
     * by the player.
     */
    void fire();
}
