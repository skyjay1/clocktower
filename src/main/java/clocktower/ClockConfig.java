package clocktower;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClockConfig {

    private static ForgeConfigSpec.IntValue TIME_OFFSET;
    private float timeOffset;

    public ClockConfig(final ForgeConfigSpec.Builder builder) {
        TIME_OFFSET = builder.comment("Number of ticks to offset clock hands",
                        "Minecraft sunrise starts at 0 and noon is at 6. Change this to 6000 to have sunrise at 6 and noon at 12.")
                .defineInRange("time_offset", 0, 0, 24000);
    }

    public void bake() {
        timeOffset = (float) TIME_OFFSET.get();
    }

    public float getTimeOffset() {
        return timeOffset;
    }
}
