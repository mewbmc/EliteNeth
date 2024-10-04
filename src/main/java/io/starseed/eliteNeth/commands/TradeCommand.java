package io.starseed.eliteNeth.commands;

import org.mineacademy.fo.command.SimpleCommand;
import io.starseed.eliteNeth.trading.TradeMenu;
import org.bukkit.entity.Player;
import io.starseed.eliteNeth.settings.Settings;

public class TradeCommand extends SimpleCommand {

    public TradeCommand() {
        super("nethertrade|trade");
        setPermission("elitenether.trade");
    }

    @Override
    protected void onCommand() {
        if (!(sender instanceof Player)) {
            tell(Settings.Messages.PLAYERS_ONLY);
            return;
        }

        Player player = getPlayer();
        new TradeMenu().displayTo(player);
    }
}