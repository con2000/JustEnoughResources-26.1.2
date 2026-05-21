package jeresources.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Jer3DBlockRenderer26 {
    private static final BlockPos PREVIEW_POS = BlockPos.ZERO;

    private Jer3DBlockRenderer26() {
    }

    public static void renderBlockPreview(
        GuiGraphicsExtractor guiGraphics,
        BlockState state,
        int x,
        int y,
        int size,
        float yawDegrees,
        float pitchDegrees
    ) {
        if (state == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            guiGraphics.item(new ItemStack(state.getBlock()), x, y);
            return;
        }

        FallingBlockRenderState renderState = new FallingBlockRenderState();
        renderState.entityType = EntityType.FALLING_BLOCK;
        renderState.lightCoords = 15728880;
        renderState.boundingBoxWidth = 1.0F;
        renderState.boundingBoxHeight = 1.0F;
        renderState.shadowRadius = 0.0F;
        renderState.outlineColor = 0;
        renderState.shadowPieces.clear();

        renderState.movingBlockRenderState.blockPos = PREVIEW_POS;
        renderState.movingBlockRenderState.randomSeedPos = PREVIEW_POS;
        renderState.movingBlockRenderState.blockState = state;
        renderState.movingBlockRenderState.biome = level.getBiome(PREVIEW_POS);
        renderState.movingBlockRenderState.cardinalLighting = level.cardinalLighting();
        renderState.movingBlockRenderState.lightEngine = level.getLightEngine();

        int centerX = x + size / 2;
        int centerY = y + size / 2;
        float renderScale = size * 0.72F;

        Quaternionf cameraQuat = new Quaternionf().rotateZ((float) Math.PI).rotateY((float) Math.PI);
        Quaternionf blockQuat = new Quaternionf()
            .rotateX((float) Math.toRadians(pitchDegrees))
            .rotateY((float) Math.toRadians(yawDegrees));

        guiGraphics.entity(
            renderState,
            renderScale,
            new Vector3f(centerX, centerY, 0.0F),
            cameraQuat,
            blockQuat,
            x,
            y,
            x + size,
            y + size
        );
    }
}
