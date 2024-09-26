package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.util.PepseConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;


/**
 * Represents the terrain in the game world, managing the generation and rendering of the ground blocks.
 */
public class Terrain {

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private static final int FACTOR_MULT = 7;

    private final float groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;
    private final TreeMap<Integer, List<Block>> mappedGroundBlocks = new TreeMap<>();
    private int leftColumn;
    private int rightColumn;

    /**
     * Constructs a Terrain object with the specified window dimensions and seed.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param seed             The seed value for the noise generator.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = windowDimensions.y() * PepseConstants.SKY_TERRAIN_RATIO;
        this.noiseGenerator = new NoiseGenerator(seed, (int) groundHeightAtX0);
    }

    /**
     * Calculates the ground height at a specific x-coordinate using noise generation.
     *
     * @param x The x-coordinate to calculate the ground height.
     * @return The height of the ground at the specified x-coordinate.
     */
    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, Block.BLOCK_SIZE * FACTOR_MULT);
        return groundHeightAtX0 + noise;
    }

    /**
     * Creates ground blocks within the specified x-coordinate range.
     *
     * @param minX The minimum x-coordinate (inclusive).
     * @param maxX The maximum x-coordinate (exclusive).
     * @return A list of newly created ground blocks.
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> newGroundBlocks = new ArrayList<>();
        RectangleRenderable rectangleRenderable;

        // Adjust leftColumn and rightColumn bounds
        int normilizedMinX = (int) (Math.floor((double) minX / Block.BLOCK_SIZE) * Block.BLOCK_SIZE);
        int normilizedMaxX =
                (int) (Math.floor((double) maxX / Block.BLOCK_SIZE) * Block.BLOCK_SIZE) + Block.BLOCK_SIZE;

        // Adjust leftColumn and rightColumn bounds
        if (leftColumn > normilizedMinX) {
            leftColumn = normilizedMinX;
        }
        if (rightColumn < normilizedMaxX - Block.BLOCK_SIZE) {
            rightColumn = normilizedMaxX - Block.BLOCK_SIZE;
        }

        // Create ground blocks in the given range
        for (int x = normilizedMinX; x < normilizedMaxX; x += Block.BLOCK_SIZE) {

            // Calculate the ground block values at the given position
            float y = (float) Math.floor(groundHeightAt(x) / Block.BLOCK_SIZE) * Block.BLOCK_SIZE;

            List<Block> blockColumn = new ArrayList<>();
            for (int i = 0; i < TERRAIN_DEPTH; i++) {
                rectangleRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));

                // Create a block at the given position
                Block groundBlock = new Block(new Vector2(x, y), rectangleRenderable);
                newGroundBlocks.add(groundBlock);
                blockColumn.add(groundBlock);

                // Move the y position for the next block
                y += Block.BLOCK_SIZE;
            }
            mappedGroundBlocks.put(x, blockColumn);
        }

        return newGroundBlocks;
    }

    /**
     * Removes a column of ground blocks at the specified x-coordinate from the game object's collection.
     *
     * @param x            The x-coordinate of the column to remove.
     * @param removeObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                     to which the object should be removed from in the GameObjectCollection.
     */
    public void removeColumn(int x, BiConsumer<GameObject, Integer> removeObject) {
        boolean firstBlock = true;
        for (Block block : mappedGroundBlocks.remove(x)) {
            if (firstBlock) {
                removeObject.accept(block, Layer.STATIC_OBJECTS);
                firstBlock = false;
            } else {
                removeObject.accept(block, Layer.BACKGROUND);
            }
        }

        // Adjust leftColumn and rightColumn bounds if necessary
        if (x == leftColumn) {
            leftColumn += Block.BLOCK_SIZE;
        } else if (x == rightColumn) {
            rightColumn -= Block.BLOCK_SIZE;
        }
    }

    /**
     * Adds a column of ground blocks at the specified x-coordinate to the game object's collection.
     *
     * @param x         The x-coordinate of the column to add.
     * @param addObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                  to which the object should be added in the GameObjectCollection.
     */
    public void addColumn(int x, BiConsumer<GameObject, Integer> addObject) {
        addInRange(x, x, addObject);
    }

    /**
     * Adds a range of ground blocks to the game object's collection.
     *
     * @param minX      The minimum x-coordinate (inclusive).
     * @param maxX      The maximum x-coordinate (exclusive).
     * @param addObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                  to which the object should be added in the GameObjectCollection.
     */
    public void addInRange(int minX, int maxX, BiConsumer<GameObject, Integer> addObject) {
        boolean firstBlock = true;
        int counter = 0;
        for (Block block : createInRange(minX, maxX)) {
            if (firstBlock) {
                addObject.accept(block, Layer.STATIC_OBJECTS);
                firstBlock = false;
            } else {
                addObject.accept(block, Layer.BACKGROUND);
            }
            counter++;
            if (counter == TERRAIN_DEPTH) {
                firstBlock = true;
                counter = 0;
            }

        }
    }

    /**
     * Retrieves the leftmost x-coordinate of the terrain.
     *
     * @return The leftmost x-coordinate of the terrain.
     */
    public int getLeftColumn() {
        return leftColumn;
    }

    /**
     * Retrieves the rightmost x-coordinate of the terrain.
     *
     * @return The rightmost x-coordinate of the terrain.
     */
    public int getRightColumn() {
        return rightColumn;
    }

}
