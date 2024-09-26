package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a background image in the game world.
 */
public class Background {

    private static final String BG_PATH = "assets/bg.png";
    private static final String BG_TAG = "background";

    /**
     * Creates a GameObject representing the background image.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param imageReader      The ImageReader instance to read the background image.
     * @return A GameObject instance representing the background.
     */
    public static GameObject create(Vector2 windowDimensions, ImageReader imageReader) {
        // Read the background image
        Renderable bgImage = imageReader.readImage(BG_PATH, true);

        // Create the background GameObject
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, bgImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        background.setTag(BG_TAG);

        return background;
    }
}
