package com.wolf.petconnect.mixins;

import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Wolf.class)
public class WolfFix {
     @Redirect(method = "setTame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Wolf;setHealth(F)V"))
     private void redirectSetHealth(Wolf instance, float health) {
         ((Wolf)(Object)this).setHealth(instance.getHealth());
     }
}
