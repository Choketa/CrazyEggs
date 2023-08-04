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
import java.util.HashSet;
import java.util.UUID;

import static me.choketa.crazyeggs.utils.EggUtils.isDestructionEgg;

public class DestructionEggEvents implements Listener {

    CrazyEggs plugin;
    DestructionEggRecipe egg;


    HashSet<UUID> projectile = new HashSet<>();
    HashMap<UUID, Long> cooldown = new HashMap<>();
    long timeElapsed;

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

        if (!projectile.contains(player.getUniqueId())) {
            return;
        }
        float power = (float) plugin.getConfig().getDouble("power");
        Bukkit.getWorld(player.getWorld().getUID()).createExplosion(event.getEntity().getLocation(), power, false);
        projectile.remove(player.getUniqueId());
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
            projectile.add(player.getUniqueId());
            return;
        }
        if (!cooldown.containsKey(player.getUniqueId())) {
            projectile.add(player.getUniqueId());
            cooldown.put(player.getUniqueId(), System.currentTimeMillis());
        } else {
            timeElapsed = System.currentTimeMillis() - cooldown.get(player.getUniqueId());
            int cooldowns = plugin.getConfig().getInt("cooldown");
            if (timeElapsed >= (cooldowns * 1000L)) {
                projectile.add(player.getUniqueId());
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
    //Disables crafting with the eggs or if player has no permission
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (!plugin.getConfig().getBoolean("can-craft-items")) return;
        Player player = (Player) event.getInventory().getHolder();
        if (player == null) return;
        if (event.getInventory().getResult() == null) return;
        if (!player.hasPermission("crazyeggs.destruction.craft") && event.getInventory().getResult().isSimilar(egg.eggItem())) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }
        if (!event.getInventory().contains(egg.eggItem())) return;

        //It starts from index 1 to exclude the result slot
        for (int i = 1; i <= event.getInventory().getMatrix().length; i++) {
            ItemStack item = event.getInventory().getItem(i);


            if (item == null) continue;
            if (!isDestructionEgg(plugin,item)) continue;
            event.getInventory().setResult(new ItemStack(Material.AIR));

        }
    }
}
