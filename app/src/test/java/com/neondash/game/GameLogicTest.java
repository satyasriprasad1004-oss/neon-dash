package com.neondash.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.neondash.game.models.Achievement;
import com.neondash.game.models.CharacterModel;
import com.neondash.game.models.GameEnvironment;
import com.neondash.game.models.Mission;
import com.neondash.game.powerups.PowerUp;
import com.neondash.game.utilities.Constants;

import org.junit.Test;

public class GameLogicTest {

    @Test
    public void testInitialCharacters() {
        CharacterModel[] chars = CharacterModel.getInitialCharacters();
        assertEquals("Must have 4 playable characters", 4, chars.length);

        // Nova (default character)
        CharacterModel nova = chars[0];
        assertEquals(Constants.CHAR_NOVA, nova.getId());
        assertEquals("Nova", nova.getName());
        assertEquals(0, nova.getUnlockCost());
        assertTrue("Nova must be unlocked by default", nova.isUnlocked());
        assertTrue("Nova must be equipped by default", nova.isEquipped());

        // Shadow
        CharacterModel shadow = chars[1];
        assertEquals(Constants.CHAR_SHADOW, shadow.getId());
        assertFalse(shadow.isUnlocked());
        assertTrue(shadow.getUnlockCost() > 0);

        // Bolt
        CharacterModel bolt = chars[2];
        assertEquals(Constants.CHAR_BOLT, bolt.getId());
        assertTrue("Bolt should have higher speed multiplier", bolt.getSpeedMultiplier() > 1.1f);

        // Titan
        CharacterModel titan = chars[3];
        assertEquals(Constants.CHAR_TITAN, titan.getId());
        assertTrue("Titan should have strong magnet multiplier", titan.getMagnetMultiplier() > 1.3f);
    }

    @Test
    public void testEnvironments() {
        GameEnvironment[] envs = GameEnvironment.getAllEnvironments();
        assertEquals("Must have 4 environments", 4, envs.length);

        GameEnvironment city = envs[0];
        assertEquals(Constants.ENV_NEON_CITY, city.getId());
        assertTrue("Neon City must be unlocked by default", city.isUnlocked());
        assertEquals(0, city.getUnlockDistance());

        GameEnvironment desert = envs[1];
        assertEquals(Constants.ENV_DESERT_RUN, desert.getId());
        assertFalse(desert.isUnlocked());
        assertTrue(desert.getUnlockDistance() > 0);

        GameEnvironment jungle = envs[2];
        assertEquals(Constants.ENV_JUNGLE_ESCAPE, jungle.getId());
        assertFalse(jungle.isUnlocked());

        GameEnvironment cyber = envs[3];
        assertEquals(Constants.ENV_CYBER_WORLD, cyber.getId());
        assertFalse(cyber.isUnlocked());

        // Test getById
        GameEnvironment retrieved = GameEnvironment.getById(Constants.ENV_CYBER_WORLD);
        assertNotNull(retrieved);
        assertEquals("Cyber World", retrieved.getName());
    }

    @Test
    public void testMissions() {
        Mission[] missions = Mission.getInitialMissions();
        assertTrue("Must have multiple initial missions", missions.length >= 5);

        for (Mission m : missions) {
            assertNotNull(m.getId());
            assertNotNull(m.getTitle());
            assertNotNull(m.getDescription());
            assertTrue("Target count must be positive", m.getTargetCount() > 0);
            assertTrue("Reward must be positive", m.getRewardCoins() > 0);
            assertFalse("Initial mission should not be completed", m.isCompleted());
            assertFalse("Initial mission should not be claimed", m.isClaimed());
        }
    }

    @Test
    public void testAchievements() {
        Achievement[] achievements = Achievement.getInitialAchievements();
        assertTrue("Must have achievements", achievements.length >= 5);

        for (Achievement a : achievements) {
            assertNotNull(a.getId());
            assertNotNull(a.getTitle());
            assertTrue(a.getRewardCoins() > 0);
            assertFalse(a.isUnlocked());
        }
    }

    @Test
    public void testPowerUpTypes() {
        PowerUp magnet = new PowerUp();
        magnet.init(PowerUp.TYPE_MAGNET, 100, 100);
        assertEquals("MAGNET", magnet.getName());
        assertTrue(magnet.isActive);

        PowerUp shield = new PowerUp();
        shield.init(PowerUp.TYPE_SHIELD, 100, 100);
        assertEquals("SHIELD", shield.getName());

        PowerUp speed = new PowerUp();
        speed.init(PowerUp.TYPE_SPEED_BOOST, 100, 100);
        assertEquals("BOOST", speed.getName());

        PowerUp slowMo = new PowerUp();
        slowMo.init(PowerUp.TYPE_SLOW_MOTION, 100, 100);
        assertEquals("SLOW-MO", slowMo.getName());

        PowerUp doubleJump = new PowerUp();
        doubleJump.init(PowerUp.TYPE_DOUBLE_JUMP, 100, 100);
        assertEquals("2X JUMP", doubleJump.getName());
    }

    @Test
    public void testConstantsAndDifficulty() {
        assertTrue(Constants.BASE_RUN_SPEED > 0);
        assertTrue(Constants.MAX_RUN_SPEED > Constants.BASE_RUN_SPEED);
        assertTrue(Constants.BASE_JUMP_FORCE < 0); // negative is upward in Canvas coordinates
        assertTrue(Constants.GRAVITY > 0);

        assertTrue("Hard difficulty should have higher multiplier than easy",
                Constants.DIFFICULTY_HARD_MULT > Constants.DIFFICULTY_EASY_MULT);
    }
}
