#!/usr/bin/env python3
"""Replace javax.annotation imports with org.jetbrains.annotations"""

import os
import re

def process_file(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Replace javax.annotation imports
        content = re.sub(r'import javax\.annotation\.Nullable;', 'import org.jetbrains.annotations.Nullable;', content)
        content = re.sub(r'import javax\.annotation\.Nonnull;', 'import org.jetbrains.annotations.Nonnull;', content)
        
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
    
    print("Replacing javax.annotation with org.jetbrains.annotations...")
    
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
