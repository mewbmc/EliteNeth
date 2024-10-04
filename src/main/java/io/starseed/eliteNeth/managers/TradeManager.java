package io.starseed.eliteNeth.managers;

import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.Common;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.starseed.eliteNeth.settings.Settings;
import io.starseed.eliteNeth.trading.TradeMenu;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Data;

public class TradeManager {

    @Getter
    private final Map<Integer, Trade> trades = new HashMap<>();

    /**
     * Initialize the TradeManager with default trades
     */
    public TradeManager() {
        loadDefaultTrades();
    }

    /**
     * Load default trades if none are configured
     */
    private void loadDefaultTrades() {
        trades.put(1, new Trade(
                CompMaterial.NETHERITE_INGOT, 1,
                CompMaterial.DIAMOND, 64,
                11 // slot in the menu
        ));

        trades.put(2, new Trade(
                CompMaterial.ANCIENT_DEBRIS, 2,
                CompMaterial.EMERALD, 32,
                13 // slot in the menu
        ));

        trades.put(3, new Trade(
                CompMaterial.BLAZE_POWDER, 4,
                CompMaterial.DIAMOND, 5,
                15 // slot in the menu
        ));
    }

    /**
     * Opens the trade menu for a player
     * @param player The player to show the menu to
     */
    public void openTradeMenu(Player player) {
        if (!player.hasPermission("elitenether.trade")) {
            Common.tell(player, Settings.Messages.NO_PERMISSION);
            return;
        }

        new TradeMenu().displayTo(player);
    }

    /**
     * Execute a trade for a player
     * @param player The player making the trade
     * @param tradeId The ID of the trade
     * @return true if trade was successful, false otherwise
     */
    public boolean executeTrade(Player player, int tradeId) {
        Trade trade = trades.get(tradeId);
        if (trade == null) return false;

        if (!hasRequiredItems(player, trade.getCurrencyMaterial(), trade.getCurrencyAmount())) {
            Common.tell(player, Settings.Messages.TRADE_FAILED);
            return false;
        }

        // Remove currency items
        removeItems(player, trade.getCurrencyMaterial(), trade.getCurrencyAmount());

        // Give reward items
        giveItems(player, trade.getRewardMaterial(), trade.getRewardAmount());

        Common.tell(player, Settings.Messages.TRADE_SUCCESS);
        return true;
    }

    /**
     * Check if a player has the required items for a trade
     * @param player The player to check
     * @param material The material needed
     * @param amount The amount needed
     * @return true if player has the items, false otherwise
     */
    public boolean hasRequiredItems(Player player, CompMaterial material, int amount) {
        return player.getInventory().containsAtLeast(material.toItem(), amount);
    }

    /**
     * Remove items from a player's inventory
     * @param player The player to remove items from
     * @param material The material to remove
     * @param amount The amount to remove
     */
    private void removeItems(Player player, CompMaterial material, int amount) {
        ItemStack item = material.toItem();
        item.setAmount(amount);
        player.getInventory().removeItem(item);
    }

    /**
     * Give items to a player
     * @param player The player to give items to
     * @param material The material to give
     * @param amount The amount to give
     */
    private void giveItems(Player player, CompMaterial material, int amount) {
        ItemStack item = material.toItem();
        item.setAmount(amount);
        player.getInventory().addItem(item);
    }

    /**
     * Get a trade by its ID
     * @param tradeId The ID of the trade
     * @return The Trade object, or null if not found
     */
    public Trade getTrade(int tradeId) {
        return trades.get(tradeId);
    }

    /**
     * Reload all trades from config
     */
    public void reloadTrades() {
        trades.clear();
        loadDefaultTrades();
        // Load from config here TODO
    }

    @Data
    public static class Trade {
        private final CompMaterial rewardMaterial;
        private final int rewardAmount;
        private final CompMaterial currencyMaterial;
        private final int currencyAmount;
        private final int menuSlot;

        public String getDisplayName() {
            return "&6Buy " + rewardAmount + "x " + formatMaterialName(rewardMaterial);
        }

        public String getCostDisplay() {
            return "&7Cost: &6" + currencyAmount + "x " + formatMaterialName(currencyMaterial);
        }

        private String formatMaterialName(CompMaterial material) {
            return material.name().replace("_", " ").toLowerCase();
        }
    }
}