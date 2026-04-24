#!/usr/bin/env python3
"""
Migrate Forge imports to NeoForge imports for Minecraft 1.21.1
"""

import os
import re

# Mapping of Forge imports to NeoForge imports
IMPORT_REPLACEMENTS = [
    # API packages
    (r'import net\.minecraftforge\.api\.distmarker\.', 'import net.neoforged.api.distmarker.'),
    (r'import net\.minecraftforge\.api\.', 'import net.neoforged.neoforge.api.'),
    
    # Common packages
    (r'import net\.minecraftforge\.common\.capabilities\.', 'import net.neoforged.neoforge.capabilities.'),
    (r'import net\.minecraftforge\.common\.util\.', 'import net.neoforged.neoforge.common.util.'),
    (r'import net\.minecraftforge\.common\.', 'import net.neoforged.neoforge.common.'),
    
    # Event bus
    (r'import net\.minecraftforge\.eventbus\.api\.', 'import net.neoforged.bus.api.'),
    
    # FML
    (r'import net\.minecraftforge\.fml\.common\.Mod', 'import net.neoforged.fml.common.Mod'),
    (r'import net\.minecraftforge\.fml\.event\.lifecycle\.', 'import net.neoforged.fml.event.lifecycle.'),
    (r'import net\.minecraftforge\.fml\.javafmlmod\.FMLJavaModLoadingContext', 'import net.neoforged.fml.ModContainer'),
    (r'import net\.minecraftforge\.fml\.', 'import net.neoforged.fml.'),
    
    # Events
    (r'import net\.minecraftforge\.event\.', 'import net.neoforged.neoforge.event.'),
    
    # Network
    (r'import net\.minecraftforge\.network\.', 'import net.neoforged.neoforge.network.'),
    
    # Registries  
    (r'import net\.minecraftforge\.registries\.', 'import net.neoforged.neoforge.registries.'),
    
    # Client
    (r'import net\.minecraftforge\.client\.', 'import net.neoforged.neoforge.client.'),
    
    # Server
    (r'import net\.minecraftforge\.server\.', 'import net.neoforged.neoforge.server.'),
]

# Code pattern replacements
CODE_REPLACEMENTS = [
    # Replace FMLJavaModLoadingContext.get().getModEventBus() with modEventBus parameter
    (r'FMLJavaModLoadingContext\.get\(\)\.getModEventBus\(\)', 'modEventBus'),
    (r'FMLJavaModLoadingContext\.get\(\)', 'modContainer'),
    
    # Constructor signature changes
    (r'public\s+(\w+)\s*\(\s*\)\s*{', lambda m: None),  # Handled separately
]

def process_file(file_path):
    """Process a single Java file and replace Forge imports with NeoForge"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Apply import replacements
        for pattern, replacement in IMPORT_REPLACEMENTS:
            content = re.sub(pattern, replacement, content)
        
        # Apply code replacements
        for pattern, replacement in CODE_REPLACEMENTS:
            if callable(replacement):
                continue  # Skip complex replacements
            content = re.sub(pattern, replacement, content)
        
        # Only write if changes were made
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            return True
        return False
        
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

def main():
    src_dir = 'src/main/java'
    count = 0
    
    print("Starting Forge to NeoForge migration...")
    
    for root, dirs, files in os.walk(src_dir):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                if process_file(file_path):
                    print(f"✓ Updated: {file_path}")
                    count += 1
    
    print(f"\nMigration complete! Updated {count} files.")

if __name__ == '__main__':
    main()
