package clocktower;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ClockBlockEntity extends BlockEntity {

    public ClockBlockEntity(BlockPos pos, BlockState blockState) {
        super(EventHandler.BlockEntityReg.CLOCK_TYPE, pos, blockState);
    }

    public float getHour() {
        return (level.getDayTime() + ClockTower.CONFIG.getTimeOffset()) / 1000.0F;
    }
}
