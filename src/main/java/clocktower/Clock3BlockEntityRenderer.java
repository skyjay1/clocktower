package clocktower;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

public class Clock3BlockEntityRenderer extends TileEntityRenderer<Clock3BlockEntity> {

    public static final ResourceLocation HAND_3 = new ResourceLocation(ClockTower.MODID, "block/clock_hand3");

    public Clock3BlockEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(Clock3BlockEntity blockEntity, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
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
        IBakedModel model = mc.getModelManager().getModel(HAND_3);
        RenderType rendertype = RenderTypeLookup.getMovingBlockRenderType(blockState);
        IVertexBuilder vertexBuilder = bufferIn.getBuffer(rendertype);

        // transforms
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 0.5D, 0.5D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.F - facing.toYRot()));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(rotation));

        // render block
        mc.getBlockRenderer().getModelRenderer().renderModel(blockEntity.getLevel(), model,
                blockState, blockPos, matrixStackIn, vertexBuilder,
                checkSides, new Random(), blockState.getSeed(blockPos), packedOverlayIn, EmptyModelData.INSTANCE);
        matrixStackIn.popPose();
    }
}
