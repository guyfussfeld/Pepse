package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The SunHalo class represents a visual halo effect around the sun in the game.
 * It creates a GameObject that follows the sun's position, providing a glowing halo.
 */
public class SunHalo {

    private static final float HALO_SIZE_FACTOR = 1.5f; // Halo size relative to the sun
    private static final Color BASE_HALO_COLOR = new Color(255, 255, 0, 20); // Base color of the halo
    private static final String SUN_HALO_TAG = "sunHalo"; // Tag for the sun halo GameObject

    /**
     * Creates a sun halo effect GameObject that follows the sun.
     *
     * @param sun The GameObject representing the sun.
     * @return The GameObject representing the sun halo effect.
     */
    public static GameObject create(GameObject sun) {
        // Calculate the halo size based on the sun's dimensions
        Vector2 haloSize = sun.getDimensions().mult(HALO_SIZE_FACTOR);
        // Create the sun halo GameObject with a translucent yellow oval renderable
        GameObject sunHalo = new GameObject(Vector2.ZERO, haloSize, new OvalRenderable(BASE_HALO_COLOR));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);

        // Add a component to update the sun halo position to match the sun position (every frame)
        sunHalo.addComponent((float deltaTime) -> sunHalo.setCenter(sun.getCenter()));

        return sunHalo;
    }
}
