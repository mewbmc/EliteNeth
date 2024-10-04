package io.starseed.eliteNeth.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import io.starseed.eliteNeth.EliteNether;
import org.bukkit.entity.Player;

public class NetherListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals(SimpleSettings.get("Settings.Nether_World_Name"))) {
            EliteNether.getInstance().getNetherManager().stopTimer(player);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String netherWorld = SimpleSettings.get("Settings.Nether_World_Name");

        if (event.getFrom().getWorld().getName().equals(netherWorld) &&
                !event.getTo().getWorld().getName().equals(netherWorld)) {
            EliteNether.getInstance().getNetherManager().stopTimer(player);
        }
    }
}