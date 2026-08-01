import java.awt.image.BufferedImage;

// Enemies are stationary and shoot projectiles
public class Enemy {
    // Stats
    public int[] pos; // First value is x and second value is y
    public int[] size; // First value is width second value is height
    public int[] spawnPos; // A spawn position is necessary since the Enemy is only instantiated once, and it scrolls with the level
    public Projectile[] projectiles; // Array containing projectiles the Enemy can shoot
    public int firingSpeed; // How many frames between shots
    public int delay; // Counter for how many frames before the Enemy can shoot again
    public int maxHp;
    public int hp;
    public boolean dead;

    // Sprites
    public BufferedImage[] sprites; // Current set of animations. Changes between referencing three different sets of animations
    public BufferedImage[] idleAnimation;
    public BufferedImage[] damageAnimation;
    public BufferedImage[] deathAnimation;
    public int animationCounter; // A way of keeping track of if animations are finished
    public int spriteIndex;
    private int frameCounter;
    public int offset; // How much the hit box is offset from the sprites

    public Enemy(int x, int y, BufferedImage[] _idleAnimation, BufferedImage[] _deathAnimation, BufferedImage[] _damageAnimation, int _offset, int _hp, int _firingSpeed, Projectile[] _projectiles){
        pos = new int[]{x, y};
        spawnPos = new int[]{x, y};
        size = new int[]{_idleAnimation[spriteIndex].getWidth(), _idleAnimation[spriteIndex].getHeight()};
        offset = _offset;
        firingSpeed = _firingSpeed;
        delay = firingSpeed;
        idleAnimation = _idleAnimation;
        deathAnimation = _deathAnimation;
        damageAnimation = _damageAnimation;
        sprites = idleAnimation;
        projectiles = _projectiles;
        dead = false;
        maxHp = _hp;
        hp = _hp;
    }

    // Called every frame by the Scene that contains the Enemy
    public void update(){
        // Animation
        frameCounter++;
        if (animationCounter == 0) sprites = idleAnimation; // If the current animation is completed and is not the idle animation, begin idling
        if (frameCounter == 10){
            if (sprites != idleAnimation) animationCounter--; // Keeps track of when current animation completes
            spriteIndex++;
            spriteIndex %= sprites.length;
            frameCounter = 0;
        }

        // Death and autoscroll
        if (hp == 0){
            die();
        }
        pos[0] -= Main.scrollSpeed;

        // Projectiles
        delay--; // Time between shots decreases by one every frame
        for (Projectile projectile : projectiles){
            // Updates all active projectiles
            if (projectile.active) projectile.update();
            // Shoots inactive projectiles if delay is completed
            else if (!dead && delay <= 0 && pos[0] < 800 - size[0]) { // Only shoots if on screen
                fire(projectile);
                delay = firingSpeed; // Reset delay
            }
        }

        // Check collision with banana
        if (Main.banana.active && !dead && sprites == idleAnimation && checkCollisions(Main.banana.pos[0], Main.banana.pos[1], Main.banana.size[0], Main.banana.size[1])){
            hp--;
            spriteIndex = 0;
            animationCounter = damageAnimation.length;
            sprites = damageAnimation;
            Main.banana.active = false;
        }
    }

    public void die(){
        dead = true;
        for (Projectile projectile : projectiles){
            projectile.active = false;
        }
        Main.time -= 50;
    }

    // Called when player spawns or respawns
    public void reset(){
        dead = false;
        sprites = idleAnimation;
        spriteIndex = 0;
        hp = maxHp;
        pos[0] = spawnPos[0];
        pos[1] = spawnPos[1];
        for (Projectile projectile : projectiles) {
            projectile.active = false;
        }
    }

    public void fire(Projectile projectile) {
        // Sets projectile to active and centers it on the Enemy
        projectile.active = true;
        projectile.pos = new int[]{pos[0] + (size[0] - projectile.size[0]) / 2, pos[1] + (size[1] - projectile.size[1]) / 2};
    }

    public boolean checkCollisions(int x, int y, int width, int height) {
        return  pos[0] - offset + sprites[spriteIndex].getWidth() > x &&
                pos[0] + offset < x + width &&
                pos[1] - offset + sprites[spriteIndex].getHeight() > y &&
                pos[1] + offset < y + height;
    }
}
