import javax.sound.sampled.*;
import java.io.*;

public class Sound implements LineListener {
    boolean resetAudio = false;
    AudioInputStream audioInputStream;
    Clip[] clips = new Clip[7];
    File[] files = new File[7];

    // Load music files on boot
    public Sound() {
        files[0] = new File("Assets/Music/menu.wav");
        files[1] = new File("Assets/Music/level1.wav");
        files[2] = new File("Assets/Music/level2.wav");
        files[3] = new File("Assets/Music/shoot.wav");
        files[4] = new File("Assets/Music/hurt.wav");
        files[5] = new File("Assets/Music/lose.wav");
        files[6] = new File("Assets/Music/win.wav");
        // Store clips in an array
        try {
            for (int i = 0; i < 7; i++){
                audioInputStream = AudioSystem.getAudioInputStream(files[i]);
                clips[i] = AudioSystem.getClip();
                clips[i].open(audioInputStream);
            }
        } catch (Exception ignored){}
    }

    // Required method for implementation; does nothing
    public void update(LineEvent event) {}

    // Start selected clip
    public void play(int index, boolean loop) {
        clips[index].start();
        if (clips[index].getFramePosition() != 0) clips[index].setFramePosition(0);
        if (loop) clips[index].loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Stops all clips and resets their playback to the beginning of the clip
    public void stop(){
        for (Clip clip : clips){
            clip.stop();
            clip.setFramePosition(0);
        }
    }
}
