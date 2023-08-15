# Company PO/Quote Parser
Written by Davey

## How to use this program
Currently this program is not in a runnable state, however it is easy to make it so. I want it to be in a finished state before making it a separate executable, and hardcoded filepaths are easy to change.  
Regardless, the inner workings are not *TOO* complicated. I am still relatively new to programming as a whole and java, so some of my code is definitely not as readable or efficient as it could be. A lot of the methods I wrote are abstractions for myself, so I can focus on the main task: pulling key data from the PO and Quote PDFs. I will probably elaborate on the specifics another time.

## Dependencies
- [Tabula](https://github.com/tabulapdf/tabula-java)
  - [PDFBox](https://github.com/apache/pdfbox)
  - [FontBox](https://mvnrepository.com/artifact/org.apache.pdfbox/fontbox)
  - [jai-imageio](https://github.com/jai-imageio)
  - [gson](https://github.com/google/gson)
  - [jj2000](https://code.google.com/p/jj2000)
  - A lot of [bouncycastle](https://mvnrepository.com/artifact/org.bouncycastle)'s stuff idk I can't find links to their repos
  - [JTS](https://github.com/locationtech/jts)
  - [SLF4J](https://mvnrepository.com/artifact/org.slf4j) (wow I should use this)
- [pdftotext](https://github.com/spatie/pdf-to-text) (you need to install this with WSL on windows or just normally on linux/macos)

## Troubleshooting
If something went wrong while running this and you are not Davey, uhh I'll change this in the actual release but for the public version I will not put my real professional contact info on here

## License
Haven't decided yet :P  
Probably GPL v3.0  
I *might* pick something from the [Anti-License Manifesto](https://www.boringcactus.com/2021/09/29/anti-license-manifesto.html) if the higher-ups let me

## Installing stuff
### wsl and pdftotext  
Open Terminal (aka Command Prompt/Windows Powershell). Type `wsl` and agree to install it. Once it installs, restart your computer. Open Terminal again and type `wsl` again. Then run `sudo apt-get update && sudo apt-get install -y xpdf` and enter your password when it prompts you. Type `which pdftotext` (or `wsl which pdftotext` if you're not in wsl) to confirm you have it installed.

### Python:
Type `python` into Terminal and it will prompt you to install it from Microsoft's store. Do that. Then restart your computer. Type `python --version` to see if it installed correctly.

### pip:
If you type `pip` into Terminal and it gives you an error, do the following. Go to [this](https://bootstrap.pypa.io/get-pip.py) website, right-click it, and "save as". Then run `python Downloads/get-pip.py`. It will tell you to add a filepath to "PATH", so open Search, type "advanced system settings", and open the corresponding app for it. In the bottom right of the window there should be a button that says "Environment Variables...", so click on it. In the "System variables" section, look for "Path" and double-click it. Add the path they provided in quotes when you ran the pip installation command (it will look like `C:\Users\<YOURNAME>\AppData\Local\Programs\Python\Python311\Scripts`). Press OK three times to save your edits and close out of the app you opened. Relaunch Terminal and type `pip` to make sure it installed. If it isn't, find an online tutorial to help you idk.

### eml-extractor:
You should now be able to run `pip install eml-extractor` without any problems. After it installed, type `eml-extractor` to see if it installed correctly. If it doesn't work, try again after closing and reopening Terminal, and if that doesn't work, try restarting your computer. 

### java
It never ends! Download java 17 (not 8, 11, or 20) from [here](https://www.oracle.com/java/technologies/downloads/#java17). Make sure JDK 17 is selected, and so is Windows. Download the x64 MSI installer, and double-click it to open it once it has finished downloading. Do what it says to do and restart your computer after it finishes installing. Run `java -version` to see if it installed correctly. 

## Prep for running
### eml-extractor
If you don't have the emls extracted already (the emls contain the quotes and POs), run `eml-extractor --source <eml folder> --destination <pdfs folder>`, making sure to configure the filepaths correctly. 

### File structure
Your file structure should look like the following:  
![The file structure](https://i.imgur.com/jEY7Hac.png)  
Inside of the inputs folder, there should be a pdfs folder. Put all of your folders with pdfs inside of there, so it looks like this:  
![Another file structure thing](https://i.imgur.com/wgNcAtu.png)  
Here's what the inside of one of these folders looks like:  
![Yet another file structure](https://i.imgur.com/0hTCvgJ.png)  
So long as the structure is exactly like this, my program should be able to work properly (in the sense that it recognizes that the files exist).

## Running the program
We finally got there! Open Terminal, type `cd` and a space, and drag the jar file onto the window (which will auto-fill its path). Delete Parser.jar from the end. If your filepath has spaces in it, put doublequotes around the entire filepath. Press enter. Then run `ls` and make sure that you see Parser.jar in the list. If so, run `java -jar Parser.jar`. If it gives you an error about "FileNotFoundException", you have the filepath wrong. Make sure you are directly in the folder that has the jar file. If the program works, check `POs.csv` and `Quotes.csv` in the outputs folder after it has run, as well as `filesread.txt` to see what files had and had not been read.  
If you need to rerun the program, just press the up key in Terminal to load the last used commands. You will need to do this entire section again whenever you close Terminal.
If at any point you would like to interrupt the program from running, press Ctrl + C in the terminal.