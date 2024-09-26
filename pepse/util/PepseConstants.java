package pepse.util;

/**
 * A utility class to hold global constants for the Pepse game.
 */
public final class PepseConstants {

    /**
     * The duration of a full day-night cycle in seconds.
     */
    public static final float DAY_CYCLE = 30;
    /**
     * The ratio of sky height to terrain height.
     */
    public static final float SKY_TERRAIN_RATIO = (float) 2 / 3;
    /**
     * The minimum energy value for the avatar.
     */
    public static final int MIN_ENERGY = 0;
    /**
     * The maximum energy value for the avatar.
     */
    public static final int MAX_ENERGY = 100;

    // Prevent instantiation
    private PepseConstants() {
        throw new UnsupportedOperationException("PepseConstants class cannot be instantiated");
    }
}
