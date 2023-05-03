# Refresh Rate
- Simple tray icon application
- Shows the refresh rate of the configured display in the tray icon
- Remembers last selected display after restart
- Allows to change the refresh rate of any connected display
- Only tested with Windows 11

### Installation
- Install Java JRE/JDK in case it is not installed
- Ensure that `JAVA_HOME` is properly set
- Place RefreshRate.exe in some directory
- Download [ChangeScreenResolution](http://tools.taubenkorb.at/change-screen-resolution/) and place it next to RefreshRate.exe
- Start RefreshRate.exe and choose path to CSR
- (Optional) Put shortcut to RefreshRate.exe into your startup directory

### Known Issues
- Menu gets stale when periodic update (every 5 minutes) is triggered
- Display names cannot be properly fetched and are therefore not displayed

### Screenshots
![Tray Icon + Tooltip](https://i.imgur.com/k5q4uhq.png)

![Tray Icon + Menu](https://i.imgur.com/AK7Oacx.png)
