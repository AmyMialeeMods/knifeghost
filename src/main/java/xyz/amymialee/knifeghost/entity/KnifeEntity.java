package xyz.amymialee.knifeghost.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class KnifeEntity extends ProjectileEntity {
    public static final TrackedData<ItemStack> STACK = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    public static final TrackedData<Integer> FLIGHT_TIME = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> STUCK = DataTracker.registerData(KnifeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public KnifeEntity(EntityType<? extends KnifeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean shouldRender(double distance) {
        var d = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(d)) d = 4.0;
        d *= 64.0;
        return distance < d * d;
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return true;
    }

    @Override
    protected double getGravity() {
        return this.dataTracker.get(FLIGHT_TIME) <= 0 ? 0.05 : 0.01;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (this.isNotStuck()) super.onCollision(hitResult);
    }

//    @Override
//    protected void onEntityHit(@NotNull EntityHitResult entityHitResult) {
//        if (this.getWorld() instanceof ServerWorld world) {
//            var owner = this.getOwner();
//            var target = entityHitResult.getEntity();
//            var targetRot = target.getRotationVector(target.getPitch(), target.getYaw());
//            var selfRot = this.getRotationVector(this.getPitch(), this.getYaw());
//            var angle = Math.toDegrees(Math.acos(targetRot.dotProduct(selfRot)));
//            var back = angle < KingsOptions.DAGGER_BACK_ANGLE.get();
//            var damageSource = this.getDamageSources().create(back ? KingsEntities.DAGGER_BACK_DAMAGE : KingsEntities.DAGGER_DAMAGE, this, owner != null ? owner : this);
//            if (owner instanceof LivingEntity livingEntity) livingEntity.onAttacking(target);
//            var stack = this.dataTracker.get(STACK);
//            var attributes = stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
//            var base = 2f;
//            if (stack.getItem() instanceof ToolItem tool) base += tool.getMaterial().getAttackDamage();
//            var damage = attributes.applyOperations(base, EquipmentSlot.MAINHAND);
//            target.timeUntilRegen = 0;
//            if (target.damage(damageSource, (this.isSmall() ? KingsOptions.DAGGER_MULTI_DAMAGE.get() : 1) * (back ? KingsOptions.DAGGER_BACK_DAMAGE.get() : KingsOptions.DAGGER_FRONT_DAMAGE.get()) * EnchantmentHelper.getDamage(world, stack, target, damageSource, (float) damage))) {
//                if (target.getType() == EntityType.ENDERMAN) return;
//                if (this.isOnFire()) target.setOnFireFor(5.0f);
//                if (target instanceof LivingEntity livingTarget) {
//                    var power = EnchantmentHelper.modifyKnockback(world, stack, target, damageSource, 0.0F);
//                    if (power > 0.0) {
//                        var potency = Math.max(0.0, 1.0 - livingTarget.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
//                        var knockback = this.getVelocity().multiply(1.0, 0.8, 1.0).normalize().multiply(power * 0.6 * potency);
//                        if (knockback.lengthSquared() > 0.0) target.addVelocity(knockback.x, knockback.y + 0.1, knockback.z);
//                    }
//                    EnchantmentHelper.onTargetDamaged(world, target, damageSource, stack);
//                    if (livingTarget != owner && livingTarget instanceof PlayerEntity && owner instanceof ServerPlayerEntity serverOwner && !this.isSilent()) {
//                        serverOwner.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
//                    }
//                }
//                this.playSound(KingsSounds.DAGGER_ENTITY_HIT, 0.8F, 0.9f + 0.6f * this.random.nextFloat());
//                this.getWorld().sendEntityStatus(this, (byte) 100);
//                this.discard();
//            } else {
//                this.deflect(ProjectileDeflection.SIMPLE, target, this.getOwner(), false);
//                this.setVelocity(this.getVelocity().multiply(0.6));
//            }
//        }
//    }
//
//    @Override
//    protected void onBlockHit(BlockHitResult blockHitResult) {
//        super.onBlockHit(blockHitResult);
//        if (!this.isRemoved()) {
//            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), KingsSounds.DAGGER_BLOCK_HIT, SoundCategory.PLAYERS, 0.8F, 0.9f + 0.6f * this.random.nextFloat());
//            this.dataTracker.set(STUCK, true);
//            if (this.dataTracker.get(LIFETIME) > 6) this.dataTracker.set(LIFETIME, KingsOptions.DAGGER_LIFETIME.get() * 2);
//            this.setPosition(blockHitResult.getPos().subtract(this.getVelocity()));
//            this.getWorld().sendEntityStatus(this, (byte) 100);
//        }
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//        if (this.isNotStuck()) {
//            var velocity = this.getVelocity();
//            if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
//                var d = velocity.horizontalLength();
//                this.setYaw((float)(MathHelper.atan2(velocity.x, velocity.z) * 180.0F / (float)Math.PI));
//                this.setPitch((float)(MathHelper.atan2(velocity.y, d) * 180.0F / (float)Math.PI));
//                this.prevYaw = this.getYaw();
//                this.prevPitch = this.getPitch();
//            }
//            this.checkBlockCollision();
//            if (this.isTouchingWaterOrRain() || this.getWorld().getBlockState(this.getBlockPos()).isOf(Blocks.POWDER_SNOW)) this.extinguish();
//            var pos = this.getPos();
//            var next = pos.add(velocity);
//            HitResult hit = this.getWorld().raycast(new RaycastContext(pos, next, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
//            if (hit.getType() != HitResult.Type.MISS) next = hit.getPos();
//            var entityHit = ProjectileUtil.getEntityCollision(this.getWorld(), this, pos.subtract(velocity), next, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
//            if (entityHit != null) hit = entityHit;
//            if (hit instanceof EntityHitResult entityHitResult) {
//                var entity = entityHitResult.getEntity();
//                var owner = this.getOwner();
//                if (entity == owner) hit = null;
//                if (entity instanceof PlayerEntity target && owner instanceof PlayerEntity player && !player.shouldDamagePlayer(target)) hit = null;
//            }
//            if (hit != null) {
//                this.hitOrDeflect(hit);
//                this.velocityDirty = true;
//            }
//            this.updateRotation();
//            if (this.isTouchingWater()) for (var i = 0; i < 4; ++i) this.getWorld().addParticle(ParticleTypes.BUBBLE, next.x - velocity.x * 0.25, next.y - velocity.y * 0.25, next.z - velocity.z * 0.25, velocity.x, velocity.y, velocity.z);
//            this.setVelocity(velocity.multiply(0.99F));
//            this.applyGravity();
//            this.setPosition(next);
//        }
//        this.dataTracker.set(LIFETIME, this.dataTracker.get(LIFETIME) - 1);
//        if (this.dataTracker.get(LIFETIME) <= 0) this.discard();
//        if (this.dataTracker.get(FLIGHT_TIME) > 0) this.dataTracker.set(FLIGHT_TIME, this.dataTracker.get(FLIGHT_TIME) - 1);
//    }

    public ItemStack getStack() {
        return this.dataTracker.get(STACK);
    }

    public void setStack(ItemStack stack) {
        this.dataTracker.set(STACK, stack);
    }

    public boolean isNotStuck() {
        return !this.dataTracker.get(STUCK);
    }

    @Override
    protected void initDataTracker(DataTracker.@NotNull Builder builder) {
        builder.add(STACK, ItemStack.EMPTY);
//        builder.add(FLIGHT_TIME, KingsOptions.DAGGER_FLIGHT_TIME.get());
        builder.add(STUCK, false);
    }
}