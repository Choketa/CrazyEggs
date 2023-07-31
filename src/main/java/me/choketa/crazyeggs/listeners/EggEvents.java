package me.choketa.crazyeggs.listeners;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.EggRecipe;
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

import java.util.HashSet;
import java.util.UUID;

import static me.choketa.crazyeggs.utils.EggUtils.*;


public class EggEvents implements Listener {
    CrazyEggs plugin;
    EggRecipe egg;

    HashSet<UUID> projectile = new HashSet<>();

    public EggEvents(CrazyEggs plugin) {
        this.plugin = plugin;
        this.egg = new EggRecipe(plugin);
    }



    //Makes the impact happen
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        //1/x chance
        int min = 1;
        int max = plugin.getConfig().getInt("spawn-egg-rarity");
        int random = (int) Math.floor(Math.random() * (max - min + 1) + min);
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();

        if (!projectile.contains(player.getUniqueId())) {
            return;
        }
        if (event.getHitEntity() == null) {
            projectile.remove(player.getUniqueId());
            return;
        }
        if (!(event.getHitEntity() instanceof LivingEntity)) {
            return;
        }

        String name = event.getHitEntity().getName().toUpperCase();

        if (event.getHitEntity() instanceof Enderman && plugin.getConfig().getBoolean("enable-enderman-effect")) {
            Bukkit.getWorld(player.getWorld().getName()).playEffect(event.getHitEntity().getLocation(),Effect.DRAGON_BREATH, 50);
        }

        Sound impactSound;
        Particle particle;

        if (plugin.getConfig().getString("sound-when-hit") == null ||
            plugin.getConfig().getString("sound-when-hit").isEmpty()){
            impactSound = null;
        }

        else {
            impactSound = Sound.valueOf(plugin.getConfig().getString("sound-when-hit"));
        }
        if (plugin.getConfig().getString("default-particle").isEmpty() ||
         plugin.getConfig().getString("default-particle") == null) {
            particle = null;
        }
        else {
            particle = Particle.valueOf(plugin.getConfig().getString("default-particle"));
        }

        LivingEntity entity = (LivingEntity) event.getHitEntity();

            entity.damage(plugin.getConfig().getInt("damage"));
            Bukkit.getWorld(entity.getWorld().getName()).playSound(event.getHitEntity(), impactSound, 0.5f, 1f);
            Bukkit.getWorld(entity.getWorld().getName()).spawnParticle(particle,entity.getLocation(),plugin.getConfig().getInt("particle-count"));

            Vector direction = event.getEntity().getVelocity().multiply(plugin.getConfig().getDouble("velocity-multiplier"));
            event.getHitEntity().setVelocity(direction.setY(plugin.getConfig().getDouble("velocity-set-y")));
            projectile.remove(player.getUniqueId());



        if (event.getHitEntity().isDead() && random == max && !(event.getHitEntity() instanceof Player)
            && plugin.getConfig().getBoolean("enable-spawn-eggs-drop")) {
                Material eggMaterial = Material.matchMaterial(name + "_SPAWN_EGG");
                if (eggMaterial == null) return;
                ItemStack eggItem = new ItemStack(eggMaterial);

                Bukkit.getWorld(player.getWorld().getName()).dropItem(event.getHitEntity().getLocation(), eggItem);
            }
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
        if (!isCrazyEgg(plugin,item)) return;
        if (!player.hasPermission("crazyeggs.use")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED+"[CrazyEggs] You are not allowed to use the egg!");
            return;
        }

        projectile.add(player.getUniqueId());
    }
    //Disables the egg hatching
    @EventHandler
    public void onHatch(PlayerEggThrowEvent event) {
        Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (!isCrazyEgg(plugin, item) && !projectile.contains(player.getUniqueId())) return;
        byte by = 0;
        event.setHatching(false);
        event.setNumHatches(by);

    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NamespacedKey recipe = new NamespacedKey(plugin,"egg");
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
        if (!player.hasPermission("crazyeggs.craft") && event.getInventory().getResult().isSimilar(egg.eggItem())) {
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }
        if (!event.getInventory().contains(Material.EGG) || !event.getInventory().contains(egg.eggItem())) return;

        //It starts from index 1 to exclude the result slot
        for (int i = 1; i <= event.getInventory().getMatrix().length; i++) {
            ItemStack item = event.getInventory().getItem(i);

            if (item == null) continue;
            if (!isCrazyEgg(plugin,item)) continue;
            event.getInventory().setResult(new ItemStack(Material.AIR));

            }
        }
    }
