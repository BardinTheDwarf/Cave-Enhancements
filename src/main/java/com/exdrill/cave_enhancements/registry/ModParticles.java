package com.exdrill.cave_enhancements.registry;

import com.exdrill.cave_enhancements.CaveEnhancements;
import com.exdrill.cave_enhancements.particle.*;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class ModParticles {

    public static final SimpleParticleType SMALL_GOOP_DRIP = FabricParticleTypes.simple();
    public static final SimpleParticleType SHOCKWAVE = FabricParticleTypes.simple();
    public static final SimpleParticleType ROSE_QUARTZ_AURA = FabricParticleTypes.simple();
    public static final SimpleParticleType SOOTHING_NOTE = FabricParticleTypes.simple();
    public static final SimpleParticleType ROSE_CHIMES = FabricParticleTypes.simple();
    public static final SimpleParticleType AMETHYST_BLAST = FabricParticleTypes.simple();
    public static final SimpleParticleType HOVERING_NOTE = FabricParticleTypes.simple();
    public static final SimpleParticleType GOOP_EXPLOSION = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "small_goop_drip"), SMALL_GOOP_DRIP);
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "shockwave"), SHOCKWAVE);
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "rose_quartz_aura"), ROSE_QUARTZ_AURA);
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "soothing_note"), SOOTHING_NOTE);
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "rose_chimes"), ROSE_CHIMES);
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "amethyst_blast"), AMETHYST_BLAST);
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "hovering_note"), HOVERING_NOTE);
        Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(CaveEnhancements.MODID, "goop_explosion"), GOOP_EXPLOSION);
    }

    public static void registerClient() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.SMALL_GOOP_DRIP, SmallGoopDripParticle.SmallGoopDripFactory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.SHOCKWAVE, ShockwaveParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.ROSE_QUARTZ_AURA, RoseQuartzAuraParticle.RoseQuartzFactory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.SOOTHING_NOTE, SoothingNoteParticle.SoothingNoteFactory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.ROSE_CHIMES, RoseChimesParticle.RoseChimesFactory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.AMETHYST_BLAST, AmethystBlastParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.HOVERING_NOTE, HoveringNoteParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.GOOP_EXPLOSION, HugeExplosionParticle.Provider::new);
    }
}
