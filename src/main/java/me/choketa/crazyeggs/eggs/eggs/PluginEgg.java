package me.choketa.crazyeggs.eggs.eggs;

import me.choketa.crazyeggs.eggs.EggPermissions;
import me.choketa.crazyeggs.eggs.EggRecipe;
import me.choketa.crazyeggs.utils.ColorUtils;
import me.choketa.crazyeggs.utils.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.choketa.crazyeggs.CrazyEggs.getPlugin;

public class PluginEgg {
    public final boolean isOld;
    private final String name;
    private final String simpleName;
    private ItemStack eggItem;
    private final File file;
    private YamlConfiguration customFile;
    private final Map<String, Object> cache;
    private final NamespacedKey key;
    private List<Pair<Sound, Pair<Float, Float>>>impactSounds;
    private List<Pair<Particle, Integer>> particles;
    private List<PotionEffect> potionEffects;

    public PluginEgg(String name) {
        this.name = name;
        this.simpleName = name.replace("_","").toLowerCase();
        this.file = new File(getPlugin().getDataFolder() + "/eggs", name + ".yml");
        this.isOld = file.exists();
        if (!isOld) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                getPlugin().getLogger().warning("Unable to create file for " + name);
            }
        }
        cache = new HashMap<>();
        customFile = YamlConfiguration.loadConfiguration(file);
        customFile.options().copyDefaults(true);
        if (!isOld)
            setDefaults();
        save();
        for (String str : customFile.getKeys(false))
            cache.put(str, get(str));

        key = new NamespacedKey(getPlugin(), "crazyeggs" + name);
        new EggPermissions(this);
    }
    public void initializeRecipe() {
        if (getBoolean("is-craftable")) new EggRecipe(this);
    }
    public <T> T get(String path) {
        return (T) cache.getOrDefault(path, (T) customFile.get(path));
    }
    public float getFloat(String path) {
        return (float) (customFile.getDouble(path));
    }
    public boolean getBoolean(String path) {
        return customFile.getBoolean(path);
    }

    public FileConfiguration save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cache.clear();
        for (String str : customFile.getKeys(false))
            cache.put(str, get(str));
        return customFile;
    }

    public FileConfiguration reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
        cache.clear();
        for (String str : customFile.getKeys(false))
            cache.put(str, get(str));
        return customFile;
    }

    public void setDefaults() {
        set("display-name",
                "&#b32222&l" + name.replace('_', ' '),
                Collections.singletonList("The name of the egg item"));

        set("lore",
                List.of("&cThis is quite the egg"),
                List.of("The lore of the egg item. Keep the dash only if you want no lore"));

        set("is-craftable",
                true,
                Collections.singletonList("Determines whether the egg is craftable or not"));
        set("recipe",
                List.of("EEE", "ETE", "EEE"),
                Collections.singletonList("The crafting layout for the recipe"));
        set("materials",
                null,
                Collections.singletonList("The materials that correspond to each letter"));
        set("materials.E", "EGG");
        set("materials.T", "TNT");

        set("upon-craft-amount",
                4,
                Collections.singletonList("Determines the amount of eggs given upon crafting"));

        set("damage",
                15.0,
                Collections.singletonList("The damage inflicted upon an entity when hit"));

        set("velocity-multiplier",
                0.45,
                Collections.singletonList("Velocity of the hit entity upon impact. Feel free to experiment"));
        set("velocity-set-y",
                0.5);

        set("enable-spawn-eggs-drop",
                true,
                Collections.singletonList("Determines if a living entity will drop their corresponding spawn egg"));

        set("spawn-egg-rarity",
                "10.0%",
                Collections.singletonList("Determines the probability that a spawn egg will be dropped"));

        set("should-explode",
                false,
                Collections.singletonList("Determines whether the egg explodes on impact"));

        set("power",
                8.0,
                Collections.singletonList("Determines the power of the explosion caused by the egg (For reference, 4 is the power of regular TNT)"));

        set("cooldown",
                10,
                Collections.singletonList("Cooldown for the egg. Input in seconds"));

        set("should-set-fire",
                false,
                Collections.singletonList("Determines whether explosions caused by the Destruction Egg should set nearby blocks on fire"));

        set("impact-particles",
                List.of("DAMAGE_INDICATOR,5")
                , List.of("The particles that should be spawned when the egg hits an entity",
                        "Parameters: Particle name, number of particles"));

        set("impact-sounds",
                List.of("ENTITY_LIGHTNING_BOLT_IMPACT,1.0,1.0")
                ,List.of("The sounds played when the egg hits an entity",
                        "Parameters: Sound name, volume, pitch"));

        set("set-glint",
                true,
                Collections.singletonList("Determines whether the egg is decorated with an enchantment glint"));

        set("potion-effects",
                List.of(new PotionEffect(PotionEffectType.SLOWNESS, 100, 0, true, true).serialize())
                ,Collections.singletonList("The potion effects to apply on entity upon impact"));
        set("custom-model-data", -1, Collections.singletonList("For resourcepacks"));
    }

    private List<Component> adaptLore(List<String> lore) {
        List<Component> actualLore = new ArrayList<>();
        for (String str : lore)
            actualLore.add(ColorUtils.format(str));
        return actualLore;
    }

    public ItemStack getEggItem() {
        if (eggItem == null) {
            this.eggItem = new ItemStack(Material.EGG);
            eggItem.editMeta(meta -> {
                meta.itemName(ColorUtils.format(get("display-name")));
                List<String> lore = get("lore");
                if (lore != null && !lore.isEmpty())
                    meta.lore(adaptLore(lore));
                if (get("set-glint")) {
                    meta.addEnchant(Enchantment.UNBREAKING, 1, false);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
                int customModelData = get("custom-model-data");
                if (customModelData != -1)
                    meta.setCustomModelData(customModelData);
                meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            });
        }
        return eggItem.clone();
    }

    public void set(@NotNull String path, Object obj, @Nullable List<String> description) {
        customFile.set(path, obj);
        if (description != null)
            customFile.setComments(path, description);
    }
    public void set(@NotNull String path, Object obj) {
        customFile.set(path, obj);
    }

    //e.g. Crazy_eggs
    public String getName() {
        return name;
    }
    //e.g crazyeggs
    public String getSimpleName() {
        return simpleName;
    }
    //E.g &cCrazy Eggs!!!!!!!
    public String getDisplayName() {
        return get("display-name");
    }
    public NamespacedKey getKey() {
        return key;
    }
    public double getDouble(String path) {
        return get(path);
    }
    public FileConfiguration getConfig() {
        return customFile;
    }

    public int getInteger(String path) {
        return get(path);
    }

    public List<Pair<Sound, Pair<Float, Float>>> getImpactSounds() {
        if (impactSounds == null) {
            List<String> soundString = get("impact-sounds");
            if (soundString != null && !soundString.isEmpty()) {
                impactSounds = new ArrayList<>();
                for (String s : soundString) {
                    String[] process = s.split(",");
                    impactSounds.add(new Pair<>(Sound.valueOf(process[0]),
                            new Pair<>(Float.parseFloat(process[1]), Float.parseFloat(process[2]))));
                }
            }
        }
        return impactSounds;
    }

    public List<Pair<Particle, Integer>> getParticles() {
        if (particles == null) {
            List<String> particleString = get("impact-particles");
            if (particleString != null && !particleString.isEmpty()) {
                particles = new ArrayList<>();
                for (String s : particleString) {
                    String[] process = s.split(",");
                    particles.add(new Pair<>(Particle.valueOf(process[0]), Integer.parseInt(process[1])));
                }
            }
        }
        return particles;
    }

    public List<PotionEffect> getPotionEffects() {
        if (potionEffects == null) {
            potionEffects = new ArrayList<>();
            List<Map<String, Object>> potions = get("potion-effects");
            for (Map<String, Object> toAdd : potions)
                potionEffects.add(new PotionEffect(toAdd));
        }
        return potionEffects;
    }

}