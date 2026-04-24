#!/bin/bash

# Script to migrate Forge imports to NeoForge imports
# Run this from the project root directory

echo "Starting Forge to NeoForge import migration..."

# Find all Java files
find src/main/java -name "*.java" -type f | while read -r file; do
    # Replace net.minecraftforge imports with net.neoforged.neoforge
    sed -i 's/import net\.minecraftforge\.api\./import net.neoforged.neoforge.api./g' "$file"
    sed -i 's/import net\.minecraftforge\.common\./import net.neoforged.neoforge.common./g' "$file"
    sed -i 's/import net\.minecraftforge\.event\./import net.neoforged.neoforge.event./g' "$file"
    sed -i 's/import net\.minecraftforge\.fml\./import net.neoforged.fml./g' "$file"
    sed -i 's/import net\.minecraftforge\.network\./import net.neoforged.neoforge.network./g' "$file"
    sed -i 's/import net\.minecraftforge\.registries\./import net.neoforged.neoforge.registries./g' "$file"
    sed -i 's/import net\.minecraftforge\.client\./import net.neoforged.neoforge.client./g' "$file"
    
    # Special cases
    sed -i 's/import net\.minecraftforge\.eventbus\.api\./import net.neoforged.bus.api./g' "$file"
    sed -i 's/FMLJavaModLoadingContext/ModContainer/g' "$file"
    sed -i 's/FMLClientSetupEvent/FMLClientSetupEvent/g' "$file"
    sed -i 's/FMLCommonSetupEvent/FMLCommonSetupEvent/g' "$file"
    
    echo "Updated: $file"
done

echo "Migration complete!"
