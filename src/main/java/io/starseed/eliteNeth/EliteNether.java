package io.starseed.eliteNeth;

import io.starseed.eliteNeth.settings.Settings;
import lombok.Getter;
import org.mineacademy.fo.plugin.SimplePlugin;
import io.starseed.eliteNeth.commands.NetherCommand;
import io.starseed.eliteNeth.commands.TradeCommand;
import io.starseed.eliteNeth.Listeners.NetherListener;
import io.starseed.eliteNeth.managers.NetherManager;
import io.starseed.eliteNeth.managers.TradeManager;
import io.starseed.eliteNeth.settings.Settings;

public class EliteNether extends SimplePlugin {

    @Getter
    private static EliteNether instance;
    @Getter
    private NetherManager netherManager;
    @Getter
    private TradeManager tradeManager;

    @Override
    protected void onPluginStart() {
        instance = this;

        // Initialize managers
        this.netherManager = new NetherManager();
        this.tradeManager = new TradeManager();

        // Register commands
        registerCommand(new NetherCommand());
        registerCommand(new TradeCommand());

        // Register listeners
        registerEvents(new NetherListener());
    }

    @Override
    protected void onReloadablesStart() {
        // Load settings
        Settings.reload();
    }

}

