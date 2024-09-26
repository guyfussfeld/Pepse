package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The Night class represents the nighttime effect in the game.
 * It creates a GameObject that simulates the night by gradually changing its opacity.
 */
public class Night {

    private static final String NIGHT_TAG = "night"; // Tag for the night GameObject
    private static final float MIDDAY_OPACITY = 0f; // Opacity value at midday (no night effect)
    private static final float MIDNIGHT_OPACITY = 0.5f; // Opacity value at midnight (full night effect)
    private static final float HALF_CYCLE_FACTOR = 0.5f; // Factor to represent half of the day cycle

    /**
     * Creates a night effect GameObject.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The length of a full day-night cycle in the game.
     * @return The GameObject representing the night effect.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        // Create the night GameObject with a black rectangle renderable
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);

        // Create a transition to change the opacity of the night GameObject
        new Transition<Float>(
                night, // The game object being changed
                night.renderer()::setOpaqueness, // The method to call to change opacity
                MIDDAY_OPACITY, // Initial opacity value (midday)
                MIDNIGHT_OPACITY, // Final opacity value (midnight)
                Transition.CUBIC_INTERPOLATOR_FLOAT, // Use a cubic interpolator for smooth transition
                cycleLength * HALF_CYCLE_FACTOR, // Transition fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Transition back and forth
                null); // No additional action upon reaching the final value

        return night;
    }
}
