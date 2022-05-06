package safro.apotheosis.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import safro.apotheosis.ench.EnchModuleEvents;

@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin extends AbstractArrow implements EnchModuleEvents.TridentGetter {

    int pierces = 0;
    Vec3 oldVel = null;
    @Shadow
    private boolean dealtDamage;
    {
        piercingIgnoreEntityIds = new IntOpenHashSet();
    }

    protected ThrownTridentMixin(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    @Override
    @Accessor
    public abstract ItemStack getTridentItem();

    @Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At("HEAD"), cancellable = true)
    public void startHitEntity(EntityHitResult res, CallbackInfo ci) {
        this.oldVel = this.getDeltaMovement();
        if (this.piercingIgnoreEntityIds.contains(res.getEntity().getId())) ci.cancel();
    }

    @Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At("TAIL"), cancellable = true)
    public void endHitEntity(EntityHitResult res, CallbackInfo ci) {
        int pierceLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, this.getTridentItem());
        if (this.pierces++ < pierceLevel) {
            this.dealtDamage = false;
            this.setDeltaMovement(this.oldVel);
            this.piercingIgnoreEntityIds.add(res.getEntity().getId());
        }
    }

}
