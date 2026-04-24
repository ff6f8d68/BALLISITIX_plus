package ballistix.common.block.subtype;

import ballistix.References;
import ballistix.common.settings.Constants;
import electrodynamics.api.ISubtype;
import net.minecraft.resources.ResourceLocation;

public enum SubtypeMissile implements ISubtype {

	closerange(Constants.CLOSERANGE_MISSILE_RANGE, 1.0f, true),
	mediumrange(Constants.MEDIUMRANGE_MISSILE_RANGE, 1.2f, true),
	longrange(Constants.LONGRANGE_MISSILE_RANGE, 1.5f, true);

	public final int range;
	public final float speedModifier;
	public final boolean radarVisible;

	SubtypeMissile(int range, float speedModifier, boolean radarVisible) {
		this.range = range;
		this.speedModifier = speedModifier;
		this.radarVisible = radarVisible;
	}

	@Override
	public String forgeTag() {
		return "missile/" + name();
	}

	@Override
	public boolean isItem() {
		return true;
	}

	@Override
	public String tag() {
		return "missile" + name();
	}

	public ResourceLocation model() {
		return new ResourceLocation(References.ID, "entity/" + tag());
	}

}
