package io.starseed.eliteNeth.commands;

import org.mineacademy.fo.command.SimpleCommand;
import io.starseed.eliteNeth.EliteNether;
import io.starseed.eliteNeth.settings.Settings;
import org.bukkit.entity.Player;

public class NetherCommand extends SimpleCommand {

    public NetherCommand() {
        super("nether|n");
        setMinArguments(0);
        setUsage("/nether [timeleft]");
        setPermission("elitenether.command.nether");
    }

    @Override
    protected void onCommand() {
        if (!(sender instanceof Player)) {
            tell(Settings.Messages.PLAYERS_ONLY);
            return;
        }

        Player player = getPlayer();

        if (args.length == 0) {
            // Handle teleport to nether
            if (player.getWorld().getName().equals(Settings.NetherSettings.WORLD_NAME)) {
                tell(Settings.Messages.ALREADY_IN_NETHER);
                return;
            }

            if (!EliteNether.getInstance().getNetherManager().canEnterNether(player)) {
                return;
            }

            EliteNether.getInstance().getNetherManager().teleportToNether(player);
            return;
        }

        if (args[0].equalsIgnoreCase("timeleft")) {
            if (!player.getWorld().getName().equals(Settings.NetherSettings.WORLD_NAME)) {
                tell(Settings.Messages.NOT_IN_NETHER);
                return;
            }

            String timeLeft = EliteNether.getInstance().getNetherManager().getTimeLeft(player);
            tell(Settings.Messages.TIME_LEFT.replace("{time}", timeLeft));
        }
    }
}