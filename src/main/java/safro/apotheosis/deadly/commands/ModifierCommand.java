package safro.apotheosis.deadly.commands;

import java.util.Arrays;
import java.util.Locale;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ModifierCommand {

	public static final SuggestionProvider<CommandSourceStack> SUGGEST_OP = (ctx, builder) -> {
		return SharedSuggestionProvider.suggest(Arrays.stream(Operation.values()).map(Operation::name), builder);
	};

	public static final SuggestionProvider<CommandSourceStack> SUGGEST_SLOT = (ctx, builder) -> {
		return SharedSuggestionProvider.suggest(Arrays.stream(EquipmentSlot.values()).map(EquipmentSlot::name), builder);
	};

	public static final SuggestionProvider<CommandSourceStack> SUGGEST_ATTRIB = (ctx, builder) -> {
		return SharedSuggestionProvider.suggest(Registry.ATTRIBUTE.keySet().stream().map(ResourceLocation::toString), builder);
	};

	public static void register(CommandDispatcher<CommandSourceStack> pDispatcher) {
		pDispatcher.register(Commands.literal("modifier").requires(c -> c.hasPermission(2)).then(Commands.argument("attribute", ResourceLocationArgument.id()).suggests(SUGGEST_ATTRIB).then(Commands.argument("op", StringArgumentType.word()).suggests(SUGGEST_OP).then(Commands.argument("value", FloatArgumentType.floatArg()).then(Commands.argument("slot", StringArgumentType.word()).suggests(SUGGEST_SLOT).executes(c -> {
			Player p = c.getSource().getPlayerOrException();
			Attribute attrib = Registry.ATTRIBUTE.get(c.getArgument("attribute", ResourceLocation.class));
			Operation op = Operation.valueOf(c.getArgument("op", String.class).toUpperCase(Locale.ROOT));
			EquipmentSlot slot = EquipmentSlot.valueOf(c.getArgument("slot", String.class).toUpperCase(Locale.ROOT));
			float value = c.getArgument("value", Float.class);
			ItemStack stack = p.getMainHandItem();
			stack.addAttributeModifier(attrib, new AttributeModifier("cmd-generated-modif", value, op), slot);
			return 0;
		}))))));
	}

}
