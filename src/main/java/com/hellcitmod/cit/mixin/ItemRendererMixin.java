package com.hellcitmod.cit.mixin;

import com.hellcitmod.cit.render.CitModelResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method = "getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;", at = @At("RETURN"), cancellable = true)
    private void hellcit$overrideModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        var resolved = CitModelResolver.resolve(stack);
        if (resolved.isEmpty()) {
            return;
        }

        Identifier modelId = resolved.get().withPrefixedPath("item/");
        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);
        if (model != null) {
            cir.setReturnValue(model);
        }
    }
}
