package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a block GameObject in the game world.
 */
public class Block extends GameObject {

    /**
     * The size of the block.
     */
    public static final int BLOCK_SIZE = 30;

    private static final String GROUND_TAG = "ground";

    /**
     * Constructs a Block object with the specified parameters.
     *
     * @param topLeftCorner The top-left corner vector of the block.
     * @param renderable    The renderable component of the block.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(BLOCK_SIZE), renderable);

        // Prevent the game object from intersecting with other game objects from any direction
        physics().preventIntersectionsFromDirection(Vector2.ZERO);

        // Make the block immovable
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

        setTag(GROUND_TAG);
    }
}