# Adding a Custom Missile Type

This guide explains how to add a new missile type to Ballistix using the dynamic missile registration system.

## 1. Item Registration

To add a new missile, use the `BallistixMissiles.registerMissile` method. This handles creating the `ItemMissile` instance, registering it with Forge, and mapping it to its metadata.

### Registration Method
```java
public static final RegistryObject<Item> MY_CUSTOM_MISSILE = BallistixMissiles.registerMissile(
    "my_custom_missile", // Registry ID
    5000,                // Range in blocks (-1 for unlimited)
    1.5f,                // Speed Modifier (1.0f is default)
    new ResourceLocation("mymod", "entity/my_custom_missile"), // Model location
    0.5f, 6.0f, 0.5f,    // Scale X, Y, Z
    0.0f, 0.0f, 0.0f     // Translation Offset X, Y, Z
);
```

## 2. Client-Side Model Registration (CRITICAL)

For custom models to render, they must be registered as additional models during the `RegisterAdditional` model event.

In your `ClientRegister` class (or equivalent), add your model to the `onModelEvent`:

```java
// Define the ResourceLocation constant
public static final ResourceLocation MODEL_MY_CUSTOM_MISSILE = new ResourceLocation("mymod", "entity/my_custom_missile");

@SubscribeEvent
public static void onModelEvent(RegisterAdditional event) {
    // ... other registrations
    event.register(MODEL_MY_CUSTOM_MISSILE);
}
```

## 3. Localization

Add the following to your `en_us.json`:
- `item.yourmod.my_custom_missile`: "My Custom Missile"

## 4. Key Notes

- **Speed Modifier**: Increases the travel speed of the missile. A value of `2.0f` makes it twice as fast as a standard missile.
- **Model Matching**: The system uses the item's registry name to synchronize the correct model and scaling data between the server and client.
- **Silo & Entity Rendering**: Missiles will automatically use the scaling and translation provided during registration both when sitting in a silo and during flight.
