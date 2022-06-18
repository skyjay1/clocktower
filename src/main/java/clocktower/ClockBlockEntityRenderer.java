package clocktower;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class ClockBlockEntityRenderer implements BlockEntityRenderer<ClockBlockEntity> {

    public static final ResourceLocation HAND_2 = new ResourceLocation(ClockTower.MODID, "block/clock_hand2");

    private final Map<Direction, Vector3d> rotations = new EnumMap<>(Direction.class);

    public ClockBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        rotations.put(Direction.NORTH, new Vector3d(0, 0, 11.0D / 16.0D));
        rotations.put(Direction.SOUTH, new Vector3d(1, 0, 5.0D / 16.0D));
        rotations.put(Direction.WEST, new Vector3d(11.0D / 16.0D, 0, 1.0D));
        rotations.put(Direction.EAST, new Vector3d(5.0D / 16.0D, 0, 0));
    }

    @Override
    public void render(ClockBlockEntity blockEntity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                       int packedLightIn, int packedOverlayIn) {
        // values
        BlockState blockState = blockEntity.getBlockState();
        BlockPos blockPos = blockEntity.getBlockPos();
        boolean checkSides = false;
        Direction facing = blockState.getValue(ClockBlock.FACING);
        float time = blockEntity.getHour();
        float rotation = time * 30.0F;
        // create vertex builder
        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getModelManager().getModel(HAND_2);
        RenderType rendertype = ItemBlockRenderTypes.getMovingBlockRenderType(blockState);
        VertexConsumer vertexBuilder = bufferIn.getBuffer(rendertype);

        // transforms
        matrixStackIn.pushPose();
        Vector3d vector3d = rotations.getOrDefault(facing, new Vector3d(0, 0, 0));
        matrixStackIn.translate(vector3d.x, vector3d.y + 1.0D, vector3d.z);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.F - facing.toYRot()));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rotation));

        // render block
        mc.getBlockRenderer().getModelRenderer().tesselateBlock(blockEntity.getLevel(), model,
                blockState, blockPos, matrixStackIn, vertexBuilder,
                checkSides, new Random(), blockState.getSeed(blockPos), packedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.popPose();
    }
}
