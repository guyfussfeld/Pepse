package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.util.Random;

/**
 * Represents a leaf GameObject in the game world that exhibits various animations.
 */
public class Leaf extends GameObject {

    private static final String LEAF_TAG = "leaf";
    private static final float START_SHAKE_ANGLE = -7f;
    private static final float END_SHAKE_ANGLE = 7f;
    private static final float SCALE_FACTOR = 1.2f;
    private static final float SHAKING_DURATION = 2f;
    private static final float ANGLES_TO_ROTATE = 90f;
    private static final float ROTATION_DURATION = 2f;

    /**
     * Constructs a Leaf object with specified position, dimensions, and renderable component.
     *
     * @param topLeftCorner       The top-left corner position of the leaf.
     * @param dimensions          The dimensions (size) of the leaf.
     * @param rectangleRenderable The RectangleRenderable component for rendering the leaf.
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, RectangleRenderable rectangleRenderable) {
        super(topLeftCorner, dimensions, rectangleRenderable);
        setTag(LEAF_TAG);

        Random rand = new Random();

        // Schedule a task to shake the leaves
        new ScheduledTask(
                this,
                rand.nextFloat() * SHAKING_DURATION,    // Random delay before starting
                false,                                          // Do not repeat
                this::shakeLeaves                               // Callback to start shaking animation
        );

    }

    /**
     * Initiates the shaking animation for the leaf.
     */
    public void shakeLeaves() {
        // Transition for angles
        new Transition<>(
                this,
                angle -> this.renderer().setRenderableAngle(angle),
                START_SHAKE_ANGLE,                             // Start angle
                END_SHAKE_ANGLE,                               // End angle
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                SHAKING_DURATION,                              // Duration in seconds
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null                                           // No callback
        );

        // Transition for dimensions
        new Transition<>(
                this,
                this::setDimensions,
                Vector2.ONES.mult(Tree.LEAF_SIZE),             // Start dimensions
                this.getDimensions().mult(SCALE_FACTOR),       // End dimensions (scaled)
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                SHAKING_DURATION,                              // Duration in seconds
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null                                           // No callback
        );
    }

    /**
     * Initiates the rotation animation for the leaf, rotate the leaf realtive to its current angle.
     */
    public void rotateLeaves() {
        // Get the current angle of the leaf
        float currentAngle = this.renderer().getRenderableAngle();

        new Transition<>(
                this,
                angle -> this.renderer().setRenderableAngle(angle),
                currentAngle,                                 // Start angle
                currentAngle + ANGLES_TO_ROTATE,              // End angle
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                ROTATION_DURATION,                            // Duration in seconds
                Transition.TransitionType.TRANSITION_ONCE,
                null                                          // No callback
        );
    }

    /**
     * Check if the leaf should collide with another GameObject.
     *
     * @param other The other GameObject to check for collision.
     * @return False, as leaves should not collide with other GameObjects.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return false;
    }
}
