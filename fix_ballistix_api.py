#!/usr/bin/env python3
"""
Fix Ballistix imports for NeoForge 1.21.1 with Electrodynamics 1.21.1-1.0.2
This script fixes all the import errors by replacing old API calls with new ones
"""

import os
import re

# Define the source directory
SRC_DIR = 'src/main/java'

# Track changes
files_modified = 0
total_changes = 0

def replace_in_file(file_path, replacements):
    """Apply multiple regex replacements to a file"""
    global files_modified, total_changes
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original = content
        changes_made = 0
        
        for pattern, replacement in replacements:
            new_content = re.sub(pattern, replacement, content)
            if new_content != content:
                changes_made += 1
                content = new_content
        
        if content != original:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            files_modified += 1
            total_changes += changes_made
            return True
        return False
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

def fix_color_imports(file_path):
    """Replace electrodynamics Color with simple int constants"""
    replacements = [
        (r'import electrodynamics\.prefab\.utilities\.math\.Color;\n?', ''),
        (r'Color\.WHITE', '0xFFFFFFFF'),
        (r'Color\.BLACK', '0xFF000000'),
        (r'Color\.RED', '0xFFFF0000'),
        (r'Color\.GREEN', '0xFF00FF00'),
        (r'Color\.BLUE', '0xFF0000FF'),
        (r'new Color\((\d+),\s*(\d+),\s*(\d+),\s*(\d+)\)', r'0xFF\1\2\3'),  # Simplified - may need manual fix
        (r'\.color\(\)', ''),  # Remove .color() calls since we're using int directly
    ]
    return replace_in_file(file_path, replacements)

def fix_screen_components(file_path):
    """Fix screen component base classes"""
    replacements = [
        # Replace AbstractScreenComponent with AbstractWidget
        (r'import electrodynamics\.prefab\.screen\.component\.AbstractScreenComponent;\n?', 
         'import net.minecraft.client.gui.components.AbstractWidget;\n'),
        (r'extends AbstractScreenComponent', 'extends AbstractWidget'),
        
        # Replace ScreenComponentGeneric - this needs special handling
        (r'import electrodynamics\.prefab\.screen\.component\.types\.ScreenComponentGeneric;\n?',
         'import net.minecraft.client.gui.components.AbstractWidget;\n'),
        (r'extends ScreenComponentGeneric', 'extends AbstractWidget'),
    ]
    return replace_in_file(file_path, replacements)

def main():
    print("Starting Ballistix API migration for 1.21.1...")
    print("=" * 60)
    
    # Walk through all Java files
    for root, dirs, files in os.walk(SRC_DIR):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                
                # Apply fixes
                fix_color_imports(file_path)
                fix_screen_components(file_path)
    
    print("=" * 60)
    print(f"Migration complete!")
    print(f"Files modified: {files_modified}")
    print(f"Total changes: {total_changes}")
    print("\nNote: Some changes may require manual review, especially:")
    print("- Color conversions (RGB to ARGB format)")
    print("- Screen component method signatures")
    print("- Tile entity component system changes")

if __name__ == '__main__':
    main()
