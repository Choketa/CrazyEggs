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
        //1/x chance
        int min = 1;
        int max = plugin.getConfig().getInt("spawn-egg-rarity");
        int random = (int) Math.floor(Math.random() * (max - min + 1) + min);
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity().getShooter();

        if (!isCrazyEgg(plugin, event.getEntity())) return;
        if (event.getHitEntity() == null) {
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
            entity.setVelocity(direction.setY(plugin.getConfig().getDouble("velocity-set-y")));
            entity.setVelocity(direction);



        if (event.getHitEntity().isDead() && random == max && !(event.getHitEntity() instanceof Player)
            && plugin.getConfig().getBoolean("enable-spawn-eggs-drop")) {
                Material eggMaterial = Material.matchMaterial(name + "_SPAWN_EGG");
                if (eggMaterial == null) return;
                ItemStack eggItem = new ItemStack(eggMaterial);

                Bukkit.getWorld(player.getWorld().getName()).dropItem(entity.getLocation(), eggItem);
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
        }

    }
    //Disables the egg hatching
    @EventHandler
    public void onHatch(PlayerEggThrowEvent event) {
        final ItemStack item = event.getEgg().getItem();
        if (!isCrazyEgg(plugin, item)) return;
        event.setHatching(false);
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
