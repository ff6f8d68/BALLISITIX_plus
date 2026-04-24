#!/usr/bin/env python3
"""
Comprehensive fix for Ballistix NeoForge 1.21.1 migration
Replaces all Electrodynamics 1.20.x API calls with NeoForge 1.21.1 standard API
"""

import os
import re

SRC_DIR = 'src/main/java'
files_modified = 0
total_replacements = 0

def apply_replacements(file_path, replacements):
    """Apply regex replacements to a file"""
    global files_modified, total_replacements
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original = content
        count = 0
        
        for pattern, replacement in replacements:
            new_content = re.sub(pattern, replacement, content)
            if new_content != content:
                count += 1
                content = new_content
        
        if content != original:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            files_modified += 1
            total_replacements += count
            print(f"✓ Fixed: {file_path} ({count} changes)")
            return True
        return False
    except Exception as e:
        print(f"✗ Error processing {file_path}: {e}")
        return False

def fix_file(file_path):
    """Apply all necessary fixes to a Java file"""
    
    # Common replacements for ALL files
    replacements = [
        # Remove Color import
        (r'import electrodynamics\.prefab\.utilities\.math\.Color;\n', ''),
        
        # Replace Color constants with ARGB integers
        (r'\bColor\.WHITE\b', '0xFFFFFFFF'),
        (r'\bColor\.BLACK\b', '0xFF000000'),
        (r'\bColor\.RED\b', '0xFFFF0000'),
        (r'\bColor\.GREEN\b', '0xFF00FF00'),
        (r'\bColor\.BLUE\b', '0xFF0000FF'),
        (r'\bColor\.GRAY\b', '0xFF808080'),
        (r'\bColor\.DARK_GRAY\b', '0xFF404040'),
        (r'\bColor\.LIGHT_GRAY\b', '0xFFC0C0C0'),
        (r'\bColor\.YELLOW\b', '0xFFFFFF00'),
        (r'\bColor\.ORANGE\b', '0xFFFFA500'),
        
        # Remove .color() calls
        (r'(\w+)\.color\(\)', r'\1'),
    ]
    
    # Screen component files
    if 'prefab/screen/' in file_path or 'client/screen/' in file_path:
        replacements.extend([
            # Replace AbstractScreenComponent
            (r'import electrodynamics\.prefab\.screen\.component\.AbstractScreenComponent;\n',
             'import net.minecraft.client.gui.components.AbstractWidget;\n'),
            (r'extends AbstractScreenComponent', 'extends AbstractWidget'),
            
            # Replace ScreenComponentGeneric
            (r'import electrodynamics\.prefab\.screen\.component\.types\.ScreenComponentGeneric;\n',
             'import net.minecraft.client.gui.components.AbstractWidget;\n'),
            (r'extends ScreenComponentGeneric', 'extends AbstractWidget'),
            
            # Replace ITexture with ResourceLocation
            (r'import electrodynamics\.api\.screen\.ITexture;\n',
             'import net.minecraft.resources.ResourceLocation;\n'),
            (r'\bITexture\b', 'ResourceLocation'),
            
            # Remove GenericScreen import (will be replaced with Screen)
            (r'import electrodynamics\.prefab\.screen\.GenericScreen;\n', ''),
        ])
    
    # Tile entity files
    if '/tile/' in file_path:
        replacements.extend([
            # Replace GenericTile
            (r'import electrodynamics\.prefab\.tile\.GenericTile;\n',
             'import net.minecraft.world.level.block.entity.BlockEntity;\n'),
            (r'extends GenericTile', 'extends BlockEntity'),
            
            # Remove component imports (will use NeoForge capabilities)
            (r'import electrodynamics\.prefab\.tile\.components\.IComponentType;\n', ''),
            (r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentContainerProvider;\n', ''),
            (r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentElectrodynamic;\n', ''),
            (r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentPacketHandler;\n', ''),
            (r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentTickable;\n', ''),
            (r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentInventory;\n', ''),
            (r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentInventory\.InventoryBuilder;\n', ''),
            
            # Replace Property system
            (r'import electrodynamics\.prefab\.properties\.Property;\n', ''),
            (r'import electrodynamics\.prefab\.properties\.PropertyType;\n', ''),
            
            # Replace ElectrodynamicsCapabilities
            (r'import electrodynamics\.api\.capability\.ElectrodynamicsCapabilities;\n', ''),
            
            # Replace SoundAPI
            (r'import electrodynamics\.api\.sound\.SoundAPI;\n', ''),
        ])
    
    # Container files
    if '/inventory/container/' in file_path or '/container/' in file_path:
        replacements.extend([
            # Replace GenericContainerBlockEntity
            (r'import electrodynamics\.prefab\.inventory\.container\.GenericContainerBlockEntity;\n',
             'import net.minecraft.world.inventory.AbstractContainerMenu;\n'),
            (r'extends GenericContainerBlockEntity<', 'extends AbstractContainerMenu'),
        ])
    
    # Screen files (client/screen/)
    if 'client/screen/' in file_path:
        replacements.extend([
            # Remove GenericScreen and related
            (r'import electrodynamics\.prefab\.screen\.GenericScreen;\n',
             'import net.minecraft.client.gui.screens.Screen;\n'),
            (r'extends GenericScreen<', 'extends Screen'),
            
            # Remove gui tab components
            (r'import electrodynamics\.prefab\.screen\.component\.types\.guitab\.ScreenComponentElectricInfo;\n', ''),
            (r'import electrodynamics\.prefab\.screen\.component\.types\.guitab\.ScreenComponentGuiTab;\n', ''),
            (r'import electrodynamics\.prefab\.screen\.component\.utils\.AbstractScreenComponentInfo;\n', ''),
            
            # Remove editbox import
            (r'import electrodynamics\.prefab\.screen\.component\.editbox\.ScreenComponentEditBox;\n', ''),
            
            # Remove wrapper imports
            (r'import electrodynamics\.prefab\.screen\.component\.types\.wrapper\.InventoryIOWrapper;\n', ''),
            
            # Remove ChatFormatter
            (r'import electrodynamics\.api\.electricity\.formatting\.ChatFormatter;\n', ''),
            (r'import electrodynamics\.api\.electricity\.formatting\.DisplayUnit;\n', ''),
        ])
    
    return apply_replacements(file_path, replacements)

def main():
    global files_modified, total_replacements
    
    print("=" * 70)
    print("Ballistix NeoForge 1.21.1 API Migration")
    print("Replacing Electrodynamics 1.20.x API with standard NeoForge API")
    print("=" * 70)
    print()
    
    # Walk through all Java files
    for root, dirs, files in os.walk(SRC_DIR):
        for file in sorted(files):
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                fix_file(file_path)
    
    print()
    print("=" * 70)
    print(f"Migration Summary:")
    print(f"  Files modified: {files_modified}")
    print(f"  Total replacements: {total_replacements}")
    print("=" * 70)
    print()
    print("IMPORTANT: Manual fixes still required for:")
    print("  1. Tile entity constructors - change to BlockEntity pattern")
    print("  2. Remove addComponent() calls - use NeoForge capabilities")
    print("  3. Replace Property<T> fields with data tracking")
    print("  4. Update screen render methods")
    print("  5. Fix container menu patterns")
    print()

if __name__ == '__main__':
    main()
