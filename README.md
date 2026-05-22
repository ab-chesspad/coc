# clockofclocks

![Clock of Clocks](https://github.com/ab-chesspad/clockofclocks/blob/main/coc-20230727.gif)  

Here is a short video giving an idea about 
the program capabilities:  
https://drive.google.com/file/d/1N1uBtxdvWi3pLbsjTWJYRlPOgPWmvSR6

**Installation**  
The program can be used on any platform supporting the standard Java,
e.g. Windows, macOS, Linux.  
To run the program you need JRE (1.8 and up), that can be installed at
https://www.java.com/en/download/manual.jsp

The program can be found here:  
https://github.com/ab-chesspad/coc/raw/refs/heads/main/target/coc.jar  

Android version is here:
https://github.com/ab-chesspad/coc/raw/refs/heads/main/  

It runs on Android phones, Android and Google TVs and boxes.  

**Launch Java program**  
To launch the program open Terminal (cmd.com on Windows) and type:  
**java -jar \<path to coc.jar file> [--root]**  
When the optional parameter '--root' is specified, the program runs in the 
full screen mode.

**Usage**  
Mouse right-click pops up the configuration screen. Depending on the
currently selected options, some parameters become disabled.
The program has four fixed themes and one custom theme. Fixed themes:  
**BL** - black hands, light bg, no dials;  
**BLL** - black hands, light bg, light dials;  
**GD** - gold hands, dark bg, no dials;  
**WDD** - white hands, dark bg, dark dials;  

**Custom** - most parameters are for this theme. It is recommended to
disable all fixed themes while configuring the custom one. This way
the user immediately sees all selected options in WYSIWYG mode.  

Mouse left-click closes the program.

**Using as screensaver**  
First, you have to run the program as described in **Launch** section
and set up the desired configuration,
because in the screensaver mode it is not possible.  

**Ubuntu**  
Install xscreensaver if you do not have it.  
In ~/.xscreensaver file right after the line  
`
programs:
`

`
  Best: "coc" /usr/bin/java -jar <path to coc.jar> -root \n\
`

Important to enter the name wrapped in quotation marks, e.g. "coc".  
Run:  
`xscreensaver-demo`  

and select coc as your screensaver

**MacOS**  
Not feasible.

**Windows**  
**to do ...**



**How to build**  
It is possible to build the program from the source code with Maven.  
Download or clone the project, navigate to its folder and type:  
**mvn package**  

**A bit of history**  
When I saw the clock made by Humans since 1982 on the wall, I was mesmerized. Then I discovered its Java implementation 
https://github.com/colugo/finless-porpoise.git 
with a very interesting algorithm.  
Unfortunately it was not without issues:
1. The window size is hard-coded.
2. I was not able to build the project in IntelliJ. I also was not able to build using Maven 
because of a nonstandard file structure.
3. The program eventually goes into an infinite loop without showing time.
4. The code is not really OO.
5. The whole code is heavily dependent on Swing. I'd like to see it split so that UI part is separated 
from the algorithm. Then it will be easier to port it 
to other platforms, e.g. Android.  

I posted an issue, but the author did not respond. 
So here is my own implementation,
free of these defects, with a broad set of options.  
  
Enjoy!
