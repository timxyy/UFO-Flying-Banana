import java.awt.image.BufferedImage;

// All projectiles are contained in an array in the Enemy class
// There are a set number of projectiles in a level, and projectile activation is controlled by the Enemy class
public class Projectile {
    // Stats
    public int[] pos; // First value is x and second value is y
    public int[] size; // First value is width second value is height
    public boolean active; // Projectiles does not do damage or get drawn if inactive
    public int direction; // Either 0 or 1; 0 shoots means projectile moves horizontally and 1 means it moves vertically
    public int speed; // Pixels moved per frame; negative goes left or up, positive goes right or down
    public boolean deflect = true; // Determines if the projectile makes the banana disappear on contact; only boss projectiles do this

    // Sprites
    public BufferedImage[] sprites;
    public int spriteIndex;
    private int frameCounter;

    public Projectile(BufferedImage[] _sprites, int _direction, int _speed){
        pos = new int[2];
        size = new int[]{_sprites[spriteIndex].getWidth(), _sprites[spriteIndex].getWidth()};
        sprites = _sprites;
        direction = _direction;
        speed = _speed;
    }

    // Actions taken every frame
    public void update(){
        // Animations
        frameCounter++;
        if (frameCounter == 4){
            spriteIndex++;
            spriteIndex %= sprites.length;
            frameCounter = 0;
        }

        // Movement
        pos[direction] += speed;
        pos[0] -= Main.scrollSpeed; // Projectiles autoscroll with the screen

        // Collisions
        // Collision with banana
        if (pos[0] > Main.panelWidth + 100 || pos[0] < -100 - sprites[spriteIndex].getWidth() || pos[1] > Main.panelHeight + 100 || pos[1] < -100 - sprites[spriteIndex].getHeight()) active = false;
        if (Main.banana.active && checkCollisions(Main.banana.pos[0], Main.banana.pos[1], Main.banana.size[0], Main.banana.size[1])) {
            active = false;
            if (!deflect) Main.banana.active = false;
        }
        // Collision with player
        if (Main.iFrames == 0 && checkCollisions(Main.playerX, (int) Main.playerY, Main.ufo[Main.frameIndex].getWidth(), Main.ufo[Main.frameIndex].getHeight())){
            Main.hurt(1, 300, 50);
            active = false;
        }
    }

    public boolean checkCollisions(int x, int y, int width, int height) {
        return  pos[0] + sprites[spriteIndex].getWidth() > x &&
                pos[0] < x + width &&
                pos[1] + sprites[spriteIndex].getHeight() > y &&
                pos[1] < y + height;
    }
}
