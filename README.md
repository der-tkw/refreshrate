# Refresh Rate
- Simple tray icon application
- Shows the refresh rate of the currently selected display in the tray icon
- Stores last selected display
- Allows to change the refresh rate of any display (via ChangeScreenResolution.exe tool)
- Only tested with Windows 11

### Requirements
- JRE/JDK installed and `JAVA_HOME` environment variable properly set 
- [ChangeScreenResolution](http://tools.taubenkorb.at/change-screen-resolution/) must be installed on your system and selected when the app starts for the first time

### Known Issues
- Menu gets stale when periodic update (every 5 minutes) is triggered
- Display names cannot be properly fetched and are thus not displayed at the moment (if you have a way, let me know)

### Screenshots
![Tray Icon + Tooltip](https://i.imgur.com/k5q4uhq.png)

![Tray Icon + Menu](https://i.imgur.com/AK7Oacx.png)