#!/usr/bin/env python3
"""
Automated fix for all Electrodynamics API imports in Ballistix
Replaces all electrodynamics.* imports with NeoForge/Minecraft standard API
"""

import os
import re

SRC_DIR = 'src/main/java'
files_modified = 0

def fix_file(file_path):
    """Fix all Electrodynamics imports in a single file"""
    global files_modified
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original = content
        
        # Screen component files
        if '/prefab/screen/' in file_path:
            # Replace ScreenComponentGeneric with AbstractWidget
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.types\.ScreenComponentGeneric;\n',
                'import net.minecraft.client.gui.components.AbstractWidget;\n',
                content
            )
            content = content.replace('extends ScreenComponentGeneric', 'extends AbstractWidget')
            
            # Replace ITexture with ResourceLocation
            content = re.sub(
                r'import electrodynamics\.api\.screen\.ITexture;\n',
                'import net.minecraft.resources.ResourceLocation;\n',
                content
            )
            content = content.replace('ITexture', 'ResourceLocation')
            
            # Replace ScreenComponentEditBox with EditBox
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.editbox\.ScreenComponentEditBox;\n',
                'import net.minecraft.client.gui.components.EditBox;\n',
                content
            )
            content = content.replace('ScreenComponentEditBox', 'EditBox')
            
            # Replace ScreenComponentGuiTab
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.types\.guitab\.ScreenComponentGuiTab;\n',
                '',
                content
            )
            
            # Remove RenderingUtils import
            content = re.sub(
                r'import electrodynamics\.prefab\.utilities\.RenderingUtils;\n',
                '',
                content
            )
        
        # Client screen files
        if '/client/screen/' in file_path:
            # Replace GenericScreen with Screen
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.GenericScreen;\n',
                'import net.minecraft.client.gui.screens.Screen;\n',
                content
            )
            content = content.replace('extends GenericScreen<', 'extends Screen')
            
            # Remove gui tab imports
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.types\.guitab\.ScreenComponentElectricInfo;\n',
                '',
                content
            )
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.types\.guitab\.ScreenComponentGuiTab;\n',
                '',
                content
            )
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.utils\.AbstractScreenComponentInfo;\n',
                '',
                content
            )
            
            # Remove ChatFormatter imports
            content = re.sub(
                r'import electrodynamics\.api\.electricity\.formatting\.ChatFormatter;\n',
                '',
                content
            )
            content = re.sub(
                r'import electrodynamics\.api\.electricity\.formatting\.DisplayUnit;\n',
                '',
                content
            )
            
            # Replace ScreenComponentEditBox
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.editbox\.ScreenComponentEditBox;\n',
                'import net.minecraft.client.gui.components.EditBox;\n',
                content
            )
            content = content.replace('ScreenComponentEditBox', 'EditBox')
            
            # Remove wrapper imports
            content = re.sub(
                r'import electrodynamics\.prefab\.screen\.component\.types\.wrapper\.InventoryIOWrapper;\n',
                '',
                content
            )
        
        # Container files
        if '/inventory/container/' in file_path or ('/container/' in file_path and 'Container' in file_path):
            content = re.sub(
                r'import electrodynamics\.prefab\.inventory\.container\.GenericContainerBlockEntity;\n',
                'import net.minecraft.world.inventory.AbstractContainerMenu;\n',
                content
            )
            content = content.replace('extends GenericContainerBlockEntity<', 'extends AbstractContainerMenu')
        
        # Tile entity files
        if '/tile/' in file_path:
            # Replace GenericTile with BlockEntity
            content = re.sub(
                r'import electrodynamics\.prefab\.tile\.GenericTile;\n',
                'import net.minecraft.world.level.block.entity.BlockEntity;\n',
                content
            )
            content = content.replace('extends GenericTile', 'extends BlockEntity')
            
            # Remove all component imports
            content = re.sub(r'import electrodynamics\.prefab\.tile\.components\.IComponentType;\n', '', content)
            content = re.sub(r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentContainerProvider;\n', '', content)
            content = re.sub(r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentElectrodynamic;\n', '', content)
            content = re.sub(r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentPacketHandler;\n', '', content)
            content = re.sub(r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentTickable;\n', '', content)
            content = re.sub(r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentInventory;\n', '', content)
            content = re.sub(r'import electrodynamics\.prefab\.tile\.components\.type\.ComponentInventory\.InventoryBuilder;\n', '', content)
            
            # Remove Property imports
            content = re.sub(r'import electrodynamics\.prefab\.properties\.Property;\n', '', content)
            content = re.sub(r'import electrodynamics\.prefab\.properties\.PropertyType;\n', '', content)
            
            # Remove capability imports
            content = re.sub(r'import electrodynamics\.api\.capability\.ElectrodynamicsCapabilities;\n', '', content)
            
            # Remove SoundAPI
            content = re.sub(r'import electrodynamics\.api\.sound\.SoundAPI;\n', '', content)
        
        # Common replacements for ALL files
        # Replace Color with int constants
        content = re.sub(r'import electrodynamics\.prefab\.utilities\.math\.Color;\n', '', content)
        content = content.replace('Color.WHITE', '0xFFFFFFFF')
        content = content.replace('Color.BLACK', '0xFF000000')
        content = content.replace('Color.RED', '0xFFFF0000')
        content = content.replace('Color.GREEN', '0xFF00FF00')
        content = content.replace('Color.BLUE', '0xFF0000FF')
        content = content.replace('Color.GRAY', '0xFF808080')
        content = content.replace('Color.DARK_GRAY', '0xFF404040')
        content = content.replace('Color.LIGHT_GRAY', '0xFFC0C0C0')
        content = content.replace('.color()', '')
        
        # Remove ElectrodynamicsTextUtils
        content = re.sub(r'import electrodynamics\.prefab\.utilities\.ElectroTextUtils;\n', '', content)
        
        if content != original:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            files_modified += 1
            return True
        return False
        
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

def main():
    global files_modified
    
    print("=" * 70)
    print("Automated Electrodynamics API Removal")
    print("=" * 70)
    print()
    
    # Process all Java files
    for root, dirs, files in os.walk(SRC_DIR):
        for file in sorted(files):
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                if fix_file(file_path):
                    print(f"✓ Fixed: {file_path}")
    
    print()
    print("=" * 70)
    print(f"Total files modified: {files_modified}")
    print("=" * 70)
    print()
    print("NOTE: This script only fixes imports. Manual fixes still needed for:")
    print("  - Tile entity constructors and component system")
    print("  - Property system replacement")
    print("  - Screen rendering methods")
    print("  - Container menu patterns")
    print()

if __name__ == '__main__':
    main()
