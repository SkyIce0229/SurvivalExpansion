package tmmi.skyice.survivalexpansion.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tmmi.skyice.survivalexpansion.event.PlayerEvent;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("RETURN"))
    public void dead(DamageSource damageSource, CallbackInfo ci) {
        PlayerEvent.DEAD.invoker().onDead((ServerPlayerEntity) (Object) this);
    }
}
