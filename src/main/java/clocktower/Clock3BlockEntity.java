package clocktower;

import net.minecraft.tileentity.TileEntity;

public class Clock3BlockEntity extends TileEntity {

    public Clock3BlockEntity() {
        super(EventHandler.BlockEntityReg.CLOCK3_TYPE);
    }

    public float getHour() {
        return (level.getDayTime() + ClockTower.CONFIG.getTimeOffset()) / 1000.0F;
    }
}
