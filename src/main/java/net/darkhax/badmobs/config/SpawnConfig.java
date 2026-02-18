package net.darkhax.badmobs.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import java.util.List;
import java.util.Collections;

public class SpawnConfig {

    private final ForgeConfigSpec.BooleanValue allowNormalSpawn;
    private final ForgeConfigSpec.BooleanValue allowSpawners;
    private final ForgeConfigSpec.BooleanValue allowSpawnEggs;
    private final ForgeConfigSpec.BooleanValue allowConversions;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> bannedDimensions;

    public SpawnConfig(ResourceLocation id, ForgeConfigSpec.Builder builder) {
        builder.comment("Options for the " + id.getNamespace() + " mod.");
        builder.push(id.getNamespace());
        builder.comment("Spawning options for " + id);
        builder.push(id.getPath());

        this.allowNormalSpawn = builder.comment("Should the entity be allowed to spawn normally?").define("allowNormalSpawning", true);
        this.allowSpawners = builder.comment("Should spawners be able to spawn the entity?").define("allowSpawners", true);
        this.allowSpawnEggs = builder.comment("Should spawn eggs be able to spawn the entity?").define("allowSpawnEggs", true);
        this.allowConversions = builder.comment("Should the entity spawn via mob conversion? i.e. villager -> zombie").define("allowConversions", true);
        
        // NEW OPTION: List of prohibited dimensions
        this.bannedDimensions = builder.comment("List of dimensions where this mob is banned. Example: [\"minecraft:overworld\"]")
                .defineList("bannedDimensions", Collections.emptyList(), obj -> obj instanceof String);

        builder.pop();
        builder.pop();
    }

    public boolean canSpawn (Level level, MobSpawnType reason) {
        // We check if the current dimension is in the blacklist
        String currentDim = level.dimension().location().toString();
        if (this.bannedDimensions.get().contains(currentDim)) {
            return false;
        }

        if (reason == MobSpawnType.SPAWNER) return this.allowSpawners.get();
        if (reason == MobSpawnType.SPAWN_EGG) return this.allowSpawnEggs.get();
        if (reason == MobSpawnType.CONVERSION) return this.allowConversions.get();

        return this.allowNormalSpawn.get();
    }
}