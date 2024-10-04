package io.starseed.eliteNeth.managers;

import lombok.Getter;
import org.mineacademy.fo.remain.CompSound;
import org.mineacademy.fo.Common;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.WorldCreator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import io.starseed.eliteNeth.EliteNether;
import io.starseed.eliteNeth.settings.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetherManager {
    private final Map<UUID, Long> playerTimers = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Location> returnLocations = new HashMap<>();
    /**
     * -- GETTER --
     *  Get the nether world
     *
     * @return The nether world
     */
    @Getter
    private World netherWorld;

    /**
     * Initialize the NetherManager
     * Creates the nether world if it doesn't exist and starts the expiration checker
     */
    public NetherManager() {
        initNetherWorld();
        startExpirationChecker();
    }

    /**
     * Initialize or load the custom nether world
     */
    private void initNetherWorld() {
        String worldName = Settings.NetherSettings.WORLD_NAME;
        netherWorld = Bukkit.getWorld(worldName);

        if (netherWorld == null) {
            Common.log("Creating new nether world: " + worldName);
            WorldCreator creator = new WorldCreator(worldName);
            creator.environment(World.Environment.NETHER);
            creator.type(WorldType.NORMAL);
            netherWorld = creator.createWorld();
        }
    }

    /**
     * Start the timer that checks for expired nether sessions
     */
    private void startExpirationChecker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : netherWorld.getPlayers()) {
                    UUID uuid = player.getUniqueId();
                    if (playerTimers.containsKey(uuid)) {
                        if (hasTimeExpired(player)) {
                            teleportBack(player);
                        }
                    }
                }
            }
        }.runTaskTimer(EliteNether.getInstance(), 20L, 20L); // Check every second
    }

    /**
     * Check if a player can enter the nether
     * @param player The player to check
     * @return true if the player can enter, false otherwise
     */
    public boolean canEnterNether(Player player) {
        UUID uuid = player.getUniqueId();

        // Check permission
        if (!player.hasPermission("elitenether.enter")) {
            Common.tell(player, Settings.Messages.NO_PERMISSION);
            return false;
        }

        // Check if player is already in nether
        if (player.getWorld().equals(netherWorld)) {
            Common.tell(player, Settings.Messages.ALREADY_IN_NETHER);
            return false;
        }

        // Check cooldown
        if (cooldowns.containsKey(uuid)) {
            long cooldownEnd = cooldowns.get(uuid);
            if (System.currentTimeMillis() < cooldownEnd) {
                long timeLeft = (cooldownEnd - System.currentTimeMillis()) / 1000;
                String timeLeftFormatted = formatTime(timeLeft * 1000);
                Common.tell(player, Settings.Messages.COOLDOWN_ACTIVE
                        .replace("{time}", timeLeftFormatted));
                return false;
            }
            cooldowns.remove(uuid);
        }
        return true;
    }

    /**
     * Teleport a player to the nether and start their timer
     * @param player The player to teleport
     */
    public void teleportToNether(Player player) {
        UUID uuid = player.getUniqueId();

        // Save return location
        returnLocations.put(uuid, player.getLocation());

        // Find safe location in nether
        Location safeLocation = findSafeLocation();

        // Teleport player
        player.teleport(safeLocation);

        // Start timer
        startTimer(player);

        // Play sound and show effects
        CompSound.ENTITY_ENDERMAN_TELEPORT.play(player);

        // Send message with time remaining
        String timeLeft = formatTime(Settings.NetherSettings.MAX_TIME_MINUTES * 60000L);
        Common.tell(player, Settings.Messages.ENTERED_NETHER
                .replace("{time}", timeLeft));
    }

    /**
     * Find or create a safe location in the nether
     * @return A safe location for teleporting
     */
    private Location findSafeLocation() {
        // Get spawn location
        Location spawn = netherWorld.getSpawnLocation();

        // Ensure the spawn area is safe (3x3 platform)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location platform = spawn.clone().add(x, -1, z);
                platform.getBlock().setType(org.bukkit.Material.NETHERRACK);

                Location air1 = platform.clone().add(0, 1, 0);
                Location air2 = platform.clone().add(0, 2, 0);
                air1.getBlock().setType(org.bukkit.Material.AIR);
                air2.getBlock().setType(org.bukkit.Material.AIR);
            }
        }

        // Return center of platform
        return spawn.clone().add(0.5, 0, 0.5);
    }

    /**
     * Teleport a player back from the nether
     * @param player The player to teleport
     */
    public void teleportBack(Player player) {
        UUID uuid = player.getUniqueId();

        // Get return location or use main world spawn if none exists
        Location returnLoc = returnLocations.getOrDefault(uuid,
                Bukkit.getWorlds().get(0).getSpawnLocation());

        // Teleport player
        player.teleport(returnLoc);

        // Stop timer and set cooldown
        stopTimer(player);

        // Play sound and show effects
        CompSound.ENTITY_ENDERMAN_TELEPORT.play(player);

        // Send message
        Common.tell(player, Settings.Messages.TIME_EXPIRED);

        // Remove return location
        returnLocations.remove(uuid);
    }

    /**
     * Start a player's nether timer
     * @param player The player to start the timer for
     */
    public void startTimer(Player player) {
        playerTimers.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Stop a player's timer and set their cooldown
     * @param player The player to stop the timer for
     */
    public void stopTimer(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerTimers.containsKey(uuid)) {
            long startTime = playerTimers.get(uuid);
            long timeSpent = System.currentTimeMillis() - startTime;

            // Set cooldown only if they used significant time (more than 1 minute)
            if (timeSpent > 60000) {
                cooldowns.put(uuid, System.currentTimeMillis() +
                        (Settings.NetherSettings.COOLDOWN_HOURS * 3600000L));
            }

            playerTimers.remove(uuid);
        }
    }

    /**
     * Check if a player's nether time has expired
     * @param player The player to check
     * @return true if the time has expired, false otherwise
     */
    public boolean hasTimeExpired(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerTimers.containsKey(uuid)) return false;

        long startTime = playerTimers.get(uuid);
        long currentTime = System.currentTimeMillis();
        long maxTime = Settings.NetherSettings.MAX_TIME_MINUTES * 60000L;

        return (currentTime - startTime) >= maxTime;
    }

    /**
     * Get the time remaining for a player in the nether
     * @param player The player to check
     * @return Formatted string of time remaining
     */
    public String getTimeLeft(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerTimers.containsKey(uuid)) return "0:00";

        long startTime = playerTimers.get(uuid);
        long currentTime = System.currentTimeMillis();
        long maxTime = Settings.NetherSettings.MAX_TIME_MINUTES * 60000L;
        long timeLeft = maxTime - (currentTime - startTime);

        return formatTime(Math.max(0, timeLeft));
    }

    /**
     * Format milliseconds into a readable time string
     * @param milliseconds Time in milliseconds
     * @return Formatted string (MM:SS)
     */
    private String formatTime(long milliseconds) {
        if (milliseconds < 0) return "0:00";
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Clean up any data for a player (used when they leave the server)
     * @param uuid The UUID of the player to clean up
     */
    public void cleanupPlayer(UUID uuid) {
        playerTimers.remove(uuid);
        returnLocations.remove(uuid);
        // Don't remove cooldowns as they need to persist
    }
}