package dev.shadowsoffire.apotheosis.util.events;

import com.mojang.datafixers.util.Either;
import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ModifyComponents {

    public static final Event<modifyComponentsCallback> MODIFY_COMPONENTS = EventFactory.createArrayBacked(modifyComponentsCallback.class, callbacks -> event -> {
        for (modifyComponentsCallback callback : callbacks) {
            callback.modifyComponents(event);
            if (event.isCancelled())
                return;
        }
    });

    @FunctionalInterface
    public interface modifyComponentsCallback {
        void modifyComponents(ModifyComponentsEvent event);
    }

    public static class ModifyComponentsEvent extends CancellableEvent {
        public final ItemStack stack;
        public final int screenWidth;
        public final int screenHeight;
        public final List<Either<FormattedText, TooltipComponent>> tooltipElements;
        public int maxWidth;

        public ModifyComponentsEvent(ItemStack stack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth) {
            this.stack = stack;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.tooltipElements = tooltipElements;
            this.maxWidth = maxWidth;
        }

    }
}