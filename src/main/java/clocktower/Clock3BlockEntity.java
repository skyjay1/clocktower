package clocktower;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Clock3BlockEntity extends BlockEntity {

    public Clock3BlockEntity(BlockPos pos, BlockState state) {
        super(EventHandler.BlockEntityReg.CLOCK3_TYPE, pos, state);
    }

    public float getHour() {
        return (level.getDayTime() + ClockTower.CONFIG.getTimeOffset()) / 1000.0F;
    }
}
