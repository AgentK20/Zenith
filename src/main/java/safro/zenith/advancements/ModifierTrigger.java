package safro.zenith.advancements;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import safro.zenith.Zenith;
import safro.zenith.spawn.modifiers.SpawnerModifier;
import safro.zenith.spawn.modifiers.SpawnerStats;
import safro.zenith.util.IBaseSpawner;

public class ModifierTrigger implements CriterionTrigger<ModifierTrigger.Instance> {
	private static final ResourceLocation ID = new ResourceLocation(Zenith.MODID, "spawner_modifier");
	private final Map<PlayerAdvancements, ModifierTrigger.Listeners> listeners = Maps.newHashMap();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, CriterionTrigger.Listener<ModifierTrigger.Instance> listener) {
		ModifierTrigger.Listeners ModifierTrigger$listeners = this.listeners.get(playerAdvancementsIn);
		if (ModifierTrigger$listeners == null) {
			ModifierTrigger$listeners = new ModifierTrigger.Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, ModifierTrigger$listeners);
		}

		ModifierTrigger$listeners.add(listener);
	}

	@Override
	public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, CriterionTrigger.Listener<ModifierTrigger.Instance> listener) {
		ModifierTrigger.Listeners ModifierTrigger$listeners = this.listeners.get(playerAdvancementsIn);
		if (ModifierTrigger$listeners != null) {
			ModifierTrigger$listeners.remove(listener);
			if (ModifierTrigger$listeners.isEmpty()) {
				this.listeners.remove(playerAdvancementsIn);
			}
		}

	}

	@Override
	public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public ModifierTrigger.Instance createInstance(JsonObject json, DeserializationContext conditionsParser) {
		MinMaxBounds.Ints minDelay = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.MIN_DELAY.getId()));
		MinMaxBounds.Ints maxDelay = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.MAX_DELAY.getId()));
		MinMaxBounds.Ints spawnCount = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.SPAWN_COUNT.getId()));
		MinMaxBounds.Ints nearbyEnts = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.MAX_NEARBY_ENTITIES.getId()));
		MinMaxBounds.Ints playerRange = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.REQ_PLAYER_RANGE.getId()));
		MinMaxBounds.Ints spawnRange = MinMaxBounds.Ints.fromJson(json.get(SpawnerStats.SPAWN_RANGE.getId()));
		Boolean ignorePlayers = json.has(SpawnerStats.IGNORE_PLAYERS.getId()) ? json.get(SpawnerStats.IGNORE_PLAYERS.getId()).getAsBoolean() : null;
		Boolean ignoreConditions = json.has(SpawnerStats.IGNORE_CONDITIONS.getId()) ? json.get(SpawnerStats.IGNORE_CONDITIONS.getId()).getAsBoolean() : null;
		Boolean redstone = json.has(SpawnerStats.REDSTONE_CONTROL.getId()) ? json.get(SpawnerStats.REDSTONE_CONTROL.getId()).getAsBoolean() : null;
		Boolean ignoreLight = json.has(SpawnerStats.IGNORE_LIGHT.getId()) ? json.get(SpawnerStats.IGNORE_LIGHT.getId()).getAsBoolean() : null;
		Boolean noAI = json.has(SpawnerStats.NO_AI.getId()) ? json.get(SpawnerStats.NO_AI.getId()).getAsBoolean() : null;
		return new ModifierTrigger.Instance(minDelay, maxDelay, spawnCount, nearbyEnts, playerRange, spawnRange, ignorePlayers, ignoreConditions, redstone, ignoreLight, noAI);
	}

	public void trigger(ServerPlayer player, SpawnerBlockEntity tile, SpawnerModifier modif) {
		ModifierTrigger.Listeners ModifierTrigger$listeners = this.listeners.get(player.getAdvancements());
		if (ModifierTrigger$listeners != null) {
			ModifierTrigger$listeners.trigger(tile, modif);
		}

	}

	public static class Instance extends AbstractCriterionTriggerInstance {
		private final MinMaxBounds.Ints minDelay;
		private final MinMaxBounds.Ints maxDelay;
		private final MinMaxBounds.Ints spawnCount;
		private final MinMaxBounds.Ints nearbyEnts;
		private final MinMaxBounds.Ints playerRange;
		private final MinMaxBounds.Ints spawnRange;
		private final Boolean ignorePlayers;
		private final Boolean ignoreConditions;
		private final Boolean redstone;
		private final Boolean ignoreLight;
		private final Boolean noAI;

		public Instance(MinMaxBounds.Ints minDelay, MinMaxBounds.Ints maxDelay, MinMaxBounds.Ints spawnCount, MinMaxBounds.Ints nearbyEnts, MinMaxBounds.Ints playerRange, MinMaxBounds.Ints spawnRange, Boolean ignorePlayers, Boolean ignoreConditions, Boolean redstone, Boolean ignoreLight, Boolean noAI) {
			super(ModifierTrigger.ID, EntityPredicate.Composite.ANY);
			this.minDelay = minDelay;
			this.maxDelay = maxDelay;
			this.spawnCount = spawnCount;
			this.nearbyEnts = nearbyEnts;
			this.playerRange = playerRange;
			this.spawnRange = spawnRange;
			this.ignorePlayers = ignorePlayers;
			this.ignoreConditions = ignoreConditions;
			this.redstone = redstone;
			this.ignoreLight = ignoreLight;
			this.noAI = noAI;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext serializer) {
			return new JsonObject();
		}

		public boolean test(SpawnerBlockEntity tile, SpawnerModifier modif) {
			IBaseSpawner logic = (IBaseSpawner) tile;
			if (!this.minDelay.matches(logic.getSpawner().minSpawnDelay)) return false;
			if (!this.maxDelay.matches(logic.getSpawner().maxSpawnDelay)) return false;
			if (!this.spawnCount.matches(logic.getSpawner().spawnCount)) return false;
			if (!this.nearbyEnts.matches(logic.getSpawner().maxNearbyEntities)) return false;
			if (!this.playerRange.matches(logic.getSpawner().requiredPlayerRange)) return false;
			if (!this.spawnRange.matches(logic.getSpawner().spawnRange)) return false;
			if (this.ignorePlayers != null && logic.getIgnoresPlayers() != this.ignorePlayers) return false;
			if (this.ignoreConditions != null && logic.getIgnoresConditions() != this.ignoreConditions) return false;
			if (this.redstone != null && logic.getRedstoneControl() != this.redstone) return false;
			if (this.ignoreLight != null && logic.getIgnoreLight() != this.ignoreLight) return false;
			if (this.noAI != null && logic.getNoAi() != this.noAI) return false;
			return true;
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<CriterionTrigger.Listener<ModifierTrigger.Instance>> listeners = Sets.newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(CriterionTrigger.Listener<ModifierTrigger.Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(CriterionTrigger.Listener<ModifierTrigger.Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(SpawnerBlockEntity tile, SpawnerModifier modif) {
			List<CriterionTrigger.Listener<ModifierTrigger.Instance>> list = null;

			for (CriterionTrigger.Listener<ModifierTrigger.Instance> listener : this.listeners) {
				if (listener.getTriggerInstance().test(tile, modif)) {
					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (CriterionTrigger.Listener<ModifierTrigger.Instance> listener1 : list) {
					listener1.run(this.playerAdvancements);
				}
			}

		}
	}
}