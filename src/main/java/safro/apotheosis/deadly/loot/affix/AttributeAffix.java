package safro.apotheosis.deadly.loot.affix;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import safro.apotheosis.deadly.DeadlyModule;
import safro.apotheosis.deadly.loot.LootCategory;
import safro.apotheosis.deadly.loot.LootRarity;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;

/**
 * Helper class for affixes that modify attributes, as the apply method is the same for most of those.
 */
public class AttributeAffix extends Affix {

	protected final @Nullable Predicate<LootCategory> types;
	protected final List<ModifierInst> modifiers;

	public AttributeAffix(LootRarity rarity, @Nullable Predicate<LootCategory> types, ModifierInst... modifiers) {
		super(rarity);
		this.types = types;
		this.modifiers = Arrays.asList(modifiers);
	}

	@Override
	public void addInformation(ItemStack stack, float level, Consumer<Component> list) {
	}

	@Override
	public Component getDisplayName(float level) {
		return new TranslatableComponent("affix." + this.getRegistryName() + ".name", fmt(level * 100)).withStyle(ChatFormatting.GRAY);
	}

	@Override
	public void addModifiers(ItemStack stack, float level, EquipmentSlot type, BiConsumer<Attribute, AttributeModifier> map) {
		LootCategory cat = LootCategory.forItem(stack);
		if (cat == null) {
			DeadlyModule.LOGGER.debug("Attempted to apply the attributes of affix {} on item {}, but it is not an affix-compatible item!", this.getRegistryName(), stack.getHoverName());
			return;
		}
		for (EquipmentSlot slot : cat.getSlots(stack)) {
			if (slot == type) {
				this.modifiers.forEach(ins -> map.accept(ins.attr.get(), ins.build(slot, this.getRegistryName(), level)));
			}
		}
	}

	@Override
	public boolean canApply(LootCategory type) {
		return this.types == null ? true : this.types.test(type);
	}

	public record ModifierInst(Supplier<Attribute> attr, Operation op, Function<Float, Float> valueFactory, Map<EquipmentSlot, UUID> cache) {

		public AttributeModifier build(EquipmentSlot slot, ResourceLocation id, float level) {
			return new AttributeModifier(cache.computeIfAbsent(slot, k -> UUID.randomUUID()), "affix:" + id, valueFactory.apply(level), op);
		}
	}

	public static class Builder {

		private final LootRarity rarity;
		private final List<ModifierInst> modifiers = new ArrayList<>();
		private Predicate<LootCategory> types;

		public Builder(LootRarity rarity) {
			this.rarity = rarity;
		}

		public Builder types(Predicate<LootCategory> types) {
			this.types = types;
			return this;
		}

		public Builder with(Supplier<Attribute> attr, Operation op, Function<Float, Float> valueFactory) {
			this.modifiers.add(new ModifierInst(attr, op, valueFactory, new HashMap<>()));
			return this;
		}

		public Builder with(Attribute attr, Operation op, Function<Float, Float> valueFactory) {
			return with(() -> attr, op, valueFactory);
		}

		public Builder with(Supplier<Attribute> attr, Operation op, float min, float max) {
			return with(attr, op, level -> min + level * max);
		}

		public Builder with(Attribute attr, Operation op, float min, float max) {
			return with(attr, op, level -> min + level * max);
		}

		public AttributeAffix build() {
			return (AttributeAffix) new AttributeAffix(rarity, types, modifiers.toArray(new ModifierInst[0]));
		}

	}

}