package dev.shadowsoffire.apotheosis.ench.asm.categories;

import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

public class AxeCategory extends EnchantmentCategoryMixin {
    @Override
    public boolean canEnchant(Item item) {
        return item.canPerformAction(new ItemStack(item), ToolActions.AXE_DIG);
    }
}

@Mixin(EnchantmentCategory.class)
abstract class EnchantmentCategoryMixin1 {
    @Shadow
    abstract boolean canEnchant(Item item);
}