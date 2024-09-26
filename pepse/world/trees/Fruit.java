package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.util.PepseConstants;
import pepse.world.Avatar;

/**
 * Represents a fruit GameObject in the game world that grants energy to the Avatar upon collision.
 * The fruit respawns after a specified delay once consumed.
 */
public class Fruit extends GameObject {

    private static final String FRUIT_TAG = "fruit"; // Tag identifying the fruit GameObject
    private static final float ENERGY_BONUS = 10f; // Energy bonus granted to Avatar upon collision
    private static final float RESPAWN_DELAY_SECONDS = PepseConstants.DAY_CYCLE; // Respawn delay in seconds
    private final OvalRenderable ovalRenderable; // Renderable component for the fruit
    private Boolean existsFlag = true; // Flag indicating if the fruit exists in the game world

    /**
     * Constructs a Fruit object with specified position, dimensions, and renderable component.
     *
     * @param topLeftCorner  The top-left corner position of the fruit.
     * @param dimensions     The dimensions (size) of the fruit.
     * @param ovalRenderable The OvalRenderable component for rendering the fruit.
     */
    public Fruit(Vector2 topLeftCorner, Vector2 dimensions, OvalRenderable ovalRenderable) {
        super(topLeftCorner, dimensions, ovalRenderable);
        this.ovalRenderable = ovalRenderable;
        setTag(FRUIT_TAG);
    }

    /**
     * Checks if the fruit exists in the game world.
     *
     * @return True if the fruit exists, false otherwise.
     */
    public Boolean isExists() {
        return existsFlag;
    }

    /**
     * Handles collision events with other GameObjects.
     *
     * @param other     The other GameObject involved in the collision.
     * @param collision Details of the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (!isAvatar(other) || !existsFlag) {
            return;
        }

        Avatar avatar = (Avatar) other;
        avatar.addEnergy(ENERGY_BONUS);
        respawnFruit();
    }

    /**
     * Determines if the fruit should collide with another GameObject.
     * Fruit should collide only with Avatars.
     *
     * @param other The other GameObject to check collision with.
     * @return True if the fruit should collide with the other GameObject, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return existsFlag && isAvatar(other);
    }

    /**
     * Respawns the fruit after a specified delay.
     * Sets exists to false, removes renderable, and schedules respawn after delay.
     */
    private void respawnFruit() {
        existsFlag = false;
        renderer().setRenderable(null);  // Remove renderable
        new ScheduledTask(this, RESPAWN_DELAY_SECONDS, false, // Respawn after 30 seconds
                () -> {
                    existsFlag = true;
                    renderer().setRenderable(ovalRenderable); // Restore renderable
                });
    }

    /**
     * Checks if the GameObject is an Avatar.
     *
     * @param gameObject The GameObject to check.
     * @return True if the GameObject is an Avatar, false otherwise.
     */
    private boolean isAvatar(GameObject gameObject) {
        return gameObject.getTag().equals(Avatar.AVATAR_TAG);
    }
}

