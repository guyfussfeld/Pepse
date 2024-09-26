package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.util.PepseConstants;

import java.awt.*;

/**
 * The Sun class represents the sun in the game.
 * It creates a GameObject that simulates the sun's movement across the sky in a day-night cycle.
 */
public class Sun {

    private static final String SUN_TAG = "sun"; // Tag for the sun GameObject
    private static final float HALF_CYCLE_FACTOR = 0.5f; // Factor to represent half of the day cycle
    private static final int SUN_SIZE = 100; // Size of the sun
    private static final float INITIAL_DEGREE = 0f; // Initial degree for the sun's rotation
    private static final float FINAL_DEGREE = 360f; // Final degree for the sun's rotation

    /**
     * Creates a sun effect GameObject.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The length of a full day-night cycle in the game.
     * @return The GameObject representing the sun effect.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        // Create the sun GameObject with a yellow oval renderable
        GameObject sun = new GameObject(Vector2.ZERO, Vector2.of(SUN_SIZE, SUN_SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        // Calculate the ground height at x=0 based on the sky-terrain ratio
        float groundHeightAtX0 = windowDimensions.y() * PepseConstants.SKY_TERRAIN_RATIO;

        // Sun initial position and cycle center position for the Transition Callback method
        Vector2 initialSunCenter = new Vector2(windowDimensions.mult(HALF_CYCLE_FACTOR));
        Vector2 cycleCenter = new Vector2(windowDimensions.x() * HALF_CYCLE_FACTOR, groundHeightAtX0);

        // Create a transition to change the sun's position in a circular path
        new Transition<Float>(
                sun, // The game object being changed
                (Float angle) -> sun.setCenter(
                        initialSunCenter.subtract(cycleCenter)
                                .rotated(angle)
                                .add(cycleCenter)), // The method to call to change position
                INITIAL_DEGREE, // Initial degree value (start of the day)
                FINAL_DEGREE, // Final degree value (end of the day)
                Transition.LINEAR_INTERPOLATOR_FLOAT, // Use a linear interpolator for smooth transition
                cycleLength, // Transition fully over a day
                Transition.TransitionType.TRANSITION_LOOP, // Loop the transition to simulate continuous
                // day-night cycle
                null); // No additional action upon reaching the final value

        return sun;
    }
}
