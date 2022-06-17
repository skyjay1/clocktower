package clocktower;

import net.minecraft.tileentity.TileEntity;

public class ClockBlockEntity extends TileEntity {

    public ClockBlockEntity() {
        super(EventHandler.BlockEntityReg.CLOCK_TYPE);
    }

    public float getHour() {
        return (level.getDayTime() + ClockTower.CONFIG.getTimeOffset()) / 1000.0F;
    }
}
