package clocktower;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Clock2BlockEntity extends BlockEntity {

    public Clock2BlockEntity(BlockPos pos, BlockState blockState) {
        super(EventHandler.BlockEntityReg.CLOCK2_TYPE, pos, blockState);
    }

    public float getHour() {
        return (level.getDayTime() + ClockTower.CONFIG.getTimeOffset()) / 1000.0F;
    }
}
