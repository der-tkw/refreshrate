# Refresh Rate
- Simple tray icon application
- Shows the refresh rate of the currently selected display
- Stores last selected display
- Allows to change the refresh rate of the currently selected display (via ChangeScreenResolution.exe tool)
- Only tested with Windows 11

### Requirements
- JRE/JDK installed and `JAVA_HOME` environment variable properly set 
- [ChangeScreenResolution](http://tools.taubenkorb.at/change-screen-resolution/) must be installed on your system

### Known Issues
- Menu gets stale when periodic update (every 5 minutes) is triggered
- Display names cannot be properly matched (AWT vs. Windows wmi) and are thus not displayed at the moment