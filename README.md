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

## TODO
- Explain how everything works in detail
- Make a pairing system between corresponding quotes and POs