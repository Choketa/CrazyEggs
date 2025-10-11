package me.choketa.crazyeggs.eggs;

import me.choketa.crazyeggs.CrazyEggs;
import me.choketa.crazyeggs.eggs.eggs.PluginEgg;
import me.choketa.crazyeggs.utils.Pair;
import org.bukkit.*;
import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.choketa.crazyeggs.CrazyEggs.RANDOM;
import static me.choketa.crazyeggs.utils.ColorUtils.format;

public class EggListener implements Listener {
    private final CrazyEggs plugin;
    private final EggManager manager;
    private final HashMap<UUID, HashMap<PluginEgg, Long>> cooldown;

    public EggListener() {
        this.plugin = CrazyEggs.getPlugin();
        this.manager = EggManager.getEggManager();
        cooldown = new HashMap<>();
    }

    //Makes the damage-type impact to happen
    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;

        if (!(event.getHitEntity() instanceof LivingEntity hitEntity))
            return;
        PluginEgg egg = manager.getEggByPDC(event.getEntity());
        if (egg == null) return;
        if (egg.getBoolean("should-explode")) return;

        World world = hitEntity.getWorld();
        Location location = hitEntity.getLocation();

        List<Pair<Sound, Pair<Float, Float>>> impactSounds = egg.getImpactSounds();
        List<Pair<Particle, Integer>> particles = egg.getParticles();
        List<PotionEffect> potionEffects = egg.getPotionEffects();

        hitEntity.damage(egg.get("damage"));

        if (impactSounds != null) {
            for (Pair<Sound, Pair<Float, Float>> pair : impactSounds)
                world.playSound(hitEntity, pair.a(), pair.b().a(), pair.b().a());
        }
        if (particles != null) {
            for (Pair<Particle, Integer> particle : particles)
                world.spawnParticle(particle.a(), location, particle.b());
        }
        if (potionEffects != null) {
            for (PotionEffect effect : potionEffects)
                hitEntity.addPotionEffect(effect);
        }

        Vector direction = event.getEntity().getVelocity().multiply(egg.getDouble("velocity-multiplier"));
        hitEntity.setVelocity(direction.setY(egg.getDouble("velocity-set-y")));
        hitEntity.setVelocity(direction);

        //1/x chance
        if (!egg.getBoolean("enable-spawn-eggs-drop")) return;
        String rarity = egg.get("spawn-egg-rarity");
        double percentage = Double.parseDouble(rarity.replace("%", ""));
        boolean isLucky = RANDOM.nextDouble(0, 100) < percentage;

        if (hitEntity.isDead() && isLucky && !(hitEntity instanceof Player)
                && plugin.getConfig().getBoolean("enable-spawn-eggs-drop")) {
            String name = hitEntity.getName().toUpperCase();
            Material eggMaterial = Material.matchMaterial(name + "_SPAWN_EGG");
            if (eggMaterial == null) return;
            ItemStack eggItem = new ItemStack(eggMaterial);

            world.dropItem(location, eggItem);
        }
    }

    //Makes the explosion-type impact to happen
    @EventHandler
    public void onExplosionHit(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player))
            return;
        Projectile proj = event.getEntity();
        PluginEgg egg = manager.getEggByPDC(proj);
        if (egg == null) return;
        if (!egg.getBoolean("should-explode")) return;

        float power = egg.getFloat("power");
        player.getWorld().createExplosion(proj.getLocation(), power, egg.get("should-set-fire"));
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player))
            return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (!(event.getEntity() instanceof Egg)) {
            return;
        }
        PluginEgg egg = manager.getEggByPDC(item);
        if (egg == null) return;
        if (!player.hasPermission("crazyeggs."+egg.getSimpleName()+".use")) {
            event.setCancelled(true);
            player.sendMessage("crazyeggs."+egg.getSimpleName()+".use");
            player.sendMessage(format("&c[CrazyEggs] You are not allowed to use this egg!"));
            return;
        }
        if (player.hasPermission("crazyeggs."+egg.getSimpleName()+".bypass.cooldown") || egg.getInteger("cooldown") <= 0) return;
        UUID id = player.getUniqueId();
        if (!cooldown.containsKey(id)) {
            HashMap<PluginEgg, Long> newMap = new HashMap<>();
            newMap.put(egg, System.currentTimeMillis());
            cooldown.put(id, newMap);
        }
        else {
            HashMap<PluginEgg, Long> map = cooldown.get(id);
            long timeElapsed = System.currentTimeMillis() - map.get(egg);
            int cooldowns = plugin.getConfig().getInt("cooldown");
            if (timeElapsed >= (cooldowns * 1000L)) {
                map.put(egg, System.currentTimeMillis());
                cooldown.put(player.getUniqueId(), map);
            }

            else {
                player.sendMessage(format("&c[CrazyEggs] You are in a cooldown. &e"+((cooldowns*1000L - timeElapsed)/1000)+" &cmore seconds."));
                event.setCancelled(true);
            }
        }
    }

    //Disables the egg hatching
    @EventHandler
    public void onHatch(PlayerEggThrowEvent event) {
        if (manager.getEggByPDC(event.getEgg()) == null) return;
        event.setHatching(false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (PluginEgg egg : manager.getEggs()) {
            NamespacedKey recipe = egg.getKey();
            if (!player.hasPermission("crazyeggs."+egg.getSimpleName()+".craft") && player.hasDiscoveredRecipe(recipe)) {
                player.undiscoverRecipe(recipe);
                continue;
            }
            if (!player.hasDiscoveredRecipe(recipe))
             player.discoverRecipe(recipe);
        }
    }

    //Disables crafting with the eggs or if player has no permission
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getInventory().getHolder();
        if (player == null) return;
        ItemStack result = event.getInventory().getResult();
        if (result == null) return;
        if (!event.getInventory().contains(Material.EGG)) return;

        for (PluginEgg egg : manager.getEggs()) {
            if (!event.getInventory().contains(egg.getEggItem())) continue;
            if (!result.isSimilar(egg.getEggItem())) continue;
            if (player.hasPermission("crazyeggs." + egg.getSimpleName() + ".craft")) continue;
            event.getInventory().setResult(new ItemStack(Material.AIR));
            return;
        }
    }
}
