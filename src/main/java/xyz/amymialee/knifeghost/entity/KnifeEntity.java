package xyz.amymialee.knifeghost.entity;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.knifeghost.KnifeGhost;

public class KnifeEntity extends ProjectileEntity {
    public static final TrackedData<ItemStack> STACK = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    public static final TrackedData<Byte> KNIFE_ID = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<Integer> FLIGHT_TIME = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> STUCK_TIME = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> STICKY = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> RETURNING = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public float prevRoll;
    public float roll;

    public KnifeEntity(EntityType<? extends KnifeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onEntityHit(@NotNull EntityHitResult entityHitResult) {
        if (this.getWorld() instanceof ServerWorld world) {
            var owner = this.getOwner();
            var target = entityHitResult.getEntity();
            var damageSource = this.getDamageSources().create(KnifeGhost.KNIFE_DAMAGE, this, owner != null ? owner : this);
            if (owner instanceof LivingEntity livingEntity) livingEntity.onAttacking(target);
            var stack = this.dataTracker.get(STACK);
            var attributes = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            var base = 2f;
            if (stack.getItem() instanceof ToolItem tool) base += tool.getMaterial().getAttackDamage();
            var damage = attributes.applyOperations(base, EquipmentSlot.MAINHAND);
            target.timeUntilRegen = 0;
            if (target.damage(damageSource, (float) damage)) {
                if (target.getType() == EntityType.ENDERMAN) return;
                if (this.isOnFire()) target.setOnFireFor(5.0f);
                if (target instanceof LivingEntity livingTarget) {
                    EnchantmentHelper.onTargetDamaged(world, target, damageSource, stack);
                    if (livingTarget != owner && livingTarget instanceof PlayerEntity && owner instanceof ServerPlayerEntity serverOwner && !this.isSilent()) {
                        serverOwner.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                    }
                }
                this.playSound(KnifeGhost.KNIFEGHOST_IMPACT, 0.8F, 1.2f + 0.6f * this.random.nextFloat());
                this.getWorld().sendEntityStatus(this, (byte) 100);
            }
            this.deflect(ProjectileDeflection.SIMPLE, target, this.getOwner(), false);
            this.setVelocity(this.getVelocity().multiply(0.6));
        }
        this.setReturning(true);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), KnifeGhost.KNIFEGHOST_IMPACT, SoundCategory.PLAYERS, 0.8F, 0.6f + 0.5f * this.random.nextFloat());
        var state = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        if (state.isOf(Blocks.HONEY_BLOCK)) {
            this.setSticky(true);
            this.setStuckTime(80);
        } else if (state.isOf(Blocks.PUMPKIN)) {
            var direction = Direction.fromRotation(this.getYaw()).getOpposite();
            this.getWorld().playSound(null, blockHitResult.getBlockPos(), SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.2F);
            this.getWorld().setBlockState(blockHitResult.getBlockPos(), KnifeGhost.GHOST_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, direction), Block.NOTIFY_ALL_AND_REDRAW);
            this.getWorld().emitGameEvent(this, GameEvent.SHEAR, this.getPos());
            this.setStuckTime(1);
        } else {
            this.setStuckTime(12);
        }
        this.setPosition(blockHitResult.getPos().subtract(this.getVelocity()));
        this.getWorld().sendEntityStatus(this, (byte) 100);
        this.setVelocity(Vec3d.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > 2400) {
            this.discard();
            return;
        }
        if (this.isSticky()) {
            if (this.random.nextFloat() > 0.2f) {
                var x = (this.getX() - this.prevX) * this.random.nextFloat();
                var y = (this.getY() - this.prevY) * this.random.nextFloat();
                var z = (this.getZ() - this.prevZ) * this.random.nextFloat();
                this.getWorld().addParticle(ParticleTypes.FALLING_HONEY, this.getX(), this.getY(), this.getZ(), x, y, z);
            }
        }
        if (this.isFlying()) this.setFlightTime(this.getFlightTime() - 1);
        this.prevRoll = this.roll;
        if (!this.isStuck()) {
            if (this.isReturning()) {
                this.roll = MathHelper.lerp(0.1F, this.roll, 0.0F);
            } else {
                this.roll = this.roll - 40.0F;
            }
            var f = this.roll % 360.0F;
            if (f >= 180.0F) {
                this.roll -= 360.0F;
                this.prevRoll -= 360.0F;
            }
            if (f < -180.0F) {
                this.roll += 360.0F;
                this.prevRoll += 360.0F;
            }
        }
        var owner = this.getOwner();
        if (this.isStuck()) {
            this.setStuckTime(this.getStuckTime() - 1);
            if (this.getStuckTime() <= 0) {
                if (owner != null) this.setVelocity(owner.getEyePos().subtract(this.getPos()).normalize().multiply(0.2f));
                this.setReturning(true);
            }
        } else if (this.isReturning()) {
            if (owner == null || !owner.isAlive()) {
                if (this.isSticky()) this.dropStack(this.getStack(), 0.1F);
                this.discard();
                return;
            }
            this.noClip = true;
            var difference = owner.getEyePos().subtract(this.getPos());
            this.setVelocity(this.getVelocity().multiply(0.92).add(difference.normalize().multiply(0.12)));
            if (difference.length() < 0.8f) {
                if (owner instanceof KnifeGhostEntity ghost) ghost.setKnife(this.getKnifeId(), true);
                this.discard();
                return;
            }
            var velocity = this.getVelocity();
            var pos = this.getPos();
            var next = pos.add(velocity);
            this.updateRotation();
            this.setVelocity(velocity.multiply(0.99F));
            this.setPosition(next);
        } else {
            var velocity = this.getVelocity();
            this.checkBlockCollision();
            if (this.isTouchingWaterOrRain() || this.getWorld().getBlockState(this.getBlockPos()).isOf(Blocks.POWDER_SNOW)) this.extinguish();
            var pos = this.getPos();
            var next = pos.add(velocity);
            HitResult hit = this.getWorld().raycast(new RaycastContext(pos, next, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            if (hit.getType() != HitResult.Type.MISS) next = hit.getPos();
            var entityHit = ProjectileUtil.getEntityCollision(this.getWorld(), this, pos.subtract(velocity), next, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
            if (entityHit != null) hit = entityHit;
            if (hit instanceof EntityHitResult entityHitResult) {
                var entity = entityHitResult.getEntity();
                if (entity == owner) hit = null;
                if (entity instanceof PlayerEntity target && owner instanceof PlayerEntity player && !player.shouldDamagePlayer(target)) hit = null;
            }
            if (hit != null) {
                this.hitOrDeflect(hit);
                this.velocityDirty = true;
            }
            this.updateRotation();
            if (this.isTouchingWater()) for (var i = 0; i < 4; ++i) this.getWorld().addParticle(ParticleTypes.BUBBLE, next.x - velocity.x * 0.25, next.y - velocity.y * 0.25, next.z - velocity.z * 0.25, velocity.x, velocity.y, velocity.z);
            this.setVelocity(velocity.multiply(0.99F));
            this.applyGravity();
            this.setPosition(next);
        }
    }

    @Override @SuppressWarnings("SuspiciousNameCombination")
    protected void updateRotation() {
        if (!this.isReturning()) {
            super.updateRotation();
            return;
        }
        var vec3d = this.getVelocity().multiply(-1);
        var d = vec3d.horizontalLength();
        this.setPitch(updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI)));
        this.setYaw(updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI)));
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.isStuck() && !this.isReturning()) super.onCollision(hitResult);
    }

    @Override
    protected double getGravity() {
        return this.dataTracker.get(FLIGHT_TIME) <= 0 ? 0.04 : 0.01;
    }

    public ItemStack getStack() {
        return this.dataTracker.get(STACK);
    }

    public void setStack(ItemStack stack) {
        this.dataTracker.set(STACK, stack);
    }

    public byte getKnifeId() {
        return this.dataTracker.get(KNIFE_ID);
    }

    public void setKnifeId(byte id) {
        this.dataTracker.set(KNIFE_ID, id);
    }

    public int getFlightTime() {
        return this.dataTracker.get(FLIGHT_TIME);
    }

    public boolean isFlying() {
        return this.dataTracker.get(FLIGHT_TIME) > 0;
    }

    public void setFlightTime(int time) {
        this.dataTracker.set(FLIGHT_TIME, time);
    }

    public int getStuckTime() {
        return this.dataTracker.get(STUCK_TIME);
    }

    public boolean isStuck() {
        return this.dataTracker.get(STUCK_TIME) > 0;
    }

    public void setStuckTime(int time) {
        this.dataTracker.set(STUCK_TIME, time);
    }

    public boolean isSticky() {
        return this.dataTracker.get(STICKY);
    }

    public void setSticky(boolean sticky) {
        this.dataTracker.set(STICKY, sticky);
    }

    public boolean isReturning() {
        return this.dataTracker.get(RETURNING);
    }

    public void setReturning(boolean returning) {
        this.dataTracker.set(RETURNING, returning);
    }

    @Override
    protected void initDataTracker(DataTracker.@NotNull Builder builder) {
        builder.add(STACK, ItemStack.EMPTY);
        builder.add(KNIFE_ID, (byte) -1);
        builder.add(FLIGHT_TIME, 40);
        builder.add(STUCK_TIME, 0);
        builder.add(STICKY, false);
        builder.add(RETURNING, false);
    }
}