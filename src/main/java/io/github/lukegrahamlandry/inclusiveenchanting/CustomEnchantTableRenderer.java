package io.github.lukegrahamlandry.inclusiveenchanting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

public class CustomEnchantTableRenderer implements BlockEntityRenderer<CustomEnchantTableTile> {
    /** The texture for the book above the enchantment table. */
    public static final Material TEXTURE_BOOK = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/enchanting_table_book"));
    private final BookModel modelBook;

    public CustomEnchantTableRenderer(BlockEntityRendererProvider.Context pContext) {
        modelBook = new BookModel(pContext.bakeLayer(ModelLayers.BOOK));
    }

    public void render(CustomEnchantTableTile tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.5D, 0.75D, 0.5D);
        float f = (float)tileEntityIn.ticks + partialTicks;
        matrixStackIn.translate(0.0D, (double)(0.1F + Mth.sin(f * 0.1F) * 0.01F), 0.0D);

        float f1;
        for(f1 = tileEntityIn.nextPageAngle - tileEntityIn.pageAngle; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
        }

        while(f1 < -(float)Math.PI) {
            f1 += ((float)Math.PI * 2F);
        }

        float f2 = tileEntityIn.pageAngle + f1 * partialTicks;
        matrixStackIn.mulPose(Vector3f.YP.rotation(-f2));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
        float f3 = Mth.lerp(partialTicks, tileEntityIn.oFlip, tileEntityIn.flip);
        float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
        float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
        float f6 = Mth.lerp(partialTicks, tileEntityIn.pageTurningSpeed, tileEntityIn.nextPageTurningSpeed);
        this.modelBook.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
        VertexConsumer ivertexbuilder = TEXTURE_BOOK.buffer(bufferIn, RenderType::entitySolid);
        this.modelBook.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();
    }
}
