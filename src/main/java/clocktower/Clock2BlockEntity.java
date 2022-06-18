package clocktower;

import net.minecraft.tileentity.TileEntity;

public class Clock2BlockEntity extends TileEntity {

    public Clock2BlockEntity() {
        super(EventHandler.BlockEntityReg.CLOCK2_TYPE);
    }

    public float getHour() {
        return (level.getDayTime() + ClockTower.CONFIG.getTimeOffset()) / 1000.0F;
    }
}
