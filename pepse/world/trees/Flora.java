package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The Flora class manages the creation and removal of trees within a specified range.
 * It uses a function to determine ground height and a seed for random generation.
 */
public class Flora {

    private static final float TREES_DENSITY = 0.1f; // Density of trees in the environment
    private final Function<Float, Float> getGroundHeightAt; // Func to get ground height at a given x coord
    private final TreeMap<Integer, Tree> mappedTrees = new TreeMap<>(); // Map to store trees by their x coord
    private final int seed; // Seed for random generation

    /**
     * Constructs a Flora object.
     *
     * @param getGroundHeightAt A function that returns ground height at a given x coordinate.
     * @param seed              The seed for random tree generation.
     */
    public Flora(Function<Float, Float> getGroundHeightAt, int seed) {
        this.getGroundHeightAt = getGroundHeightAt;
        this.seed = seed;
    }

    /**
     * Creates trees within a specified range of x coordinates.
     *
     * @param minX The minimum x coordinate.
     * @param maxX The maximum x coordinate.
     * @return A list of newly created trees.
     */
    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> newTrees = new ArrayList<>();

        int normalizedMinX = normalizeCoordinate(minX);
        int normalizedMaxX = normalizeCoordinate(maxX) + Block.BLOCK_SIZE;

        // Generate trees within the specified normalized range
        for (int x = normalizedMinX; x < normalizedMaxX; x += Block.BLOCK_SIZE) {
            Random random = new Random(Objects.hash(x, seed));
            if (random.nextFloat() < TREES_DENSITY) {
                Tree tree = new Tree(new Vector2(x, getGroundHeightAt.apply((float) x)), random);
                newTrees.add(tree);
                mappedTrees.put(x, tree);
            }
        }
        return newTrees;
    }

    /**
     * Removes a tree at a specific x coordinate.
     *
     * @param x            The x coordinate of the column to remove.
     * @param removeObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                     to which the object should be removed from in the GameObjectCollection.
     * @param avatar       The avatar object to remove as a listener from the tree.
     */
    public void removeTreeInColumn(int x, BiConsumer<GameObject, Integer> removeObject, Avatar avatar) {
        Tree tree = mappedTrees.remove(x);
        if (tree != null) {
            tree.removeTree(removeObject);
            avatar.removeListener(tree);
        }
    }

    /**
     * Adds a tree at a specific x coordinate.
     *
     * @param x         The x coordinate of the column to add.
     * @param addObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                  to which the object should be added in the GameObjectCollection.
     * @param avatar    The avatar object to add as a listener to the tree.
     */
    public void addTreeInColumn(int x, BiConsumer<GameObject, Integer> addObject, Avatar avatar) {
        addTreesInRange(x, x, addObject, avatar);
    }

    /**
     * Adds trees within a specified range of x coordinates.
     *
     * @param minX      The minimum x coordinate.
     * @param maxX      The maximum x coordinate.
     * @param addObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                  to which the object should be added in the GameObjectCollection.
     * @param avatar    The avatar object to add as a listener to the trees.
     */
    public void addTreesInRange(int minX, int maxX, BiConsumer<GameObject, Integer> addObject,
                                Avatar avatar) {
        for (Tree tree : createInRange(minX, maxX)) {
            tree.addTree(addObject);
            avatar.addListener(tree);
        }
    }

    /**
     * Normalizes the x coordinate to the nearest block size multiple.
     *
     * @param x The x coordinate to normalize.
     * @return The normalized x coordinate.
     */
    private int normalizeCoordinate(int x) {
        return (int) (Math.floor((double) x / Block.BLOCK_SIZE) * Block.BLOCK_SIZE);
    }
}
