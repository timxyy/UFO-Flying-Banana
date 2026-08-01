import java.awt.image.BufferedImage;

// Spawned and updated by the player in Main
public class Banana {
    // Stats
    public int[] pos; // First value is x and second value is y
    public int[] size; // First value is width second value is height
    public int speed = 10;
    public double theta; // Angle between banana and mouse click; used to calculate movement
    public boolean active;

    // Sprites
    public BufferedImage[] sprites;
    public int spriteIndex;
    private int frameCounter;

    public Banana(int x, int y, BufferedImage[] _sprites){
        pos = new int[]{x, y};
        size = new int[]{_sprites[spriteIndex].getWidth(), _sprites[spriteIndex].getHeight()};
        sprites = _sprites;
    }

    // Actions taken every frame; updated by player
    public void update(){
        // Animation
        frameCounter++;
        if (frameCounter == 2){
            spriteIndex++;
            spriteIndex %= sprites.length;
            frameCounter = 0;
        }

        // Movement
        pos[0] += (int) (speed * Math.cos(theta));
        pos[1] += (int) (speed * Math.sin(theta));

        // Disappear when offscreen
        if (pos[0] > Main.panelWidth + 100 || pos[0] < -100 - sprites[spriteIndex].getWidth() || pos[1] > Main.panelHeight + 100 || pos[1] < -100 - sprites[spriteIndex].getHeight()) active = false;
    }

    // When fired, set position to player position and set target position to mouse position
    public void fire(int mouseX, int mouseY, int playerX, int playerY){
        pos = new int[]{playerX, playerY};
        active = true;
        theta = Math.atan2(mouseY - playerY, mouseX - playerX);
    }
}
