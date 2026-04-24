import os

# Define the mapping of OLD strings to NEW strings
replacements = {
    # NeoForge/FML Refactors
    "net.neoforged.fml.common.Mod.EventBusSubscriber": "net.neoforged.fml.common.EventBusSubscriber",
    "net.neoforged.neoforge.common.MinecraftForge": "net.neoforged.neoforge.common.NeoForge",
    "net.neoforged.neoforge.event.TickEvent": "net.neoforged.neoforge.event.tick.ServerTickEvent",
    "net.neoforged.neoforge.network.NetworkHooks": "net.neoforged.neoforge.network.registration.PayloadRegistrar", # Closest equivalent

    # Common Event Fixes
    "event.phase == Phase.START": "false /* Refactored to .Post event */",
    "event.phase == Phase.END": "true",

    # Potential Voltaic/ED Case Sensitivity Fixes
    "voltaic.api.capability.voltaicCapabilities": "voltaic.api.capability.VoltaicCapabilities",

    # Annotation cleanup
    "org.jetbrains.annotations.Nonnull": "javax.annotation.Nonnull",
}

def process_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)

                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                original_content = content
                for old, new in replacements.items():
                    content = content.replace(old, new)

                if content != original_content:
                    with open(file_path, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"Updated: {file_path}")

if __name__ == "__main__":
    src_path = "./src"
    if os.path.exists(src_path):
        print("Starting global refactor...")
        process_files(src_path)
        print("Done! Try running ./gradlew compileJava now.")
    else:
        print("Error: 'src' folder not found in current directory.")