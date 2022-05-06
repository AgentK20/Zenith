package safro.apotheosis.ench.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import safro.apotheosis.api.enchant.TableApplicableEnchant;

public class InertEnchantment extends Enchantment implements TableApplicableEnchant {

	public InertEnchantment() {
		super(Rarity.VERY_RARE, null, new EquipmentSlot[0]);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isDiscoverable() {
		return false;
	}

	@Override
	public boolean isAllowedOnBooks() {
		return false;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

}
