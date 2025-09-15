package me.choketa.crazyeggs.listeners;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.recipes.DestructionEggRecipe;
import org.bukkit.*;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static me.choketa.crazyeggs.utils.EggUtils.isDestructionEgg;

public class DestructionEggEvents implements Listener {

    private final CrazyEggs plugin;
    private final DestructionEggRecipe egg;


    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public DestructionEggEvents(CrazyEggs plugin) {
        this.plugin = plugin;
        this.egg = new DestructionEggRecipe(plugin);
    }

    //Makes the impact happen
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();

        if (!isDestructionEgg(plugin, event.getEntity())) return;

        float power = (float) plugin.getConfig().getDouble("power");
        Bukkit.getWorld(player.getWorld().getUID()).createExplosion(event.getEntity().getLocation(), power, false);
        }
    //Adds the player to the Set
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!(event.getEntity() instanceof Egg)) {
            return;
        }
        if (!isDestructionEgg(plugin,item)) return;
        if (!player.hasPermission("crazyeggs.destruction.use")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED+"[CrazyEggs] You are not allowed to use the egg!");
            return;
        }
        if (player.hasPermission("crazyeggs.bypass.cooldown") || player.isOp()) {
            return;
        }
        if (!cooldown.containsKey(player.getUniqueId())) {
            cooldown.put(player.getUniqueId(), System.currentTimeMillis());
        } else {
            long timeElapsed = System.currentTimeMillis() - cooldown.get(player.getUniqueId());
            int cooldowns = plugin.getConfig().getInt("cooldown");
            if (timeElapsed >= (cooldowns * 1000L)) {
                cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            }
            else {
                player.sendMessage(ChatColor.DARK_RED+"You are in a cooldown. "+(((cooldowns*1000L) - timeElapsed)/1000)+" more seconds");
                event.setCancelled(true);
            }
        }
    }
    //Disables the egg hatching
    @EventHandler
    public void onHatch(PlayerEggThrowEvent event) {
        final ItemStack item = event.getEgg().getItem();
        if (!isDestructionEgg(plugin, item)) return;
        event.setHatching(false);
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NamespacedKey recipe = new NamespacedKey(plugin,"destructionegg");
        if (!player.hasPermission("crazyeggs.craft") && player.hasDiscoveredRecipe(recipe)) player.undiscoverRecipe(recipe);
        if (player.hasDiscoveredRecipe(recipe)) return;
        player.discoverRecipe(recipe);
    }
    //Handles the Crafting. Only with the actual, unedited Crazy Eggs.
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (!plugin.getConfig().getBoolean("can-craft-items")) return;
        Player player = (Player) event.getInventory().getHolder();
        if (player == null) return;
        if (event.getInventory().getResult() == null) return;
        if (!player.hasPermission("crazyeggs.destruction.craft") && event.getInventory().getResult().isSimilar(egg.eggItem())) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }
}
