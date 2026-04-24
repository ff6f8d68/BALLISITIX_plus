package ballistix.registers;

import ballistix.References;
import ballistix.common.inventory.container.ContainerCIWSTurret;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.inventory.container.ContainerFireControlRadar;
import ballistix.common.inventory.container.ContainerLaserTurret;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.bplus.inventory.container.ContainerDesignator;
import ballistix.bplus.inventory.container.ContainerBigDartPod;
import ballistix.bplus.inventory.container.ContainerRangeDesignator;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IForgeMenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class BallistixMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU_TYPES, References.ID);

	public static final RegistryObject<MenuType<ContainerMissileSilo>> CONTAINER_MISSILESILO = MENU_TYPES.register("missilesilo", () -> IForgeMenuType.create((id, inv, data) -> new ContainerMissileSilo(id, inv)));
	public static final RegistryObject<MenuType<ContainerSAMTurret>> CONTAINER_SAMTURRET = MENU_TYPES.register("samturret", () -> IForgeMenuType.create((id, inv, data) -> new ContainerSAMTurret(id, inv)));
	public static final RegistryObject<MenuType<ContainerFireControlRadar>> CONTAINER_FIRECONTROLRADAR = MENU_TYPES.register("firecontrolradar", () -> IForgeMenuType.create((id, inv, data) -> new ContainerFireControlRadar(id, inv)));
	public static final RegistryObject<MenuType<ContainerSearchRadar>> CONTAINER_SEARCHRADAR = MENU_TYPES.register("radar", () -> IForgeMenuType.create((id, inv, data) -> new ContainerSearchRadar(id, inv)));
	public static final RegistryObject<MenuType<ContainerESMTower>> CONTAINER_ESMTOWER = MENU_TYPES.register("esmtower", () -> IForgeMenuType.create((id, inv, data) -> new ContainerESMTower(id, inv)));
	public static final RegistryObject<MenuType<ContainerCIWSTurret>> CONTAINER_CIWSTURRET = MENU_TYPES.register("ciwsturret", () -> IForgeMenuType.create((id, inv, data) -> new ContainerCIWSTurret(id, inv)));
	public static final RegistryObject<MenuType<ContainerLaserTurret>> CONTAINER_LASERTURRET = MENU_TYPES.register("laserturret", () -> IForgeMenuType.create((id, inv, data) -> new ContainerLaserTurret(id, inv)));
	public static final RegistryObject<MenuType<ContainerRailgunTurret>> CONTAINER_RAILGUNTURRET = MENU_TYPES.register("railgunturret", () -> IForgeMenuType.create((id, inv, data) -> new ContainerRailgunTurret(id, inv)));
    public static final RegistryObject<MenuType<ContainerDesignator>> CONTAINER_DESIGNATOR = MENU_TYPES.register("designator", () -> IForgeMenuType.create((id, inv, data) -> new ContainerDesignator(id, inv)));
    public static final RegistryObject<MenuType<ContainerBigDartPod>> CONTAINER_BIGDARTPOD = MENU_TYPES.register("bigdartpod", () -> IForgeMenuType.create((id, inv, data) -> new ContainerBigDartPod(id, inv)));
    public static final RegistryObject<MenuType<ContainerRangeDesignator>> CONTAINER_RANGEDESIGNATOR = MENU_TYPES.register("rangedesignator", () -> IForgeMenuType.create((id, inv, data) -> new ContainerRangeDesignator(id, inv)));

}
