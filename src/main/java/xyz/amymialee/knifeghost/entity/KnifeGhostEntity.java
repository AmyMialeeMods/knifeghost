package xyz.amymialee.knifeghost.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableRangedAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.AvoidSun;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.AvoidEntity;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.EscapeSun;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.StrafeTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.knifeghost.KnifeGhost;
import xyz.amymialee.mialib.util.MMath;

import java.util.List;

public class KnifeGhostEntity extends HostileEntity implements SmartBrainOwner<KnifeGhostEntity>, RangedAttackMob {
    private static final TrackedData<Byte> KNIFE_FLAGS = DataTracker.registerData(KnifeGhostEntity.class, TrackedDataHandlerRegistry.BYTE);
    private final DefaultedList<ItemStack> knives = DefaultedList.ofSize(8, ItemStack.EMPTY);

    public KnifeGhostEntity(EntityType<? extends KnifeGhostEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
        this.experiencePoints = 10;
    }

    public static DefaultAttributeContainer.Builder createGhostAttributes() {
        return createHostileAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        var idSet = new ObjectArrayList<Integer>();
        for (var i = 0; i < 8; i++) if (this.hasKnife(i)) idSet.add(i);
        if (idSet.isEmpty()) return;
        var id = idSet.get(this.getRandom().nextInt(idSet.size()));
        var knife = this.knives.get(id);
        if (knife.isEmpty()) return;
        var projectile = KnifeGhost.KNIFE_ENTITY.create(this.getWorld());
        if (projectile == null) return;
        var pos = this.getKnifePos(id, 0.5f);
        projectile.setPos(pos.x, pos.y, pos.z);
        projectile.setStack(knife.copyWithCount(1));
        projectile.setVelocity(this, this.getPitch(), 0, 0, 1.5F, 5.0F);
        projectile.setOwner(this);
        projectile.velocityDirty = true;
        this.getWorld().spawnEntity(projectile);
        this.setKnife(id, false);
    }

    public Vec3d getKnifePos(int id, float delta) {
        var pos = this.getPos();
        var x = pos.x + Math.sin(Math.toDegrees(this.age + delta + id / 8f * 360)) * 0.5;
        var y = pos.y + this.getHeight() * 0.75;
        var z = pos.z - Math.cos(Math.toDegrees(this.age + delta + id / 8f * 360)) * 0.5;
        return new Vec3d(x, y, z);
    }

    @Override
    public void tickMovement() {
        if (!this.isOnGround() && this.getVelocity().y < 0.0) this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        super.tickMovement();
    }

    @Override
    public List<? extends ExtendedSensor<? extends KnifeGhostEntity>> getSensors() {
        return ObjectArrayList.of(
                new NearbyPlayersSensor<>(),
                new NearbyLivingEntitySensor<KnifeGhostEntity>().setPredicate((target, entity) -> target instanceof IronGolemEntity));
    }

    @Override
    public BrainActivityGroup<? extends KnifeGhostEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new AvoidSun<>(),
                new EscapeSun<>().cooldownFor(entity -> 20),
                new AvoidEntity<>().avoiding(entity -> entity instanceof IronGolemEntity),
                new LookAtTarget<>().runFor(entity -> entity.getRandom().nextBetween(40, 300)),
                new StrafeTarget<>(),
                new MoveToWalkTarget<>());
    }

    @Override @SuppressWarnings("unchecked")
    public BrainActivityGroup<? extends KnifeGhostEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<KnifeGhostEntity>(
                        new TargetOrRetaliate<>().attackablePredicate(entity -> entity instanceof PlayerEntity),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextBetweenExclusive(30, 60))));
    }

    @Override
    public BrainActivityGroup<? extends KnifeGhostEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new KnifeAttack<>()
        );
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        var list = new NbtList();
        this.knives.forEach((itemStack) -> list.add(itemStack.encode(this.getRegistryManager())));
        nbt.put("knives", list);
        nbt.putByte("knifeFlags", this.dataTracker.get(KNIFE_FLAGS));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.knives.clear();
        for (var nbtElement : nbt.getList("knives", NbtElement.COMPOUND_TYPE)) this.knives.add(ItemStack.fromNbtOrEmpty(this.getRegistryManager(), (NbtCompound) nbtElement));
        this.dataTracker.set(KNIFE_FLAGS, nbt.getByte("knifeFlags"));
    }

    public boolean hasKnife(int id) {
        return MMath.getByteFlag(this.dataTracker.get(KNIFE_FLAGS), id);
    }

    public void setKnife(int id, boolean hasKnife) {
        this.dataTracker.set(KNIFE_FLAGS, MMath.setByteFlag(this.dataTracker.get(KNIFE_FLAGS), id, hasKnife));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(KNIFE_FLAGS, (byte) 0);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.knives.replaceAll(ignored -> world.getRandom().nextInt(100) != 0 ? KnifeGhost.KNIFE.getDefaultStack() : KnifeGhost.getRandomKnife(world.getRandom()));
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return KnifeGhost.KNIFEGHOST_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return KnifeGhost.KNIFEGHOST_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return KnifeGhost.KNIFEGHOST_DEATH;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0F;
    }

    @Override
    protected void mobTick() {
        this.tickBrain(this);
    }

    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }

    public static class KnifeAttack<E extends LivingEntity & RangedAttackMob> extends AnimatableRangedAttack<E> {
        public KnifeAttack() {
            super(12);
        }

        @Override
        protected void start(E entity) {
            LookTargetUtil.lookAt(entity, this.target);
        }

        @Override
        protected void doDelayedAction(E entity) {
            if (this.target == null) return;
            if (!BrainUtils.canSee(entity, this.target) || entity.squaredDistanceTo(this.target) > this.attackRadius) return;
            entity.shootAt(this.target, 1f);
            BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
        }
    }
}