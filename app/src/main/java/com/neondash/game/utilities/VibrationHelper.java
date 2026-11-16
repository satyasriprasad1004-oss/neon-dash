package com.neondash.game.utilities;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

import com.neondash.game.database.PreferencesManager;

public class VibrationHelper {

    private static Vibrator cachedVibrator = null;
    private static boolean initialized = false;

    private static synchronized Vibrator getVibrator(Context context) {
        if (!initialized && context != null) {
            Context appCtx = context.getApplicationContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                VibratorManager vibratorManager = (VibratorManager) appCtx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                cachedVibrator = vibratorManager != null ? vibratorManager.getDefaultVibrator() : null;
            } else {
                cachedVibrator = (Vibrator) appCtx.getSystemService(Context.VIBRATOR_SERVICE);
            }
            initialized = true;
        }
        return cachedVibrator;
    }

    public static void vibrateTap(Context context) {
        if (!PreferencesManager.getInstance(context).isVibrationEnabled()) return;
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(25);
            }
        }
    }

    public static void vibratePowerUp(Context context) {
        if (!PreferencesManager.getInstance(context).isVibrationEnabled()) return;
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                long[] timings = new long[]{0, 40, 50, 70};
                int[] amplitudes = new int[]{0, 150, 0, 255};
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1));
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    public static void vibrateCollision(Context context) {
        if (!PreferencesManager.getInstance(context).isVibrationEnabled()) return;
        Vibrator vibrator = getVibrator(context);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(250);
            }
        }
    }
}
