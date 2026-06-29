import os, glob, re

directory = r"C:\Users\m1ch3\OneDrive\Documentos\NetBeansProjects\internship-program-system\internship-program-system\src\main\java\mx\uv\internshipprogramsystem\gui\controllers"

for filepath in glob.glob(os.path.join(directory, "*.java")):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Check if there is catch (Exception
    if 'catch (Exception' in content or 'catch (Throwable' in content:
        # replace catch (Exception e) with catch (RuntimeException e)
        new_content = re.sub(r'catch\s*\(\s*Exception\s+(\w+)\s*\)', r'catch (RuntimeException \1)', content)
        new_content = re.sub(r'catch\s*\(\s*Throwable\s+(\w+)\s*\)', r'catch (RuntimeException \1)', new_content)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print("Updated " + os.path.basename(filepath))
