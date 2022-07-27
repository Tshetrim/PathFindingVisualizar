import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioWorker {

    private boolean soundOn;

    public AudioWorker() {
        this.soundOn = true;
    }

    public AudioWorker(boolean defaultSoundToggle) {
        this.soundOn = defaultSoundToggle;
    }

    public synchronized void playSound(final String url) {
        if (soundOn) {
            new Thread(new Runnable() {
                // The wrapper thread is unnecessary, unless it blocks on the
                // Clip finishing; see comments.
                public void run() {
                    try {
                        AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                                getClass().getResource(url));
                        Clip clip = AudioSystem.getClip();
                        clip.open(inputStream);
                        clip.start();
                        // System.out.println("played");
                    } catch (Exception e) {
                        // System.err.println("Error getting sound file " + e.getMessage());
                    }
                }
            }).start();
        }
    }

    public void playLowPop() {
        playSound("pop_low.wav");
    }

    public void playHighPop() {
        playSound("pop_low.wav");
    }

    public void setSoundOn() {
        soundOn = true;
    }

    public void setSoundOff() {
        soundOn = false;
    }
}
