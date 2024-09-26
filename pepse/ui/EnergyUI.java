package pepse.ui;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.util.PepseConstants;

import java.awt.*;
import java.util.function.Supplier;


/**
 * The EnergyUI class represents a user interface component that displays the player's current energy level.
 * It extends the GameObject class and uses a TextRenderable to display the energy percentage.
 */
public class EnergyUI extends GameObject {

    private static final String ENERGY_UI_TAG = "EnergyUI"; // Tag for the energy UI
    private static final String PERCENTAGE = "%"; // Percentage symbol for energy display

    private static final Vector2 ENERGY_UI_POSITION = Vector2.ONES.mult(10); // Position of the energy UI
    private static final Vector2 ENERGY_UI_SIZE = Vector2.ONES.mult(50); // Size of the energy UI

    private static final int LOW_ENERGY_THRESHOLD = 20; // Threshold for low energy
    private static final int MEDIUM_ENERGY_THRESHOLD = 50; // Threshold for medium energy
    private static final Color LOW_ENERGY_COLOR = Color.RED; // Color for low energy
    private static final Color MEDIUM_ENERGY_COLOR = Color.ORANGE; // Color for medium energy
    private static final Color HIGH_ENERGY_COLOR = Color.BLACK; // Color for high energy

    private final Supplier<Float> getEnergy;
    private final TextRenderable textRenderable;

    /**
     * Constructs an EnergyUI object with a supplier for the current energy level.
     *
     * @param energySupplier A Supplier<Float> that provides the current energy level.
     */
    public EnergyUI(Supplier<Float> energySupplier) {
        super(ENERGY_UI_POSITION, ENERGY_UI_SIZE, null);
        this.getEnergy = energySupplier;

        textRenderable = new TextRenderable("");
        renderer().setRenderable(textRenderable);

        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        setTag(ENERGY_UI_TAG);
    }

    /**
     * Update the energy UI
     *
     * @param deltaTime the time passed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateEnergyUI();
    }

    /**
     * Update the text display to show the current energy percentage.
     */
    private void updateEnergyUI() {
        // Get the current energy and update the text
        int currentEnergy = Math.max(PepseConstants.MIN_ENERGY, Math.min(PepseConstants.MAX_ENERGY,
                getEnergy.get().intValue())); // Ensure energy is between 0 and 100
        textRenderable.setString(currentEnergy + PERCENTAGE);
        recolorText(currentEnergy);
    }

    /**
     * Recolor the text based on the current energy level.
     *
     * @param currentEnergy The current energy level as an integer.
     */
    private void recolorText(int currentEnergy) {
        // Recolor the text based on the current energy
        if (currentEnergy < LOW_ENERGY_THRESHOLD) {
            textRenderable.setColor(LOW_ENERGY_COLOR);
        } else if (currentEnergy < MEDIUM_ENERGY_THRESHOLD) {
            textRenderable.setColor(MEDIUM_ENERGY_COLOR);
        } else {
            textRenderable.setColor(HIGH_ENERGY_COLOR);
        }
    }
}
