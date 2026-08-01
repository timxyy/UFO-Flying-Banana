import java.awt.image.BufferedImage;
import java.awt.*;

// A Scene is assigned to each game state
// Each Scene contains all the Enemies and Buttons in a level
public class Scene {
    public BufferedImage background; // Background sprite
    public int backgroundOffset; // Variable to keep track of background scrolling
    public Color obstacleColour; // Color of obstacles in a level; used to determine player collision
    public Button[] buttons;
    public Enemy[] enemies;

    public Scene(BufferedImage _background, Color _obstacleColour, Button[] _buttons, Enemy[] _enemies) {
        background = _background;
        backgroundOffset = 0; // Allows for autoscroll; Usually 0 but continually decreases if the level is playable
        obstacleColour = _obstacleColour;
        buttons = _buttons;
        enemies = _enemies;
    }

    public void update(){
        // Levels with Enemies are playable levels and must scroll
        if (enemies != null) {
            // Updates all the enemies in a level
            for (Enemy enemy: enemies) {
                if (!enemy.dead) enemy.update();
            }
            // If the background has not reached the end of the level, scroll left
            if (background.getWidth() + backgroundOffset > 799 + Main.scrollSpeed) backgroundOffset -= Main.scrollSpeed;
        }
    }
}
