package com.neondash.game.utilities;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.SoundPool;

import com.neondash.game.database.PreferencesManager;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SoundManager {

    private static SoundManager instance;
    private final Context context;
    private final PreferencesManager prefs;

    private SoundPool soundPool;
    private int soundJump = 0;
    private int soundDoubleJump = 0;
    private int soundSlide = 0;
    private int soundCoin = 0;
    private int soundPowerUp = 0;
    private int soundShieldBreak = 0;
    private int soundCollision = 0;
    private int soundClick = 0;

    // Static looping background music
    private AudioTrack musicTrack;
    private volatile boolean isMusicPlaying = false;

    private static final int SAMPLE_RATE = 22050;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = PreferencesManager.getInstance(context);

        initSoundPool();
        initMusicTrack();
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    private void initSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(attributes)
                .build();

        // Synthesize and load all 8 SFX on a background worker once
        new Thread(() -> {
            try {
                soundJump = loadWaveform("sfx_jump.wav", generateSweep(320.0f, 750.0f, 120, 0.75f));
                soundDoubleJump = loadWaveform("sfx_dbljump.wav", generateSweep(600.0f, 1200.0f, 140, 0.8f));
                soundSlide = loadWaveform("sfx_slide.wav", generateSweep(280.0f, 110.0f, 180, 0.6f));
                soundCoin = loadWaveform("sfx_coin.wav", generateCoinPing());
                soundPowerUp = loadWaveform("sfx_powerup.wav", generatePowerUpArpeggio());
                soundShieldBreak = loadWaveform("sfx_shield.wav", generateSweep(900.0f, 300.0f, 180, 0.85f));
                soundCollision = loadWaveform("sfx_crash.wav", generateCrash());
                soundClick = loadWaveform("sfx_click.wav", generateSweep(600.0f, 850.0f, 35, 0.5f));
            } catch (Exception ignored) {
            }
        }).start();
    }

    private int loadWaveform(String filename, short[] samples) {
        try {
            File file = new File(context.getCacheDir(), filename);
            writeWavFile(file, samples, SAMPLE_RATE);
            return soundPool.load(file.getAbsolutePath(), 1);
        } catch (Exception e) {
            return 0;
        }
    }

    private static void writeWavFile(File file, short[] samples, int sampleRate) throws IOException {
        int dataSize = samples.length * 2;
        int totalSize = 36 + dataSize;
        try (FileOutputStream fos = new FileOutputStream(file);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(fos))) {
            // RIFF header
            dos.writeBytes("RIFF");
            dos.writeInt(Integer.reverseBytes(totalSize));
            dos.writeBytes("WAVE");
            // fmt chunk
            dos.writeBytes("fmt ");
            dos.writeInt(Integer.reverseBytes(16));
            dos.writeShort(Short.reverseBytes((short) 1)); // PCM
            dos.writeShort(Short.reverseBytes((short) 1)); // Mono
            dos.writeInt(Integer.reverseBytes(sampleRate));
            dos.writeInt(Integer.reverseBytes(sampleRate * 2));
            dos.writeShort(Short.reverseBytes((short) 2));
            dos.writeShort(Short.reverseBytes((short) 16));
            // data chunk
            dos.writeBytes("data");
            dos.writeInt(Integer.reverseBytes(dataSize));
            for (short sample : samples) {
                dos.writeShort(Short.reverseBytes(sample));
            }
            dos.flush();
        }
    }

    // Tone generators
    private short[] generateSweep(float startFreq, float endFreq, int durationMs, float volume) {
        int numSamples = (SAMPLE_RATE * durationMs) / 1000;
        short[] buffer = new short[numSamples];
        double phase = 0.0;
        for (int i = 0; i < numSamples; i++) {
            float t = (float) i / numSamples;
            float freq = startFreq + (endFreq - startFreq) * t;
            phase += (2.0 * Math.PI * freq) / SAMPLE_RATE;
            float env = 1.0f - t;
            double sample = (Math.sin(phase) + 0.25 * Math.sin(2.0 * phase)) * env * volume;
            buffer[i] = (short) (sample * 32767);
        }
        return buffer;
    }

    private short[] generateCoinPing() {
        int numSamples = (SAMPLE_RATE * 110) / 1000;
        short[] buffer = new short[numSamples];
        double phase1 = 0.0;
        double phase2 = 0.0;
        for (int i = 0; i < numSamples; i++) {
            float t = (float) i / numSamples;
            phase1 += (2.0 * Math.PI * 987.77) / SAMPLE_RATE;  // B5
            phase2 += (2.0 * Math.PI * 1318.51) / SAMPLE_RATE; // E6
            float env = 1.0f - t;
            double sample = (0.6 * Math.sin(phase1) + 0.4 * Math.sin(phase2)) * env * 0.7;
            buffer[i] = (short) (sample * 32767);
        }
        return buffer;
    }

    private short[] generatePowerUpArpeggio() {
        float[] notes = new float[]{523.25f, 659.25f, 783.99f, 1046.50f}; // C5, E5, G5, C6
        int noteDuration = 60;
        int samplesPerNote = (SAMPLE_RATE * noteDuration) / 1000;
        short[] buffer = new short[samplesPerNote * notes.length];
        for (int n = 0; n < notes.length; n++) {
            double phase = 0.0;
            for (int i = 0; i < samplesPerNote; i++) {
                float t = (float) i / samplesPerNote;
                phase += (2.0 * Math.PI * notes[n]) / SAMPLE_RATE;
                float env = 1.0f - t * 0.5f;
                double sample = Math.sin(phase) * env * 0.7;
                buffer[n * samplesPerNote + i] = (short) (sample * 32767);
            }
        }
        return buffer;
    }

    private short[] generateCrash() {
        int numSamples = (SAMPLE_RATE * 260) / 1000;
        short[] buffer = new short[numSamples];
        double phase = 0.0;
        for (int i = 0; i < numSamples; i++) {
            float t = (float) i / numSamples;
            float freq = 180.0f - 140.0f * t;
            phase += (2.0 * Math.PI * freq) / SAMPLE_RATE;
            // Noise component for explosive crunch
            double noise = (Math.random() * 2.0 - 1.0) * 0.35;
            float env = (float) Math.exp(-t * 4.0);
            double sample = (Math.sin(phase) * 0.65 + noise) * env * 0.85;
            buffer[i] = (short) (sample * 32767);
        }
        return buffer;
    }

    private void playSfx(int soundId, float volume) {
        if (!prefs.isSoundEnabled() || soundPool == null || soundId == 0) return;
        soundPool.play(soundId, volume, volume, 1, 0, 1.0f);
    }

    public void playJump() {
        playSfx(soundJump, 0.75f);
    }

    public void playDoubleJump() {
        playSfx(soundDoubleJump, 0.85f);
    }

    public void playSlide() {
        playSfx(soundSlide, 0.65f);
    }

    public void playCoin() {
        playSfx(soundCoin, 0.8f);
    }

    public void playPowerUp() {
        playSfx(soundPowerUp, 0.85f);
    }

    public void playShieldBreak() {
        playSfx(soundShieldBreak, 0.9f);
    }

    public void playCollision() {
        playSfx(soundCollision, 0.95f);
    }

    public void playClick() {
        playSfx(soundClick, 0.55f);
    }

    // Procedural Synth-wave Music using a Static Hardware Looping AudioTrack (Zero CPU)
    private void initMusicTrack() {
        new Thread(() -> {
            try {
                // 120 BPM 4-second seamless loop: 8 eighth-notes
                int totalSamples = SAMPLE_RATE * 4;
                short[] buffer = new short[totalSamples];
                float[] bassNotes = new float[]{110.0f, 110.0f, 130.81f, 110.0f, 87.31f, 87.31f, 98.0f, 82.41f};
                int samplesPerStep = totalSamples / bassNotes.length;

                for (int noteIdx = 0; noteIdx < bassNotes.length; noteIdx++) {
                    float freq = bassNotes[noteIdx];
                    double phase = 0.0;
                    int offset = noteIdx * samplesPerStep;
                    for (int i = 0; i < samplesPerStep; i++) {
                        float t = (float) i / samplesPerStep;
                        phase += (2.0 * Math.PI * freq) / SAMPLE_RATE;
                        float env = (float) Math.exp(-t * 3.5);
                        float kick = (i < samplesPerStep / 6) ? (float) Math.sin((double) i / 8) * 0.35f : 0.0f;
                        double val = (Math.sin(phase) * 0.45 + kick) * env * 0.35;
                        buffer[offset + i] = (short) (val * 32767);
                    }
                }

                musicTrack = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build())
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(SAMPLE_RATE)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build())
                        .setBufferSizeInBytes(buffer.length * 2)
                        .setTransferMode(AudioTrack.MODE_STATIC)
                        .build();

                musicTrack.write(buffer, 0, buffer.length);
                musicTrack.setLoopPoints(0, totalSamples, -1);

                if (prefs.isMusicEnabled() && isMusicPlaying) {
                    musicTrack.play();
                }
            } catch (Exception ignored) {
            }
        }).start();
    }

    public synchronized void startMusic() {
        if (!prefs.isMusicEnabled()) return;
        isMusicPlaying = true;
        if (musicTrack != null) {
            try {
                if (musicTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
                    musicTrack.play();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public synchronized void stopMusic() {
        isMusicPlaying = false;
        if (musicTrack != null) {
            try {
                if (musicTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                    musicTrack.pause();
                    musicTrack.reloadStaticData();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void release() {
        stopMusic();
        if (musicTrack != null) {
            try {
                musicTrack.stop();
                musicTrack.release();
            } catch (Exception ignored) {
            }
            musicTrack = null;
        }
        if (soundPool != null) {
            try {
                soundPool.release();
            } catch (Exception ignored) {
            }
            soundPool = null;
        }
    }
}
