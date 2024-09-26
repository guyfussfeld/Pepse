package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

/**
 * Represents the trunk of a tree GameObject in the game world.
 */
public class Trunk extends GameObject {

    private static final String TRUNK_TAG = "trunk";
    private static final float HALF_FACTOR = 0.5f;

    private final Vector2 topOfTheTree;

    /**
     * Constructs a Trunk object with the specified position, dimensions, and renderable component.
     *
     * @param topLeftCorner       The top-left corner position of the trunk.
     * @param dimensions          The dimensions (width and height) of the trunk.
     * @param rectangleRenderable The RectangleRenderable component for rendering the trunk.
     */
    public Trunk(Vector2 topLeftCorner, Vector2 dimensions, RectangleRenderable rectangleRenderable) {
        super(topLeftCorner, dimensions, rectangleRenderable);
        this.topOfTheTree = topLeftCorner.add(new Vector2(dimensions.x() * HALF_FACTOR, 0));

        // Prevent the game object from intersecting with other game objects from any direction
        physics().preventIntersectionsFromDirection(Vector2.ZERO);

        // Make the block immovable
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

        setTag(TRUNK_TAG);
    }

    /**
     * Retrieves the top position of the trunk (top center).
     *
     * @return The top position of the trunk.
     */
    public Vector2 getTopOfTheTree() {
        return topOfTheTree;
    }

}