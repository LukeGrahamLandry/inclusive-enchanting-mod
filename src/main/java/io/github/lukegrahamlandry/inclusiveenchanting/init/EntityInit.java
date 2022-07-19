package io.github.lukegrahamlandry.inclusiveenchanting.init;

import io.github.lukegrahamlandry.inclusiveenchanting.CustomTridentEntity;
import io.github.lukegrahamlandry.inclusiveenchanting.InclusiveEnchanting;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityInit {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, InclusiveEnchanting.MOD_ID);

    public static final RegistryObject<EntityType<CustomTridentEntity>> CUSTOM_TRIDENT = ENTITY_TYPES.register("enchanted_trident",
            () -> EntityType.Builder.of((EntityType.EntityFactory<CustomTridentEntity>) CustomTridentEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(20)
                    .build(new ResourceLocation(InclusiveEnchanting.MOD_ID, "enchanted_trident").toString()));
}
