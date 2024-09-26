package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the player character in the game world, managing animations, movement, and energy.
 */
public class Avatar extends GameObject {

    /**
     * The height of the avatar game object
     */
    public static final int AVATAR_HEIGHT = 60;
    /**
     * The tag of the avatar game object
     */
    public static final String AVATAR_TAG = "avatar";
    private static final int AVATAR_WIDTH = 50;

    // Movement constants
    private static final float VELOCITY_X = 400;
    private static final float GRAVITY = 600;
    private static final float VELOCITY_Y = -(GRAVITY + 50);

    // Animation sequences
    private static final String[] IDLE_FRAMES_SEQUENCE = {
            "assets/idle_0.png", "assets/idle_1.png",
            "assets/idle_2.png", "assets/idle_3.png"
    };

    private static final String[] JUMP_FRAMES_SEQUENCE = {
            "assets/jump_0.png", "assets/jump_1.png",
            "assets/jump_2.png", "assets/jump_3.png"
    };

    private static final String[] RUN_FRAMES_SEQUENCE = {
            "assets/run_0.png", "assets/run_1.png",
            "assets/run_2.png", "assets/run_3.png",
            "assets/run_4.png", "assets/run_5.png"
    };

    // Energy consumption rates
    private static final float IDLE_ENERGY = 1f;
    private static final float RUN_ENERGY = 0.5f;
    private static final float JUMP_ENERGY = 10f;
    private static final float MAX_ENERGY = 100f;
    private static final float TIME_BETWEEN_FRAMES = 0.1f;

    // Instance variables - the input listener, animations, and listeners
    private final UserInputListener inputListener;
    private final AnimationRenderable idleAnimation;
    private final AnimationRenderable jumpAnimation;
    private final AnimationRenderable runAnimation;

    private final List<AvatarListener> listeners = new ArrayList<>();
    private float energy = MAX_ENERGY;

    /**
     * Constructs an Avatar object at the specified position with input listener and image reader.
     *
     * @param topLeftCorner The top-left corner position of the avatar
     * @param inputListener The input listener to handle user input
     * @param imageReader   The image reader to load avatar animations
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, Vector2.of(AVATAR_WIDTH, AVATAR_HEIGHT), null);
        // Set the avatar's physics properties
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);

        this.inputListener = inputListener;

        // Load animations
        idleAnimation = new AnimationRenderable(IDLE_FRAMES_SEQUENCE, imageReader, true, TIME_BETWEEN_FRAMES);
        jumpAnimation = new AnimationRenderable(JUMP_FRAMES_SEQUENCE, imageReader, true, TIME_BETWEEN_FRAMES);
        runAnimation = new AnimationRenderable(RUN_FRAMES_SEQUENCE, imageReader, true, TIME_BETWEEN_FRAMES);

        // Set the initial animation
        renderer().setRenderable(idleAnimation);
        setTag(AVATAR_TAG);
    }

    /**
     * Updates the avatar's state based on user input and game logic.
     *
     * @param deltaTime The time elapsed since the last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateAvatarState();
    }

    /**
     * Retrieves the current energy level of the avatar.
     *
     * @return The current energy level
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Increases the avatar's energy by the specified amount, up to the maximum energy limit.
     *
     * @param energy The amount of energy to add
     */
    public void addEnergy(float energy) {
        this.energy = Math.min(this.energy + energy, MAX_ENERGY);
    }

    /**
     * Adds a listener to receive notifications when the avatar performs a jump.
     *
     * @param listener The listener to add
     */
    public void addListener(AvatarListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener that no longer wants to receive notifications.
     *
     * @param listener The listener to remove
     */
    public void removeListener(AvatarListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sets the current animation renderable of the avatar.
     *
     * @param animation The animation to set
     */
    private void setAnimation(AnimationRenderable animation, Boolean isFlippedHorizontally) {
        if (renderer().getRenderable() != animation) {
            renderer().setRenderable(animation);
        }
        if (isFlippedHorizontally != null && (renderer().isFlippedHorizontally() != isFlippedHorizontally)) {
            renderer().setIsFlippedHorizontally(isFlippedHorizontally);
        }
    }

    /**
     * Updates the avatar's state based on user input and game logic.
     */
    private void updateAvatarState() {
        checkRun();
        checkJump();
        checkIdle();
    }


    /**
     * Checks if the avatar should perform a running action based on user input and energy level.
     */
    private void checkRun() {
        if (!enoughEnergyToRun()) {
            if (getVelocity().x() != 0) {
                transform().setVelocityX(0);
            }
            return;
        }

        boolean movingLeft = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean movingRight = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);

        if (movingLeft && !movingRight) {
            transform().setVelocityX(-VELOCITY_X);
            decreaseEnergy(RUN_ENERGY);
            setAnimation(runAnimation, true);

        } else if (movingRight && !movingLeft) {
            transform().setVelocityX(VELOCITY_X);
            decreaseEnergy(RUN_ENERGY);
            setAnimation(runAnimation, false);

        } else {
            transform().setVelocityX(0);
        }
    }


    /**
     * Checks if the avatar should perform a jumping action based on user input and energy level.
     */
    private void checkJump() {
        if (isOnGround() && inputListener.isKeyPressed(KeyEvent.VK_SPACE) && energy >= JUMP_ENERGY) {
            transform().setVelocityY(VELOCITY_Y);
            decreaseEnergy(JUMP_ENERGY);
            notifyJump();
            setAnimation(jumpAnimation, null);
        }
    }

    /**
     * Checks if the avatar should enter idle state based on user input and current movement.
     */
    private void checkIdle() {
        if (isOnGround() && isNotMoving()) {
            addEnergy(IDLE_ENERGY);
            setAnimation(idleAnimation, null);
        }
    }

    /**
     * Decreases the avatar's energy by the specified amount, down to a minimum of 0.
     *
     * @param energy The amount of energy to subtract
     */
    private void decreaseEnergy(float energy) {
        this.energy = Math.max(this.energy - energy, 0);
    }

    /**
     * Checks if the avatar has enough energy to run.
     *
     * @return True if the avatar has enough energy to run, false otherwise
     */
    private boolean enoughEnergyToRun() {
        return energy >= RUN_ENERGY;
    }

    /**
     * Checks if the avatar is currently on the ground.
     *
     * @return True if the avatar is on the ground, false otherwise
     */
    private boolean isOnGround() {
        return getVelocity().y() == 0;
    }

    /**
     * Checks if the avatar is currently not moving horizontally.
     *
     * @return True if the avatar is not moving horizontally, false otherwise
     */
    private boolean isNotMoving() {
        return getVelocity().x() == 0;
    }

    /**
     * Notifies all listeners that the avatar has performed a jump action.
     */
    private void notifyJump() {
        for (AvatarListener listener : listeners) {
            listener.onJump();
        }
    }
}
