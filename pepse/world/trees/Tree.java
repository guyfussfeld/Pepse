package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.AvatarListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Represents a tree in the game world, consisting of a trunk, leaves, and fruits.
 * Provides methods to add and remove the tree from a GameObjectCollection, and handles animations
 * when the Avatar jumps near the tree.
 */
public class Tree implements AvatarListener {

    /**
     * The size of each leaf
     */
    public static final int LEAF_SIZE = 30;
    // Trunk constants
    private static final Color TRUNK_COLOR = new Color(100, 50, 20); // Color of the tree trunk
    private static final int MIN_TRUNK_HEIGHT = 120; // Minimum height of the tree trunk
    private static final int ADDED_TRUNK_HEIGHT = 150; // Additional height for the tree trunk
    private static final int TRUNK_WIDTH = 30; // Width of the tree trunk
    // Leaves constants
    private static final Color LEAF_COLOR = new Color(50, 200, 30); // Color of the tree leaves
    private static final float LEAVES_DENSITY = 0.5f; // Density of leaves on the tree

    // Fruits constants
    private static final Color INITIAL_FRUIT_COLOR = new Color(220, 20, 60); // Initial Color of fruit
    private static final List<Color> FRUIT_COLORS = List.of(
            new Color(220, 20, 60), // Red
            new Color(255, 165, 0), // Orange
            new Color(255, 215, 0), // Yellow
            new Color(75, 0, 130),  // Purple
            new Color(0, 0, 255),   // Blue
            new Color(41, 253, 219),// Cyan
            new Color(182, 9, 182)  // Pink
    );
    private static final int FRUIT_SIZE = (int) (LEAF_SIZE * 0.8); // Size of each fruit
    private static final float FRUITS_DENSITY = 0.02f; // Density of fruits on the tree

    private final List<Leaf> leaves = new ArrayList<>(); // List to hold all leaves of the tree
    private final List<Fruit> fruits = new ArrayList<>(); // List to hold all fruits of the tree
    private final Random rand; // Random object for generating random values
    private Trunk trunk; // The trunk of the tree

    /**
     * Constructs a Tree object with a random position and initializes its trunk, leaves, and fruits.
     *
     * @param bottomLeftCorner The bottom-left corner position where the tree's trunk starts.
     * @param random           The Random object used for generating random values.
     */
    public Tree(Vector2 bottomLeftCorner, Random random) {
        this.rand = random;
        createTrunk(bottomLeftCorner);
        createLeavesAndFruits(createTopTreeBox());
    }

    /**
     * Creates the trunk of the tree with a random height and places it at the specified position.
     *
     * @param bottomLeftCorner The bottom-left corner position where the trunk starts.
     */
    private void createTrunk(Vector2 bottomLeftCorner) {
        int trunkHeight = rand.nextInt(ADDED_TRUNK_HEIGHT) + MIN_TRUNK_HEIGHT;
        trunk = new Trunk(bottomLeftCorner.subtract(new Vector2(0, trunkHeight)),
                new Vector2(TRUNK_WIDTH, trunkHeight),
                new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR)));
    }

    /**
     * Calculates the bounding box for the top area of the tree where leaves and fruits can be created.
     *
     * @return An array containing the coordinates [YstartPoint, XstartPoint, YendPoint, XendPoint] of the
     * top tree area.
     */
    private float[] createTopTreeBox() {
        float leavesCubeSize = trunk.getCenter().y() - trunk.getTopOfTheTree().y();
        int NormilizedleavesCubeSize = Math.round(leavesCubeSize / (float) LEAF_SIZE) * LEAF_SIZE;
        float YstartPoint = trunk.getTopOfTheTree().y() - NormilizedleavesCubeSize;
        float XstartPoint = trunk.getTopOfTheTree().x() - NormilizedleavesCubeSize;
        float YendPoint = trunk.getTopOfTheTree().y() + NormilizedleavesCubeSize;
        float XendPoint = trunk.getTopOfTheTree().x() + NormilizedleavesCubeSize;
        return new float[]{YstartPoint, XstartPoint, YendPoint, XendPoint};
    }

    /**
     * Creates leaves and fruits within the calculated top tree area based on defined densities.
     *
     * @param topTreeBox The bounding box coordinates of the top tree area.
     */
    private void createLeavesAndFruits(float[] topTreeBox) {
        for (float i = topTreeBox[0]; i < topTreeBox[2]; i += LEAF_SIZE) {
            for (float j = topTreeBox[1]; j < topTreeBox[3]; j += LEAF_SIZE) {
                if (rand.nextFloat() < LEAVES_DENSITY) {
                    leaves.add(new Leaf(new Vector2(j, i), Vector2.ONES.mult(LEAF_SIZE),
                            new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR))));
                }
                if (rand.nextFloat() < FRUITS_DENSITY) {
                    fruits.add(new Fruit(new Vector2(j, i), Vector2.ONES.mult(FRUIT_SIZE),
                            new OvalRenderable(ColorSupplier.approximateColor(INITIAL_FRUIT_COLOR))));
                }
            }
        }
    }

    /**
     * Adds the tree, its trunk, leaves, and fruits to the specified GameObjectCollection.
     *
     * @param addObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                  to which the object should be added in the GameObjectCollection.
     */
    public void addTree(BiConsumer<GameObject, Integer> addObject) {
        addObject.accept(trunk, Layer.STATIC_OBJECTS);
        for (Leaf leaf : leaves) {
            addObject.accept(leaf, Layer.STATIC_OBJECTS);
        }
        for (Fruit fruit : fruits) {
            addObject.accept(fruit, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Removes the tree, its trunk, leaves, and fruits from the specified GameObjectCollection.
     *
     * @param removeObject A BiConsumer that accepts a GameObject and an Integer representing the layer
     *                     to which the object should be removed from in the GameObjectCollection.
     */
    public void removeTree(BiConsumer<GameObject, Integer> removeObject) {
        removeObject.accept(trunk, Layer.STATIC_OBJECTS);
        for (Leaf leaf : leaves) {
            removeObject.accept(leaf, Layer.STATIC_OBJECTS);
        }
        for (Fruit fruit : fruits) {
            removeObject.accept(fruit, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Changes the color of the tree trunk to a random color.
     */
    private void changeTrunkColor() {
        trunk.renderer().setRenderable(new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR)));
    }

    /**
     * Initiates rotation animation for all leaves of the tree.
     */
    private void rotateLeaves() {
        for (Leaf leaf : leaves) {
            leaf.rotateLeaves();
        }
    }

    /**
     * Alternates the color of all fruits of the tree.
     */
    private void changeFruitColor() {
        for (Fruit fruit : fruits) {
            if (fruit.isExists()) {
                fruit.renderer().setRenderable(
                        new OvalRenderable(ColorSupplier.approximateColor(getRandomFruitColor())));
            }
        }
    }

    /**
     * Returns a random fruit color from the predefined list.
     *
     * @return A random color from the FRUIT_COLORS list.
     */
    private Color getRandomFruitColor() {
        return FRUIT_COLORS.get(rand.nextInt(FRUIT_COLORS.size()));
    }

    /**
     * Implements the AvatarListener interface method to handle actions when the Avatar jumps near the tree.
     * This method changes the trunk color, rotates leaves, and alternates fruit colors.
     */
    @Override
    public void onJump() {
        changeTrunkColor();
        rotateLeaves();
        changeFruitColor();
    }

}

