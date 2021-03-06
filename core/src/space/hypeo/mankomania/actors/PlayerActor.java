package space.hypeo.mankomania.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import space.hypeo.mankomania.StageFactory;
import space.hypeo.mankomania.StageManager;

/**
 * Class that represents a Player.
 */
public class PlayerActor extends Image {
    private static final float PLAYER_SCALE = 20f;
    protected FieldActor currentField;

    // Current player state.
    private int balance;
    private boolean isLocal;
    private String playerID;

    // For demostration purposes only. TODO: Remove!
    private float timeElapsed = 0;
    private Random die = new Random();

    // For dice feature
    private static final float EARTH_GRAVITY = 9.81f;
    private int moveFields;
    private static final float GRAVITY_FORCE_THRESHOLD = 1.9f;
    final StageManager manager;
    final Viewport viewport;

    /**
     * Creates a new instance of a Class that implementaion for a Player.
     *
     * @param playerID     The player's ID (useful for communications)
     * @param balance      The player's current balance (starting balance)
     * @param isLocal      Defines whether this player is the local one (i.e the one controlled with this device)
     * @param currentField Defines the players current position.
     */
    public PlayerActor(String playerID, int balance, boolean isLocal, FieldActor currentField, final Viewport viewport, final StageManager stageManager) {
        super(new Texture("tile.png"));
        this.currentField = currentField;
        this.setBounds(currentField.getX(), currentField.getY(), PLAYER_SCALE, PLAYER_SCALE);
        this.isLocal = isLocal;
        this.balance = balance;

        this.manager = stageManager;
        this.viewport = viewport;
    }

    /**
     * Defines whether this player is the local one (i.e the one controlled with this device)
     *
     * @return
     */
    public boolean isLocal() {
        return this.isLocal;
    }

    @Override
    public void act(float deltaTime)
    {
        float xValue;
        float yValue;
        float zValue;
        float gForce;

        timeElapsed += deltaTime;
        if (timeElapsed >= 0.18f) {
            timeElapsed = 0;

            xValue = Gdx.input.getAccelerometerX() / EARTH_GRAVITY;
            yValue = Gdx.input.getAccelerometerY() / EARTH_GRAVITY;
            zValue = Gdx.input.getAccelerometerZ() / EARTH_GRAVITY;

            gForce = (float) Math.sqrt(xValue * xValue + yValue * yValue + zValue * zValue);

            if (gForce > GRAVITY_FORCE_THRESHOLD) {

                // TODO: check if it is the players turn, then move
                moveFields = die.nextInt(6) + 1;
                this.move(moveFields);
                manager.push(StageFactory.getDiceResult(viewport, manager, moveFields));

                // TODO: maybe cheat function here (for example: if other player is playing roulette)
            }
        }

    }

    /**
     * Moves the player a specific amount of steps on the board.
     * @param steps The amount of steps to move.
     */
    public void move(int steps) {
        currentField = currentField.getFollowingField(steps);
        if (this.isLocal())
            currentField.trigger(this);
        updateBounds();
    }

    /**
     * Updates the object bounds to the current field.
     */
    private void updateBounds() {
        this.setBounds(currentField.getX(), currentField.getY(), PLAYER_SCALE, PLAYER_SCALE);
        System.out.println(balance);
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
