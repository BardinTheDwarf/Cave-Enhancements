package com.teamabode.cave_enhancements.entity.dripstone_tortoise;

import com.teamabode.cave_enhancements.entity.dripstone_tortoise.goals.DripstoneTortoiseAttackGoal;
import com.teamabode.cave_enhancements.entity.dripstone_tortoise.goals.DripstoneTortoiseOccasionalStompGoal;
import com.teamabode.cave_enhancements.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DripstoneTortoise extends Animal implements NeutralMob {
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(DripstoneTortoise.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(10, 22);

    public final AnimationState stompingAnimationState = new AnimationState();
    private int occasionalStompCooldown;
    @Nullable private UUID persistentAngerTarget;

    public DripstoneTortoise(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
        this.setOccasionalStompCooldown(5);
        this.xpReward = 15;
    }

    //NBT
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        addPersistentAngerSaveData(compound);
        compound.putInt("OccasionalStompCooldown", this.getOccasionalStompCooldown());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        readPersistentAngerSaveData(this.level, compound);
        this.setOccasionalStompCooldown(compound.getInt("OccasionalStompCooldown"));
    }

    public int getOccasionalStompCooldown() {
        return occasionalStompCooldown;
    }

    public void setOccasionalStompCooldown(int value) {
        occasionalStompCooldown = value;
    }

    // Sounds
    protected SoundEvent getDeathSound() {
        return ModSounds.ENTITY_DRIPSTONE_TORTOISE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ENTITY_DRIPSTONE_TORTOISE_HURT;
    }

    protected SoundEvent getAmbientSound() {
        return ModSounds.ENTITY_DRIPSTONE_TORTOISE_IDLE;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundEvent sound = ModSounds.ENTITY_DRIPSTONE_TORTOISE_STEP;
        this.playSound(sound, 0.15F, 1.0F);
    }

    // Goals
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(1, new DripstoneTortoiseAttackGoal(this));
        this.goalSelector.addGoal(2, new DripstoneTortoiseOccasionalStompGoal(this));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.5D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createDripstoneTortoiseAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.125D)
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ARMOR, 5)
                .add(Attributes.ARMOR_TOUGHNESS, 2)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 3);
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource == DamageSource.STALAGMITE || damageSource == DamageSource.FALLING_STALACTITE || damageSource.isProjectile() || damageSource.getEntity() instanceof DripstoneTortoise) return true;
        return super.isInvulnerableTo(damageSource);
    }

    public void aiStep() {
        if (tickCount % 20 == 0 && this.getOccasionalStompCooldown() > 0) {
            this.setOccasionalStompCooldown(this.getOccasionalStompCooldown() - 1);
        }
        super.aiStep();
    }

    public boolean canBeLeashed(Player player) {
        return false;
    }

    // Spawn Placement
    public static boolean isDarkEnoughToSpawn(ServerLevelAccessor serverLevelAccessor, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevelAccessor.getBrightness(LightLayer.SKY, blockPos) > randomSource.nextInt(32)) {
            return false;
        } else {
            DimensionType dimensionType = serverLevelAccessor.dimensionType();
            int i = dimensionType.monsterSpawnBlockLightLimit();
            if (i < 15 && serverLevelAccessor.getBrightness(LightLayer.BLOCK, blockPos) > i) {
                return false;
            } else {
                int j = serverLevelAccessor.getLevel().isThundering() ? serverLevelAccessor.getMaxLocalRawBrightness(blockPos, 10) : serverLevelAccessor.getMaxLocalRawBrightness(blockPos);
                return j <= dimensionType.monsterSpawnLightTest().sample(randomSource);
            }
        }
    }

    public static boolean checkDripstoneTortoiseSpawnRules(EntityType<? extends DripstoneTortoise> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        return serverLevelAccessor.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(serverLevelAccessor, blockPos, randomSource) && checkMobSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
    }

    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int remainingPersistentAngerTime) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, remainingPersistentAngerTime);
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID persistentAngerTarget) {
        this.persistentAngerTarget = persistentAngerTarget;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    public void summonPike(double x, double z, double minY, double maxY){
        BlockPos pos = new BlockPos(x, maxY, z);
        boolean finishedCalculation = false;
        double d = 0.0D;

        while(pos.getY() >= Mth.floor(minY) - 1) {
            BlockPos belowPos = pos.below();
            BlockState state = level.getBlockState(belowPos);
            if (state.isFaceSturdy(level, belowPos, Direction.UP)) {
                if (!level.isEmptyBlock(pos)) {
                    BlockState blockState2 = level.getBlockState(pos);
                    VoxelShape voxelShape = blockState2.getCollisionShape(level, pos);
                    if (!voxelShape.isEmpty()) {
                        d = voxelShape.max(Direction.Axis.Y);
                    }
                }
                finishedCalculation = true;
                break;
            }

            pos = pos.below(1);
        }

        if (finishedCalculation) {
            DripstonePike dripstonePike = new DripstonePike(level, x, this.getY() + d, z, this);
            level.addFreshEntity(dripstonePike);
        }
    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }
}
