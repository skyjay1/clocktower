package clocktower;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

import java.util.Random;

public class Clock3BlockEntityRenderer implements BlockEntityRenderer<Clock3BlockEntity> {

    public static final ResourceLocation HAND_3 = new ResourceLocation(ClockTower.MODID, "block/clock_hand3");

    public Clock3BlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(Clock3BlockEntity blockEntity, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn,
                       int packedLightIn, int packedOverlayIn) {
        // values
        BlockState blockState = blockEntity.getBlockState();
        BlockPos blockPos = blockEntity.getBlockPos();
        boolean checkSides = false;
        Direction facing = blockState.getValue(Clock2Block.FACING);
        float time = blockEntity.getHour();
        float rotation = time * 30.0F;

        // create vertex builder
        Minecraft mc = Minecraft.getInstance();
        BakedModel model = mc.getModelManager().getModel(HAND_3);
        RenderType rendertype = ItemBlockRenderTypes.getMovingBlockRenderType(blockState);
        VertexConsumer vertexBuilder = bufferIn.getBuffer(rendertype);

        // transforms
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.F - facing.toYRot()));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rotation));

        // render block
        mc.getBlockRenderer().getModelRenderer().tesselateBlock(blockEntity.getLevel(), model,
                blockState, blockPos, matrixStackIn, vertexBuilder,
                checkSides, new Random(), blockState.getSeed(blockPos), packedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.popPose();
    }

    @Override
    public int getViewDistance() {
        return 264;
    }

    @Override
    public boolean shouldRenderOffScreen(Clock3BlockEntity blockEntity) {
        return true;
    }
}
