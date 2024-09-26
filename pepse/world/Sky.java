package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sky GameObject in the game world.
 */
public class Sky {

    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY_TAG = "sky";

    /**
     * Creates a GameObject representing the sky with a basic color.
     *
     * @param windowDimensions The dimensions of the game window.
     * @return A GameObject instance representing the sky.
     */
    public static GameObject create(Vector2 windowDimensions) {
        // Create the sky GameObject
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));

        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY_TAG);

        return sky;
    }
}
