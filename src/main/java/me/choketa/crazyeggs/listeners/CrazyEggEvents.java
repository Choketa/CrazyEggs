package me.choketa.crazyeggs.listeners;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.recipes.CrazyEggRecipe;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


import java.util.Random;

import static me.choketa.crazyeggs.utils.EggUtils.*;


public class CrazyEggEvents implements Listener {
    private final CrazyEggs plugin;
    private final CrazyEggRecipe egg;

    public CrazyEggEvents() {
        this.plugin = CrazyEggs.getPlugin();
        this.egg = plugin.getCrazyEggsRecipe();
    }

    //Makes the impact happen
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getHitEntity() instanceof LivingEntity hitEntity))
            return;

        if (!isCrazyEgg(event.getEntity())) return;

        World world = hitEntity.getWorld();
        Location location = hitEntity.getLocation();

        if (hitEntity instanceof Enderman && plugin.getConfig().getBoolean("enable-enderman-effect"))
            world.playEffect(location, Effect.DRAGON_BREATH, 50);

        Sound impactSound;
        Particle particle;

        String soundString = plugin.getConfig().getString("sound-when-hit");
        if (soundString == null || soundString.isEmpty()) impactSound = null;
        else impactSound = Sound.valueOf(soundString);

        String particleString = plugin.getConfig().getString("default-particle");
        if (particleString == null || particleString.isEmpty()) particle = null;
        else particle = Particle.valueOf(particleString);

        hitEntity.damage(plugin.getConfig().getInt("damage"));
        if (impactSound != null)
            world.playSound(hitEntity, impactSound, 0.5f, 1f);
        if (particle != null)
            world.spawnParticle(particle, location, plugin.getConfig().getInt("particle-count"));

        Vector direction = event.getEntity().getVelocity().multiply(plugin.getConfig().getDouble("velocity-multiplier"));
        hitEntity.setVelocity(direction.setY(plugin.getConfig().getDouble("velocity-set-y")));
        hitEntity.setVelocity(direction);

        //1/x chance
        int max = plugin.getConfig().getInt("spawn-egg-rarity");
        int random = new Random().nextInt(0, max);

        if (hitEntity.isDead() && random+1 == max && !(hitEntity instanceof Player)
                && plugin.getConfig().getBoolean("enable-spawn-eggs-drop")) {
            String name = hitEntity.getName().toUpperCase();
            Material eggMaterial = Material.matchMaterial(name + "_SPAWN_EGG");
            if (eggMaterial == null) return;
            ItemStack eggItem = new ItemStack(eggMaterial);

            world.dropItem(location, eggItem);
        }
    }

    //Adds the player to the Set
    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!(event.getEntity() instanceof Egg)) {
            return;
        }
        if (!isCrazyEgg(item)) return;
        if (!player.hasPermission("crazyeggs.use")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "[CrazyEggs] You are not allowed to use the egg!");
        }

    }

    //Disables the egg hatching
    @EventHandler
    public void onHatch(PlayerEggThrowEvent event) {
        final ItemStack item = event.getEgg().getItem();
        if (!isCrazyEgg(item)) return;
        event.setHatching(false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NamespacedKey recipe = new NamespacedKey(plugin, "egg");
        if (!player.hasPermission("crazyeggs.craft") && player.hasDiscoveredRecipe(recipe))
            player.undiscoverRecipe(recipe);
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
        if (!player.hasPermission("crazyeggs.craft") && event.getInventory().getResult().isSimilar(egg.eggItem())) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }
        if (!event.getInventory().contains(Material.EGG) || !event.getInventory().contains(egg.eggItem())) return;

        //It starts from index 1 to exclude the result slot
        for (int i = 1; i <= event.getInventory().getMatrix().length; i++) {
            ItemStack item = event.getInventory().getItem(i);

            if (item == null) continue;
            if (!isCrazyEgg(item)) continue;
            event.getInventory().setResult(new ItemStack(Material.AIR));

        }
    }
}