import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Main extends JPanel implements MouseListener, KeyListener, Runnable {
    // Screen dimensions
    public static int panelWidth;
    public static int panelHeight;

    // Game States
    // 0 <- Home Screen
    // 1 <- Tutorial
    // 2 <- Credits
    // 3 <- High Scores
    // 4 <- Level 1
    // 5 <- Level 1 Win Screen
    // 6 <- Level 2
    // 7 <- Level 2 Win Screen
    // 8 <- Level 1 Game Over
    // 9 <- Level 2 Game Over
    public static int gameState;
    public static Scene[] scenes;
    public static int scrollSpeed = 3; // Level scrolling speed
    public static Banana banana; // Player Projectile

    // Sprites for every object in the game
    public static BufferedImage[] backgrounds = new BufferedImage[9];
    public static BufferedImage[] bananaProjectile = new BufferedImage[7];
    public static BufferedImage[] bossStarProjectile = new BufferedImage[3];
    public static BufferedImage[] fishBoss = new BufferedImage[4];
    public static BufferedImage[] fishBossDeath = new BufferedImage[5];
    public static BufferedImage[] fishBossDamage = new BufferedImage[2];
    public static BufferedImage[] mosquito = new BufferedImage[5];
    public static BufferedImage[] mosquitoBoss = new BufferedImage[4];
    public static BufferedImage[] mosquitoBossDeath = new BufferedImage[5];
    public static BufferedImage[] mosquitoBossDamage = new BufferedImage[2];
    public static BufferedImage[] octopus = new BufferedImage[4];
    public static BufferedImage[] starProjectile = new BufferedImage[3];
    public static BufferedImage[] ufo = new BufferedImage[5];

    // Player stats
    public static int hp;
    public static int iFrames; // Invincibility frames
    public static int playerX; // Locked to a certain value; included for clarity
    public static double playerY; // Only movable coordinate; tracked as a double for precision
    public static int hitRadius; // Radius of hit box; necessary to prevent an inaccurate square hit box

    // Physics
    public static double playerAccel;
    public static double playerChange;
    public static double maxSpeed;
    public static double accelSpeed;
    public static double decaySpeed;
    public static double gravity;

    // Animation
    public static int frameIndex;
    public static int frameCounter;

    // Time variable to keep track of scores
    public static int time;
    public static int[] highScores = new int[6];

    // Sound player object
    public static Sound soundPlayer;

    public static void main(String[] args) throws IOException{
        // Creating Game Window
        JFrame frame = new JFrame("Game Name");
        Main panel = new Main();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load High Scores
        Scanner inputFile = new Scanner(new File("Assets/highscores.txt"));
        for (int i = 0; i < 6; i++){
            highScores[i] = inputFile.nextInt();
        }
        inputFile.close();

        // Create sound player
        try{
            soundPlayer = new Sound();
            soundPlayer.play(0, true);
        } catch (Exception ignored){}
    }

    // Timer
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(10); //100FPS
            } catch (Exception e) {
                System.out.println("Timer Error");
            }
        }
    }

    // Setting Game/Window/Image/Timer Data
    public Main() {
        // Image Importation
        try {
            // Backgrounds
            for (int i = 0; i < 9; i++) {
                backgrounds[i] = ImageIO.read(new File("Assets/backgrounds/gs" + i + ".png"));
            }
            // Banana
            for (int i = 0; i < 7; i++) {
                bananaProjectile[i] = ImageIO.read(new File("Assets/banana/banana" + i + ".png"));
            }
            // Boss Projectiles
            for (int i = 0; i < 3; i++) {
                bossStarProjectile[i] = ImageIO.read(new File("Assets/star_boss/star_boss" + i + ".png"));
            }
            // Level 2 Boss
            for (int i = 0; i < 4; i++) {
                fishBoss[i] = ImageIO.read(new File("Assets/fish_boss/fish_boss" + i + ".png"));
            }
            for (int i = 0; i < 5; i++) {
                fishBossDeath[i] = ImageIO.read(new File("Assets/fish_boss/death/fish_boss_death" + i + ".png"));
            }
            for (int i = 0; i < 2; i++) {
                fishBossDamage[i] = ImageIO.read(new File("Assets/fish_boss/damage/fish_boss_damage" + i + ".png"));
            }
            // Level 1 Regular Enemy
            for (int i = 0; i < 5; i++) {
                mosquito[i] = ImageIO.read(new File("Assets/mosquito/mosquito" + i + ".png"));
            }
            // Level 1 Boss
            for (int i = 0; i < 4; i++) {
                mosquitoBoss[i] = ImageIO.read(new File("Assets/mosquito_boss/mosquito_boss" + i + ".png"));
            }
            for (int i = 0; i < 5; i++) {
                mosquitoBossDeath[i] = ImageIO.read(new File("Assets/mosquito_boss/death/mosquito_boss_death" + i + ".png"));
            }
            for (int i = 0; i < 2; i++) {
                mosquitoBossDamage[i] = ImageIO.read(new File("Assets/mosquito_boss/damage/mosquito_boss_damage" + i + ".png"));
            }
            // Level 2 Regular Enemy
            for (int i = 0; i < 4; i++) {
                octopus[i] = ImageIO.read(new File("Assets/octopus/octopus" + i + ".png"));
            }
            // Enemy Projectiles
            for (int i = 0; i < 3; i++) {
                starProjectile[i] = ImageIO.read(new File("Assets/star/star" + i + ".png"));
            }
            // Player
            for (int i = 0; i < 5; i++) {
                ufo[i] = ImageIO.read(new File("Assets/ufo/ufo" + i + ".png"));
            }
        } catch (Exception e) {
            System.out.println("Image Error");
        }

        // A collection of all the Scenes in the game; index of each Scene is the associated game state
        // Menu Scenes have only Buttons and a background while levels have a background, enemies, an obstacle colour but no buttons
        scenes = new Scene[]{
                new Scene(
                    backgrounds[0],
                    null,
                    new Button[]{
                        new Button(4, 340, 420, 120, 50), // Play
                        new Button(1, 195, 455, 120, 50), // How To
                        new Button(3, 340, 495, 120, 50), // High Scores
                        new Button(2, 485, 455, 120, 50)  // Credits
                    }, null
                ), // 0 <- Home Screen
                new Scene(
                    backgrounds[1],
                    null,
                    new Button[]{
                        new Button(0, 605, 510, 155, 65)
                    }, null
                ), // 1 <- Tutorial
                new Scene(
                    backgrounds[2],
                    null,
                    new Button[]{
                        new Button(0, 605, 510, 155, 65)
                    }, null
                ), // 2 <- Credits
                new Scene(
                    backgrounds[3],
                    null,
                    new Button[]{
                        new Button(0, 605, 510, 155, 65)
                    }, null
                ), // 3 <- High Scores
                new Scene(
                    backgrounds[4],
                    new Color(68, 103, 0),
                    null,
                    new Enemy[]{
                        new Boss(backgrounds[4].getWidth(), panelHeight / 2, mosquitoBoss, mosquitoBossDeath, mosquitoBossDamage, 25, 10, 100,
                            new Projectile[]{
                                new Projectile(bossStarProjectile, 0, -3),
                                new Projectile(bossStarProjectile, 0, -3),
                                new Projectile(bossStarProjectile, 0, -3),
                                new Projectile(bossStarProjectile, 0, -3),
                                new Projectile(bossStarProjectile, 0, -3)

                            }),
                            new Enemy(800, 540, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(1050, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(2000, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(2500, 540, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(2600, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(2700, 540, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(3000, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(3800, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(4000, 540, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(4800, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(5000, 540, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(5200, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(5400, 540, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(6300, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(7200, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(8600, 540, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(9700, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(11000, 0, mosquito, mosquito, mosquito, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    })
                        }
                ), // 4 <- Level 1
                new Scene(
                    backgrounds[5],
                    null,
                    new Button[]{
                        new Button(6, 550, 395, 155, 65),
                        new Button(0, 550, 480, 155, 65)
                    }, null
                ), // 5 <- Level 1 Win Screen
                new Scene(
                    backgrounds[6],
                    new Color(61, 129, 255),
                    null,
                    new Enemy[]{
                        new Boss(backgrounds[6].getWidth(), 0, fishBoss, fishBossDeath, fishBossDamage, 10, 10, 50,
                            new Projectile[]{
                                new Projectile(bossStarProjectile, 0, -1),
                                new Projectile(bossStarProjectile, 0, -2),
                                new Projectile(bossStarProjectile, 0, -3),
                                new Projectile(bossStarProjectile, 0, -4),
                                new Projectile(bossStarProjectile, 0, -5),
                                new Projectile(bossStarProjectile, 0, -6),
                                new Projectile(bossStarProjectile, 0, -7),
                                new Projectile(bossStarProjectile, 0, -8),
                                new Projectile(bossStarProjectile, 0, -9)
                            }),
                            new Enemy(900, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(950, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(1000, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(2200, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(2600, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(2700, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(3000, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(3250, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(3500, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(3800, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(4000, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(4200, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(4500, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(5000, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(5200, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(5700, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(6000, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(6100, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(6100, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(7100, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(8000, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(8000, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(8200, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(8500, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(9100, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(9350, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(9400, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(9500, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                            new Enemy(10000, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(10400, 0, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, 5)
                                    }),
                            new Enemy(10600, 537, octopus, octopus, octopus, 0, 1, 25,
                                    new Projectile[]{
                                            new Projectile(starProjectile, 1, -5)
                                    }),
                    }

                ), // 6 <- Level 2
                new Scene(
                    backgrounds[7],
                    null,
                    new Button[]{
                        new Button(0, 550, 480, 155, 65)
                    }, null
                ), // 7 <- Level 2 Win Screen
                new Scene(
                    backgrounds[8],
                    null,
                    new Button[]{
                        new Button(4, 550, 395, 155, 65),
                        new Button(0, 550, 480, 155, 65)
                    }, null
                ), // 8 <- Level 1 Game Over
                new Scene(
                    backgrounds[8],
                    null,
                    new Button[]{
                        new Button(6, 550, 395, 155, 65),
                        new Button(0, 550, 480, 155, 65)
                    }, null
                ) // 9 <- Level 2 Game Over
        };

        // Setting player stats and physics
        playerX = 75;
        playerY = panelHeight / 2d;
        banana = new Banana(playerX, (int) playerY, bananaProjectile);
        hitRadius = 25;
        panelWidth = 800;
        panelHeight = 600;
        playerAccel = 0;
        playerChange = 0;
        maxSpeed = 5;
        accelSpeed = 0.3;
        gravity = 0.25;
        decaySpeed = 0.25;
        hp = 10;
        // Game screen stats
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        addKeyListener(this);
        addMouseListener(this);
        this.setFocusable(true);
        Thread thread = new Thread(this);
        thread.start();
    }

    // Draw Screen
    public void paintComponent(Graphics g) {
        // Drawing
        super.paintComponent(g); // Clear screen
        // Draw background
        g.drawImage(scenes[gameState].background, scenes[gameState].backgroundOffset, 0, null);
        if (scenes[gameState].enemies != null) { // If the scene is a playable level
            for (Enemy enemy : scenes[gameState].enemies) {
                // Draw all living Enemies
                if (!enemy.dead) g.drawImage(enemy.sprites[enemy.spriteIndex], enemy.pos[0], enemy.pos[1], null);
                // Draw all active Projectiles
                for (Projectile projectile : enemy.projectiles) {
                    if (projectile.active) g.drawImage(projectile.sprites[projectile.spriteIndex], projectile.pos[0], projectile.pos[1], null);
                }
            }
            // Update the banana if active
            if (banana.active){
                banana.update();
                // Draw the banana
                g.drawImage(bananaProjectile[banana.spriteIndex], banana.pos[0], banana.pos[1], null);
            }
            // Draw the player
            g.drawImage(ufo[frameIndex], playerX, (int) playerY, null);
            // Draw the player and boss health bars
            g.setColor(new Color(9, 9, 9));
            g.fillRect(25, 25, 174, 34);
            g.fillRect(scenes[gameState].enemies[0].pos[0] + 100 - scenes[gameState].enemies[0].size[0], 25, 174, 34);
            g.setColor(new Color(55, 155, 255));
            for (int i = 0; i < hp; i++){
                g.fillRect(29 + i * 17, 29, 12, 26);
            }
            g.setColor(new Color(255, 55, 55));
            for (int i = 0; i < scenes[gameState].enemies[0].hp; i++){
                g.fillRect(scenes[gameState].enemies[0].pos[0] + 104 - scenes[gameState].enemies[0].size[0] + i * 17, 29, 12, 26);
            }

            // Updates
            // Update the player
            playerUpdate();
            // Update all enemies in the level
            scenes[gameState].update();
            // Update the time/score
            time++;
            // Display the time
            g.setColor(new Color(255, 255, 255));
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
            g.drawString(String.format("%02d:%02d", time / 6000,time / 100 % 60), 370, 35);
        }
        // Draw scores if scene is high scores
        else if (gameState == 2) {
            g.setColor(new Color(255, 255, 255));
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
            for (int i = 0; i < 6; i++){
                if (highScores[i] != -1) g.drawString(String.format("%02d:%02d", highScores[i] / 60, highScores[i] % 60), 120 + i / 3 * 350, 300 + i % 3 * 100);
            }
        }
    }

    // Player updates every frame
    public void playerUpdate() {
        // Animation
        frameCounter++;
        if (frameCounter == 8) {
            frameIndex++;
            frameIndex %= ufo.length;
            frameCounter = 0;
        }

        // Physics
        playerChange += playerAccel;
        if (Math.abs(playerChange) >= maxSpeed) playerChange *= maxSpeed / Math.abs(playerChange);
        if (playerAccel == 0) playerChange *= decaySpeed;
        playerY += playerChange;

        if (iFrames > 0) iFrames--; // Decrease iFrames
        // Die if offscreen
        if (playerY > panelHeight + 100 || playerY < -100 - ufo[frameIndex].getHeight()) die();

        // Collision detection with obstacles by colour
        // Find the center of the circular player hit box
        int center = playerX + ufo[frameIndex].getWidth() / 2;
        // Loop through the x values of the diameter of the hit box
        for (int i = center - hitRadius; i < center + hitRadius; i++) {
            // For every x value, find the corresponding y value (x^2 + y^2 = r^2) so that the loop traces out a circle
            // Then, for every coordinate, check that coordinate of the background image (factoring in scrolling)
            // If said coordinate is a certain colour, the player must be touching an obstacle
            try {
                if (iFrames == 0
                        && (backgrounds[gameState].getRGB(i - scenes[gameState].backgroundOffset, (int) (Math.sqrt((hitRadius + i - center) * (hitRadius - i + center)) + playerY + ufo[frameIndex].getHeight() / 2d)) == scenes[gameState].obstacleColour.getRGB()
                        || backgrounds[gameState].getRGB(i - scenes[gameState].backgroundOffset, (int) (-Math.sqrt((hitRadius + i - center) * (hitRadius - i + center)) + playerY + ufo[frameIndex].getHeight() / 2d)) == scenes[gameState].obstacleColour.getRGB())) {
                        hurt(1, 500, 50);
                    return;
                }
            } catch (Exception ignored){}
        }

        if (hp <= 0){
            die();
        }
    }

    // Called when player takes damage
    public static void hurt(int damage, int timePenalty, int frames){
        // Play damage sound
        try {
            soundPlayer.play(4, false);
        } catch (Exception ignored){}
        // If the player has not yet moved, trigger gravity
        if (playerAccel == 0) playerAccel = gravity;
        // Decrease hp, increase time (score penalty), and give iFrames
        hp -= damage;
        time += timePenalty;
        iFrames = frames;
    }

    public void die() {
        // Stops sound and switches to game over screen
        soundPlayer.stop();
        gameState = gameState == 4 ? 8 : gameState == 6 ? 9 : gameState;
        time = 0;
        try {
            soundPlayer.play(5, false);
        } catch (Exception ignored){}
    }

    public void spawn() {
        // Reset time, hp, position, reset Enemies, reset physics, deactivate banana
        time = 0;
        hp = 10;
        playerY = panelHeight / 2d;
        playerAccel = 0;
        playerChange = 0;
        scenes[gameState].backgroundOffset = 0;
        for (Enemy enemy : scenes[gameState].enemies){
            enemy.reset();
        }
        banana.active = false;
    }

    public static void levelFinish() throws IOException{
        // Update high scores and go to win screen
        overwriteTime(time / 100, gameState / 2 - 1);
        gameState++;
        try {
            soundPlayer.stop();
            soundPlayer.play(6, false);
        } catch (Exception ignored){}
    }

    // Update high scores
    public static void overwriteTime(int time, int level) throws IOException{
        // Variables
        PrintWriter outputFile = new PrintWriter(new FileWriter("Assets/highscores.txt"));
        int n = (level - 1) * 3;

        for (int i = 0; i < 3; i++){
            if (time < highScores[n + i] || highScores[n + i] == -1){
                for (int j = 2 - i; j > 0; j--){
                    highScores[n + j] = highScores[n + j - 1];
                }
                highScores[n + i] = time;
                break;
            }
        }

        for (int i = 0; i < 6; i++){
            outputFile.println(highScores[i]);
        }
        outputFile.close();
    }

    // Mouse and Keyboard Methods
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        switch (gameState) {
            // Menu Buttons
            case 0, 1, 2, 3, 5, 7, 8, 9:
                for (Button button : scenes[gameState].buttons) {
                    // If a button is clicked
                    if (button.checkClick(e.getX(), e.getY()) && e.getButton() == 1){
                        gameState = button.destination; // Change game state
                        // If the destination is a playable level
                        if (scenes[gameState].enemies != null) {
                            spawn(); // Player Spawns into new level
                            try{
                                soundPlayer.stop();
                                soundPlayer.play(gameState / 2 - 1, true);
                                soundPlayer.resetAudio = true;
                            } catch (Exception ignored){}
                        } else if (soundPlayer.resetAudio){
                            try{
                                soundPlayer.stop();
                                soundPlayer.play(0, true);
                                soundPlayer.resetAudio = false;
                            } catch (Exception ignored){}
                        }
                    }
                }
                break;
            // Shoot banana in playable level
            case 4, 6:
                if (!banana.active){
                    banana.fire(e.getX(), e.getY(), playerX, (int) playerY);
                    try {
                        soundPlayer.play(3, false);
                    } catch (Exception ignored){}
                }
                if (playerAccel == 0) playerAccel = gravity;
                break;
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w':
                playerAccel = -accelSpeed;
                break;
            case 's':
                playerAccel = 2 * accelSpeed;
                break;
            case 'v':
                if (scenes[gameState].enemies != null) {
                    try {
                        levelFinish();
                    } catch (IOException ignored) {}
                }
                break;
        }
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w', 's':
                playerAccel = gravity;
                break;
        }
    }
}