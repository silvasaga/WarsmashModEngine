package com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders;

import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;

public class CAttackOrder implements COrder {
	private final CUnit unit;
	private boolean wasWithinPropWindow = false;
	private final CWidget target;

	public CAttackOrder(final CUnit unit, final CWidget target) {
		this.unit = unit;
		this.target = target;
	}

	@Override
	public boolean update(final CSimulation simulation) {
		final float prevX = this.unit.getX();
		final float prevY = this.unit.getY();
		final float deltaY = this.target.getY() - prevY;
		final float deltaX = this.target.getX() - prevX;
		final double goalAngleRad = Math.atan2(deltaY, deltaX);
		float goalAngle = (float) Math.toDegrees(goalAngleRad);
		if (goalAngle < 0) {
			goalAngle += 360;
		}
		final float facing = this.unit.getFacing();
		float delta = goalAngle - facing;
		final float absDelta = Math.abs(delta);
		final float propulsionWindow = simulation.getUnitData().getPropulsionWindow(this.unit.getTypeId());
		final float turnRate = simulation.getUnitData().getTurnRate(this.unit.getTypeId());
		final int speed = this.unit.getSpeed();

		if (delta < -180) {
			delta = 360 + delta;
		}
		if (delta > 180) {
			delta = -360 + delta;
		}

		if ((absDelta <= 1.0) && (absDelta != 0)) {
			this.unit.setFacing(goalAngle);
		}
		else {
			float angleToAdd = ((Math.signum(delta) * turnRate) * WarsmashConstants.SIMULATION_STEP_TIME) * 360;
			if (absDelta < Math.abs(angleToAdd)) {
				angleToAdd = delta;
			}
			this.unit.setFacing(facing + angleToAdd);
		}
		if (absDelta < propulsionWindow) {
			final float speedTick = speed * WarsmashConstants.SIMULATION_STEP_TIME;
			final float speedTickSq = speedTick * speedTick;

			if (((deltaX * deltaX) + (deltaY * deltaY)) <= speedTickSq) {
				this.unit.setX(this.target.getX());
				this.unit.setY(this.target.getY());
				return true;
			}
			else {
				this.unit.setX(prevX + (float) (Math.cos(goalAngleRad) * speedTick));
				this.unit.setY(prevY + (float) (Math.sin(goalAngleRad) * speedTick));
			}
			this.wasWithinPropWindow = true;
		}
		else {
			// If this happens, the unit is facing the wrong way, and has to turn before
			// moving.
			this.wasWithinPropWindow = false;
		}

		return false;
	}

	@Override
	public int getOrderId() {
		return CAbilityAttack.ORDER_ID;
	}

	@Override
	public String getAnimationName() {
		if (!this.wasWithinPropWindow) {
			return "stand";
		}
		return "walk";
	}

}