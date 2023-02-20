package dev.crifurch.unlimitedadmin.modules.world.gui;

import dev.crifurch.unlimitedadmin.UnlimitedAdmin;
import dev.crifurch.unlimitedadmin.modules.world.WorldsModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collection;

public class WorldManagerGui implements Listener {
    private final WorldsModule manager;
    private final Inventory inv;
    private HumanEntity entity;

    public WorldManagerGui(WorldsModule manager) {
        this.manager = manager;
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 36);

        // Put the items into the inventory
        initializeItems();
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        inv.setItem(5, createGuiItem(Material.DIRT, "New world", "§aFirst line of the lore", "§bSecond line of the lore"));
        int i = 9;
        final Collection<String> worlds = manager.getWorlds();
        for (String element : worlds) {
            inv.setItem(i, createGuiItem(Material.COBBLESTONE, element, "Custom world"));
            i++;
        }
        for (; i < 27; i++) {
            inv.setItem(i, createGuiItem(Material.GLOWSTONE, "Empty", "No world"));
        }
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
        Bukkit.getServer().getPluginManager().registerEvents(this, UnlimitedAdmin.getInstance());
        entity = ent;
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isTransparent()) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is the best option for your inventory click's
        p.sendMessage("You clicked at slot " + e.getRawSlot());
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {

        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        if (e.getPlayer().getUniqueId().equals(entity.getUniqueId())) {
            inv.clear();
        }
    }
}