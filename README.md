## SMART-TIME Intelligent Task & Study Planner

SmartTime is a lightweight JavaFX-based task management tool built for students to plan, organize, and track academic work efficiently. 

## Setup
- To set up the project:
1. Install Java JDK 25 for your OS from the official website
2. Install JavaFX SDK 25 for your OS
   1. macOS Apple Silicon: javafx-sdk-25.x-osx-aarch64.zip
   2. macOS Intel: osx-x64
   3. Windows: win-x64
4. Clone this project via Git
5. Import this project in Eclipse 
6. Right click on the project folder (smarttime) from the project explorer view.
7. Go to Build Path -> Libraries -> Ensure JRE System Library is pointing to Java SE 25.
8. Click on "Modulepath" option -> Add Library -> User Library -> User Libraries -> New -> Give a name (JavaFXLibraries)
9. Select JavaFXLibraries -> Add External JARs
10. Navigate to downloaded JavaFX SDK lib folder:/Users/<your-username>/javafx-sdk-25.0.1/lib
11. Select all the javafx-*.jar files and click Open. -> Click Apply and Close

- To run the project:
1. Right click the project folder (smarttime) from the project explorer view.
2. Select Run As > Run Configurations....
3. Create a new configuration by clicking the "New launch configuration" icon present in the left side menu on the top.
4. Give a name to the configuration and select the correct project - "smarttime"
5. For the "Main class", use the "Search..." button & select "Main - application" and click "OK".
7. Now, on the "Arguments" tab, under "VM Arguments", paste this (adjust the path): "--module-path /Users/<your_username>/javafx-sdk-25.0.1/lib --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics"
8. Select the second option only "Use the -XX:+ShowCodeDetails...."
9. Click "Apply" & then "Run".
11. The project should now be running.
