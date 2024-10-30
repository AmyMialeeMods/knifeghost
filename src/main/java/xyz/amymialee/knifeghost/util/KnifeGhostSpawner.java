package xyz.amymialee.knifeghost.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.knifeghost.KnifeGhost;
import xyz.amymialee.knifeghost.entity.KnifeGhostEntity;

public class KnifeGhostSpawner {
    public static void trySpawn(World world, BlockPos pos) {
        var ghost = KnifeGhost.KNIFE_GHOST_ENTITY.create(world);
        if (ghost == null) return;
        var spawnPos = getNearbySpawnPos(world, ghost, pos);
        if (spawnPos == null) {
            ghost.discard();
            return;
        }
        ghost.getNavigation().startMovingTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 1.0);
        ghost.setPositionTarget(pos, 48);
        world.spawnEntity(ghost);
    }

    private static @Nullable BlockPos getNearbySpawnPos(World world, KnifeGhostEntity ghost, BlockPos pos) {
        for (var i = 0; i < 10; i++) {
            var x = pos.getX() - 24 + world.random.nextInt(48);
            var y = pos.getY() - 2;
            var z = pos.getZ() - 24 + world.random.nextInt(48);
            var mutablePos = new BlockPos(x, y, z);
            for (; y < pos.getY() + 6; y++) {
                ghost.setPosition(mutablePos.getX() + 0.5, mutablePos.getY(), mutablePos.getZ() + 0.5);
                if (!suffocatesAt(world, mutablePos)) return mutablePos;
                mutablePos = mutablePos.up();
            }
        }
        return null;
    }

    private static boolean suffocatesAt(BlockView world, BlockPos pos) {
        for (var blockPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
            if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) return true;
        }
        return false;
    }
}