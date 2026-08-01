import java.awt.image.BufferedImage;
import java.util.Random;

// Boss class inherits most Enemy traits but gain randomized movement and have death animations
public class Boss extends Enemy {
    // Variable for random movement
    int destination = 100 + new Random().nextInt(450 - size[1]);

    // Inheriting Enemy attributes
    public Boss(int x, int y, BufferedImage[] _idleAnimation, BufferedImage[] _deathAnimation, BufferedImage[] _damageAnimation, int _offset, int _hp, int _firingSpeed, Projectile[] _projectiles) {
        super(x, y, _idleAnimation, _deathAnimation, _damageAnimation, _offset, _hp, _firingSpeed, _projectiles);
    }

    public void update() {
        // Inherited Enemy behaviour
        super.update();

        // Death Animation
        // When hp reaches 0, instead of dying, change sprites to death animation and resets animationCounter
        if (hp == 0){
            dead = false;
            hp--;
            spriteIndex = 0;
            animationCounter = deathAnimation.length;
            sprites = deathAnimation;
        }
        // Once the death animation completes, the Boss dies and completes the current level
        if (animationCounter == 0 && sprites == deathAnimation){
            Main.time += 50;
            die();
            try {
                Main.levelFinish();
            } catch (Exception e){
                System.out.println("File Error");
            }
        }

        // Randomized movement
        // Bosses pick a random y-coordinate in a certain range and move towards it
        if (pos[1] != destination) {
            pos[1] += pos[1] > destination ? -1 : 1;
        } else {
            destination = 100 + new Random().nextInt(450 - size[1]); // When the boss reaches the destination a new destination is chosen
        }
        if (pos[0] < 800 - size[0]) pos[0] += Main.scrollSpeed; // Cancel autoscroll when on screen
    }

    public void fire(Projectile projectile) {
        super.fire(projectile);
        projectile.deflect = false; // Boss projectiles cause the banana to disappear on hit
    }
}
