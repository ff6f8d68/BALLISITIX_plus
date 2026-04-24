# Adding a Custom Explosive Type

This guide explains how to register any block as a custom explosive for Ballistix.

## 1. Explosive Registration

To register a block as an explosive, use the `BallistixExplosives.registerExplosive` method.

### Simple Radius Explosion
Registers a block that creates a standard vanilla-style explosion when triggered.
```java
// Register TNT as an explosive with 80 tick fuse and radius 4.0
BallistixExplosives.registerExplosive(Blocks.TNT, 80, 4.0f);
```

### Custom Trigger Logic
Registers a block with a custom callback function that runs when the fuse expires.
```java
BallistixExplosives.registerExplosive(MyBlocks.CUSTOM_NUKE.get(), 100, (level, pos) -> {
    // Custom logic (e.g., spawn particles, custom blast class, etc.)
    new MyCustomBlast(level, pos).performExplosion();
});
```

## 2. Triggering the Explosive

### From a Missile
Addons can use these custom explosives in the Missile Silo by placing the registered block in the explosive slot.

### Manually in Code
You can manually trigger the primed version of a custom explosive using:
```java
BlockExplosive.explode(level, pos, MyBlocks.CUSTOM_NUKE.get());
```

## 3. Integration Details

- **Silo Compatibility**: Any block registered via `BallistixExplosives` will be accepted by the Missile Silo's warhead slot.
- **Defusing**: Custom explosives can be defused using the Ballistix Defuser. It will drop the original block upon successful defusal.
- **Prime Behavior**: When ignited (redstone or fire), the block will turn into a primed entity (`EntityExplosive`) that ticks down its fuse before executing the `onTriggered` callback.
- **Syncing**: The system automatically handles syncing the custom block type between server and client for the primed entity.
