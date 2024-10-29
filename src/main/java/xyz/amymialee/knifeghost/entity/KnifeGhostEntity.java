package xyz.amymialee.knifeghost.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
import xyz.amymialee.knifeghost.KnifeGhost;
import xyz.amymialee.knifeghost.cca.KnivesComponent;
import xyz.amymialee.mialib.util.MMath;

import java.util.List;

public class KnifeGhostEntity extends HostileEntity implements SmartBrainOwner<KnifeGhostEntity>, RangedAttackMob {
    private static final TrackedData<Byte> KNIFE_FLAGS = DataTracker.registerData(KnifeGhostEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> CHARGING_KNIFE = DataTracker.registerData(KnifeGhostEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final int KNIFE_COUNT = 8;
    public final KnivesComponent knives;
    public int sunExposure = 0;
    @Environment(EnvType.CLIENT) public final List<Vec3d> knifeVel;
    @Environment(EnvType.CLIENT) public final List<Vec3d> knifePos;
    @Environment(EnvType.CLIENT) public final List<Vec3d> knifePrevPos;
    @Environment(EnvType.CLIENT) public final List<Float> knifeYaw;
    @Environment(EnvType.CLIENT) public final List<Float> knifePrevYaw;
    @Environment(EnvType.CLIENT) public final List<Float> knifePitch;
    @Environment(EnvType.CLIENT) public final List<Float> knifePrevPitch;
    @Environment(EnvType.CLIENT) public final List<Float> knifeRoll;
    @Environment(EnvType.CLIENT) public final List<Float> knifePrevRoll;
    @Environment(EnvType.CLIENT) public double prevCapeX;
    @Environment(EnvType.CLIENT) public double prevCapeY;
    @Environment(EnvType.CLIENT) public double prevCapeZ;
    @Environment(EnvType.CLIENT) public double capeX;
    @Environment(EnvType.CLIENT) public double capeY;
    @Environment(EnvType.CLIENT) public double capeZ;
    @Environment(EnvType.CLIENT) public float prevStrideDistance;
    @Environment(EnvType.CLIENT) public float strideDistance;

    public KnifeGhostEntity(EntityType<? extends KnifeGhostEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 10;
        this.knives = this.getComponent(KnivesComponent.KEY);
        this.knifeVel = DefaultedList.ofSize(KNIFE_COUNT, this.getPos());
        this.knifePos = DefaultedList.ofSize(KNIFE_COUNT, this.getPos());
        this.knifePrevPos = DefaultedList.ofSize(KNIFE_COUNT, this.getPos());
        this.knifeYaw = DefaultedList.ofSize(KNIFE_COUNT, 0f);
        this.knifePrevYaw = DefaultedList.ofSize(KNIFE_COUNT, 0f);
        this.knifePitch = DefaultedList.ofSize(KNIFE_COUNT, 0f);
        this.knifePrevPitch = DefaultedList.ofSize(KNIFE_COUNT, 0f);
        this.knifeRoll = DefaultedList.ofSize(KNIFE_COUNT, 0f);
        this.knifePrevRoll = DefaultedList.ofSize(KNIFE_COUNT, 0f);
    }

    public static DefaultAttributeContainer.Builder createGhostAttributes() {
        return createHostileAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (this.getChargingKnife() < 0 || this.getChargingKnife() >= 8) return;
        var projectile = EntityType.SPECTRAL_ARROW.create(this.getWorld());//KnifeGhost.KNIFE_ENTITY.create(this.getWorld());
        if (projectile == null) return;
        var pos = this.getKnifePos(this.getChargingKnife(), 0.5f);
        projectile.setPos(pos.x, pos.y, pos.z);
//        projectile.setStack(knife.copyWithCount(1));
        projectile.setVelocity(this, this.getPitch(), this.getYaw(), 0, 1.5F, 5.0F);
        projectile.setOwner(this);
        projectile.velocityDirty = true;
        this.getWorld().spawnEntity(projectile);
        this.setKnife(this.getChargingKnife(), false);
        this.getWorld().playSound(this, this.getBlockPos(), KnifeGhost.KNIFEGHOST_THROW, this.getSoundCategory(), 1.0f, 0.8f + this.getRandom().nextFloat() * 0.4f);
    }

    public Vec3d getKnifePos(int id, float delta) {
        var pos = this.getPos();
        var x = pos.x + Math.sin(Math.toDegrees(this.age + delta + (double) id / KNIFE_COUNT * 360)) * 0.5;
        var y = pos.y + this.getHeight() * 0.75;
        var z = pos.z - Math.cos(Math.toDegrees(this.age + delta + (double) id / KNIFE_COUNT * 360)) * 0.5;
        return new Vec3d(x, y, z);
    }

    @Override
    public void tick() {
        if (this.getWorld().isDay() && this.getWorld().isSkyVisible(this.getBlockPos())) {
//            this.sunExposure++;
            if (this.sunExposure > 100) this.damage(this.getDamageSources().inFire(), 600.0F);
        } else {
            this.sunExposure -= 4;
            if (this.sunExposure < 0) this.sunExposure = 0;
        }
        super.tick();
        if (this.getWorld().isClient) this.clientTick();
    }

    @Environment(EnvType.CLIENT)
    public void clientTick() {
        var yaw = MathHelper.lerpAngleDegrees(0.5f, this.prevHeadYaw, this.headYaw);
        var count = 0;
        for (var i = 0; i < 8; i++) if (this.hasKnife(i)) count++;
        for (var knife = 0; knife < KNIFE_COUNT; knife++) {
            this.knifePrevPos.set(knife, this.knifePos.get(knife));
            this.knifePrevYaw.set(knife, this.knifeYaw.get(knife));
            this.knifePrevPitch.set(knife, this.knifePitch.get(knife));
            this.knifePrevRoll.set(knife, this.knifeRoll.get(knife));
            var curVel = this.knifeVel.get(knife);
            var curPos = this.knifePos.get(knife);
            var curYaw = this.knifeYaw.get(knife);
            var curPitch = this.knifePitch.get(knife);
            var curRoll = this.knifeRoll.get(knife);
            var rotation = this.age / 6f + knife * (Math.PI / KNIFE_COUNT * 2);
            var frontage = -Math.cos(rotation + Math.toRadians(yaw));
            var x = Math.sin(rotation) * 1.45f;
            var y = 1.35 + frontage * 1.2f + Math.sin(Math.pow(knife + 4, 4) + this.age * 0.3) * 0.08;
            var z = Math.cos(rotation) * 1.45f;
            var speed = 0.012f;
            speed = speed * (1 + (8 - count) * 0.1f);
            if (this.isDead()) {
                x = curPos.getX();
                y = 0;
                z = curPos.getZ();
                speed = 0.08f;
            } else if (this.getChargingKnife() == knife) {
                var look = this.getRotationVector();
                x = look.x;
                y = this.getStandingEyeHeight() + look.y;
                z = look.z;
                speed = 0.12f;
            }
            var newVel = curVel.multiply(0.9f).add(new Vec3d(x - curPos.getX(), y - curPos.getY(), z - curPos.getZ()).multiply(speed));
            var newPos = curPos.add(newVel);
            var newYaw = -yaw - 90;
            var newPitch = 0f;
            var newRoll = MathHelper.cos((float) (this.age / 5f + knife * (Math.PI / 8f * 2))) * 45 * (1 + (8 - count) * 0.2f);
            if (this.isDead()) {
                newPitch += 90;
                newRoll = 90 * (knife % 2 == 0 ? 1 : -1);
            }
            this.knifeVel.set(knife, newVel);
            this.knifePos.set(knife, newPos);
            this.knifeYaw.set(knife, MathHelper.lerpAngleDegrees(0.1f, curYaw, newYaw));
            this.knifePitch.set(knife, MathHelper.lerpAngleDegrees(0.1f, curPitch, newPitch));
            this.knifeRoll.set(knife, MathHelper.lerpAngleDegrees(0.1f, curRoll, newRoll));
            if (this.getChargingKnife() == knife) {
                this.knifePitch.set(knife, MathHelper.lerpAngleDegrees(0.4f, curPitch, curPitch - 180f));
                this.knifeRoll.set(knife, MathHelper.lerpAngleDegrees(0.1f, curRoll, -45));
            }
        }
        this.updateCapeAngles();
    }

    @Environment(EnvType.CLIENT)
    private void updateCapeAngles() {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        var d = this.getX() - this.capeX;
        var e = this.getY() - this.capeY;
        var f = this.getZ() - this.capeZ;
        if (d > 10.0) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }
        if (f > 10.0) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }
        if (e > 10.0) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }
        if (d < -10.0) {
            this.capeX = this.getX();
            this.prevCapeX = this.capeX;
        }
        if (f < -10.0) {
            this.capeZ = this.getZ();
            this.prevCapeZ = this.capeZ;
        }
        if (e < -10.0) {
            this.capeY = this.getY();
            this.prevCapeY = this.capeY;
        }
        this.capeX += d * 0.25;
        this.capeZ += f * 0.25;
        this.capeY += e * 0.25;
        this.prevStrideDistance = this.strideDistance;
        float stride;
        if (this.isOnGround() && !this.isDead() && !this.isSwimming()) {
            stride = Math.min(0.1F, (float)this.getVelocity().horizontalLength());
        } else {
            stride = 0.0F;
        }
        this.strideDistance += (stride - this.strideDistance) * 0.4F;
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
                new NearbyLivingEntitySensor<KnifeGhostEntity>().setPredicate((target, entity) -> target instanceof PlayerEntity || target instanceof IronGolemEntity));
    }

    @Override
    public BrainActivityGroup<? extends KnifeGhostEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new AvoidSun<>(),
                new EscapeSun<>().cooldownFor(entity -> 20),
                new AvoidEntity<>().avoiding(entity -> entity instanceof IronGolemEntity),
                new AvoidEntity<KnifeGhostEntity>().avoiding(entity -> entity instanceof PlayerEntity).startCondition((g) -> g.dataTracker.get(KNIFE_FLAGS) == 0),
                new LookAtTarget<>().runFor(entity -> entity.getRandom().nextBetween(40, 300)),
                new MoveToWalkTarget<>());
    }

    @Override @SuppressWarnings("unchecked")
    public BrainActivityGroup<? extends KnifeGhostEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<KnifeGhostEntity>(
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextBetweenExclusive(30, 60))));
    }

    @Override
    public BrainActivityGroup<? extends KnifeGhostEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<KnifeGhostEntity>().invalidateIf((e, p) -> e.dataTracker.get(KNIFE_FLAGS) == 0),
                new InvalidateAttackTarget<>(),
                new KnifeAttack(),
                new StrafeTarget<KnifeGhostEntity>().stopStrafingWhen((e) -> e.getChargingKnife() == -1));
    }

    public boolean hasKnife(int id) {
        return MMath.getByteFlag(this.dataTracker.get(KNIFE_FLAGS), id);
    }

    public void setKnife(int id, boolean hasKnife) {
        this.dataTracker.set(KNIFE_FLAGS, MMath.setByteFlag(this.dataTracker.get(KNIFE_FLAGS), id, hasKnife));
    }

    public int getChargingKnife() {
        return this.dataTracker.get(CHARGING_KNIFE);
    }

    public void setChargingKnife(int id) {
        this.dataTracker.set(CHARGING_KNIFE, (byte) id);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(KNIFE_FLAGS, (byte) -1);
        builder.add(CHARGING_KNIFE, (byte) -1);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("knives", this.dataTracker.get(KNIFE_FLAGS));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(KNIFE_FLAGS, nbt.getByte("knives"));
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

    public static class KnifeAttack extends AnimatableRangedAttack<KnifeGhostEntity> {
        public KnifeAttack() {
            super(24);
        }

        @Override
        protected void start(KnifeGhostEntity entity) {
            LookTargetUtil.lookAt(entity, this.target);
            var idSet = new ObjectArrayList<Integer>();
            for (var i = 0; i < 8; i++) if (entity.hasKnife(i)) idSet.add(i);
            if (idSet.isEmpty()) return;
            var id = idSet.get(entity.getRandom().nextInt(idSet.size()));
            entity.setChargingKnife(id);
        }

        @Override
        protected void doDelayedAction(KnifeGhostEntity entity) {
            if (this.target == null) return;
            if (!BrainUtils.canSee(entity, this.target) || entity.squaredDistanceTo(this.target) > this.attackRadius) return;
            entity.shootAt(this.target, 1f);
            BrainUtils.setForgettableMemory(entity, MemoryModuleType.ATTACK_COOLING_DOWN, true, this.attackIntervalSupplier.apply(entity));
        }

        @Override
        protected void stop(KnifeGhostEntity entity) {
            super.stop(entity);
            entity.setChargingKnife(-1);
        }
    }
}