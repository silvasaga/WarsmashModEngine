package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.impl;

import java.util.List;

import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.inventory.CAbilityInventory;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.types.CAbilityType;

public class CAbilityTypeInventory extends CAbilityType<CAbilityTypeInventoryLevelData> {

	public CAbilityTypeInventory(final War3ID alias, final War3ID code,
			final List<CAbilityTypeInventoryLevelData> levelData) {
		super(alias, code, levelData);
	}

	@Override
	public CAbility createAbility(final int handleId) {
		final CAbilityTypeInventoryLevelData levelData = getLevelData(0);
		return new CAbilityInventory(handleId, getAlias(), levelData.isCanDropItems(), levelData.isCanGetItems(),
				levelData.isCanUseItems(), levelData.isDropItemsOnDeath(), levelData.getItemCapacity());
	}

}
