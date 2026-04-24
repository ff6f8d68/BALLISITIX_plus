#!/usr/bin/env python3
"""Fix NeoForge API changes for 1.21.1"""

import os
import re

def process_file(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Replace ForgeRegistries with BuiltInRegistries
        content = re.sub(r'import net\.neoforged\.neoforge\.registries\.ForgeRegistries;', 
                        'import net.minecraft.core.registries.BuiltInRegistries;', content)
        
        # Replace ForgeRegistries usage with BuiltInRegistries
        content = re.sub(r'ForgeRegistries\.', 'BuiltInRegistries.', content)
        
        # Fix EventBusSubscriber - in NeoForge it's just @EventBusSubscriber without .Bus
        # The import is correct, but usage might need adjustment
        
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
    
    print("Fixing NeoForge API changes...")
    
    for root, dirs, files in os.walk(src_dir):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                if process_file(file_path):
                    print(f"✓ Updated: {file_path}")
                    count += 1
    
    print(f"\nComplete! Updated {count} files.")

if __name__ == '__main__':
    main()
