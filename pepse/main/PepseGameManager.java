package pepse.main;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.ui.EnergyUI;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;

/**
 * Main game manager class for PEPSE game.
 * Manages initialization, update, and interaction of game objects and components.
 */
public class PepseGameManager extends GameManager {

    private static final int SEED = 666;
    private static final float DAY_CYCLE = 30;
    private static final float WINDOW_FACTOR = 2 / 3f;

    private int[] currentRenderedRange;
    private Terrain terrain;
    private Flora flora;
    private Avatar avatar;

    /**
     * Entry point for the PEPSE game.
     *
     * @param args The command line arguments. Unused.
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * Updates the game state based on elapsed time.
     *
     * @param deltaTime The time elapsed since the last update in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateTerrainAndFlora();
    }

    /**
     * Updates the terrain and flora based on avatar movement.
     * Adjusts the rendered range and manages addition/removal of terrain and flora elements.
     */
    private void updateTerrainAndFlora() {
        int right = terrain.getRightColumn();
        int left = terrain.getLeftColumn();

        // Adjust terrain and flora based on avatar's movement
        if (avatarIsRightToCenter()) {

            flora.removeTreeInColumn(left, gameObjects()::removeGameObject, avatar);
            terrain.removeColumn(left, gameObjects()::removeGameObject);

            terrain.addColumn(right + Block.BLOCK_SIZE, gameObjects()::addGameObject);
            flora.addTreeInColumn(right + Block.BLOCK_SIZE, gameObjects()::addGameObject, avatar);

            currentRenderedRange[0] += Block.BLOCK_SIZE;
            currentRenderedRange[1] += Block.BLOCK_SIZE;

        } else if (avatarIsLeftToCenter()) {

            flora.removeTreeInColumn(right, gameObjects()::removeGameObject, avatar);
            terrain.removeColumn(right, gameObjects()::removeGameObject);

            terrain.addColumn(left - Block.BLOCK_SIZE, gameObjects()::addGameObject);
            flora.addTreeInColumn(left - Block.BLOCK_SIZE, gameObjects()::addGameObject, avatar);

            currentRenderedRange[0] -= Block.BLOCK_SIZE;
            currentRenderedRange[1] -= Block.BLOCK_SIZE;
        }
    }

    /**
     * Checks if the avatar is right of the center of the rendered range.
     *
     * @return True if the avatar is right of the center, false otherwise.
     */
    private boolean avatarIsRightToCenter() {
        return avatar.getCenter().x() > centerZoneToRight();
    }

    /**
     * Checks if the avatar is left of the center of the rendered range.
     *
     * @return True if the avatar is left of the center, false otherwise.
     */
    private boolean avatarIsLeftToCenter() {
        return avatar.getCenter().x() < centerZoneToLeft();
    }

    /**
     * Retrieves the x-coordinate of the center zone to the right of the current rendered range.
     *
     * @return The x-coordinate of the center zone to the right.
     */
    private float centerZoneToRight() {
        return (float) ((currentRenderedRange[0] + currentRenderedRange[1]) / 2.0) + Block.BLOCK_SIZE;
    }

    /**
     * Retrieves the x-coordinate of the center zone to the left of the current rendered range.
     *
     * @return The x-coordinate of the center zone to the left.
     */
    private float centerZoneToLeft() {
        return (float) ((currentRenderedRange[0] + currentRenderedRange[1]) / 2.0) - Block.BLOCK_SIZE;
    }

    /**
     * Initializes the game by setting up game objects, terrain, flora, avatar, UI, and camera.
     *
     * @param imageReader      The image reader for loading game images.
     * @param soundReader      The sound reader for loading game sounds.
     * @param inputListener    The user input listener for handling player input.
     * @param windowController The window controller for managing game window operations.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        int windowWidth = (int) windowController.getWindowDimensions().x();

        /* Initialize the current rendered range, centered at the origin, initially 4/3 times the window width
        to allow for smooth rendering of terrain and flora, without abrupt changes in the rendered range */
        this.currentRenderedRange = new int[]{(int) (-windowWidth * WINDOW_FACTOR),
                (int) (windowWidth * WINDOW_FACTOR)};

        // Initialize background elements
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        GameObject bg = Background.create(windowController.getWindowDimensions(), imageReader);
        gameObjects().addGameObject(bg, Layer.BACKGROUND);


        GameObject sun = Sun.create(windowController.getWindowDimensions(), DAY_CYCLE);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);

        // Add night effect
        GameObject night = Night.create(windowController.getWindowDimensions(), DAY_CYCLE);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        // Initialize terrain
        terrain = new Terrain(windowController.getWindowDimensions(), SEED);
        terrain.addInRange(currentRenderedRange[0], currentRenderedRange[1], gameObjects()::addGameObject);

        // Initialize avatar and camera
        Vector2 startingAvatarPos = new Vector2(0, terrain.groundHeightAt(0) - Avatar.AVATAR_HEIGHT);
        avatar = new Avatar(startingAvatarPos, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));

        // Initialize flora
        flora = new Flora(terrain::groundHeightAt, SEED);
        flora.addTreesInRange(currentRenderedRange[0], currentRenderedRange[1],
                gameObjects()::addGameObject, avatar);

        // Add UI components
        EnergyUI energyUI = new EnergyUI(avatar::getEnergy);
        gameObjects().addGameObject(energyUI, Layer.UI);
    }
}