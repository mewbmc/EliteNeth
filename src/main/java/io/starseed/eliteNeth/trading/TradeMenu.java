package io.starseed.eliteNeth.trading;

import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import io.starseed.eliteNeth.settings.Settings;

import lombok.Getter;

public class TradeMenu extends Menu {

    public TradeMenu() {
        setTitle(Settings.TradeSettings.MENU_TITLE);
        setSize(9 * 3); // 3 rows
    }

    @Override
    public ItemStack getItemAt(int slot) {
        if (slot == 11) {
            return ItemCreator.of(CompMaterial.NETHERITE_INGOT)
                    .name("&6Buy Netherite Ingot")
                    .lore("&7Cost: &664 Diamonds")
                    .lore("")
                    .lore("&eClick to purchase!")
                    .build().make();
        }

        if (slot == 13) {
            return ItemCreator.of(CompMaterial.ANCIENT_DEBRIS)
                    .name("&6Buy Ancient Debris")
                    .lore("&7Cost: &632 Emeralds")
                    .lore("")
                    .lore("&eClick to purchase!")
                    .build().make();
        }

        if (slot == 15) {
            return ItemCreator.of(CompMaterial.BLAZE_POWDER)
                    .name("&6Buy Blaze Powder")
                    .lore("&7Cost: &65 Diamonds")
                    .lore("")
                    .lore("&eClick to purchase!")
                    .build().make();
        }

        return null;
    }

    @Override
    protected void onMenuClick(Player player, int slot, ItemStack clicked) {
        if (clicked == null) return;

        switch (slot) {
            case 11:
                handleTrade(player, CompMaterial.DIAMOND, 64, CompMaterial.NETHERITE_INGOT, 1);
                break;
            case 13:
                handleTrade(player, CompMaterial.EMERALD, 32, CompMaterial.ANCIENT_DEBRIS, 2);
                break;
            case 15:
                handleTrade(player, CompMaterial.DIAMOND, 5, CompMaterial.BLAZE_POWDER, 4);
                break;
        }
    }

    private void handleTrade(Player player, CompMaterial currency, int cost, CompMaterial item, int amount) {
        if (hasItems(player, currency, cost)) {
            removeItems(player, currency, cost);
            giveItems(player, item, amount);
            player.playSound(player.getLocation(), CompMaterial.EXPERIENCE_BOTTLE.getMaterial().name(), 1, 1);
            tell(String.valueOf(player), Settings.Messages.TRADE_SUCCESS);
        } else {
            tell(String.valueOf(player), Settings.Messages.TRADE_FAILED);
        }
    }

    private boolean hasItems(Player player, CompMaterial material, int amount) {
        return player.getInventory().containsAtLeast(material.toItem(), amount);
    }

    private void removeItems(Player player, CompMaterial material, int amount) {
        ItemStack item = material.toItem();
        item.setAmount(amount);
        player.getInventory().removeItem(item);
    }

    private void removeItems(Player player, ItemStack item) {
        player.getInventory().removeItem(item);
    }

    private void giveItems(Player player, CompMaterial material, int amount) {
        ItemStack item = material.toItem();
        item.setAmount(amount);
        player.getInventory().addItem(item);
    }
}