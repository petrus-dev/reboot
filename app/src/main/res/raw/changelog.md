# Reboot Changelog #

* version 2.0.6
  * Updated Russian translation by "Reboot_Master"
  * Italian translation by "devilz-wolit"

* version 2.0.5
  * Fix attempt for the soft reboot command which stopped working on some devices.
  * Support for Android 7.1.1

* version 2.0.4
  * Removed root check on start, to solve the problem of rooted devices not detected.
  * Fixed soft reboot for CM13 on OnePlus One (maybe other devices too).

* version 2.0.3
  * Turkish version by agabey_42

* version 2.0.2
  * Bug fix : Crash when detecting device properties on Xperia C3 Dual.

* version 2.0.1
  * Bug fix : incorrect detection of iMito QX1 device

* version 2.0.0
  * Open source : [https://github.com/petrus-dev/reboot](https://github.com/petrus-dev/reboot)
  * Support for Android 7.0
  * Better title design for old Android versions
  * Updated and cleaned up code
  * Better changelog and about pages formatting

* version 1.4.4
  * Update of the portuguese translation by ViperRunner
  * Support for Android 5.1.1
  * Added a "Reboot to download mode" button (hidden by default, you have to enable it in the options screen)

* version 1.4.3
  * Portuguese version by ViperRunner

* version 1.4.2
  * Fix for android 5.0.

* version 1.4.1
  * New launcher icon by tattman.
  * A few minor fixes.

* version 1.4.0
  * Changed package name to publish the app on Google Play
  * Added an option to remove the old app if present
  * Updated polish translation by dergov
  * French translation

* version 1.3.0
  * Updated root lib, using chainfire libsuperuser lib.
  * Added support for Android Lollipop.

* version 1.2.4
  * Updated polish translation by dregov
  * Switched from Eclipse to Android Studio IDE. I hope nothing was broken by the migration.

* version 1.2.3
  * Spanish translation by litry

* version 1.2.2
  * On some Rockchip 3188 devices running KitKat, rebooting to recovery didn't allow to reboot back to System. When one of these devices is detected, a simple "reboot recovery" command will be used.
  * You can force the old method, by setting the option 'Use "misc image" trick to reboot to recovery' to yes.

* version 1.2.1
  * Removed the asynchronous task, due to issues on some devices. Root commands are launched in the main thread.
  * Restored the root access check at startup.
  * New setting : you can now choose if you want to stop wifi before rebooting (WARNING : Selecting "no" on iMito QX1 is likely to cause a bootloop. Use at your own risk!)
  * New setting : you can now choose if you want to reboot all FileSystems Read-Only before rebooting. Enabling it may prevent FileSystem corruption, but can also freeze your system. It was enabled by default on 1.2.0 and caused some issues. Note that sdcard and external usb flash drives and HDD are still unmounted, if you disable this option.

* version 1.2.0
  * Added a quicker "Soft Reboot" command.
  * Fix : root commands are now executed in an asynchronous task, to prevent FCs.
  * Removed root test at startup, which caused some problems. The check is now none just before reboot.
  * New settings : you can now show or hide any buttons (except "Cancel"). So if for example "Reboot to bootloader" does not work on your device, the button can be hidden.

* version 1.1.2
  * Added a "Close" option in all menus, to close the current screen.
  * The fix introduced in v1.0.0 caused some side effects on other devices (airplane mode set on reboot). This fix is now used only on iMito QX1 device.  

* version 1.1.1
  * Bug Fix : Menu didn't show on some devices.

* version 1.1.0
  * Support Android versions from 1.5 to 4.4.
  * Added Holo Theme for Android versions 3.0+.
  * Menu button added : The About and Changelog pages are now visible.
  * Settings page with only one option at the moment : you can disable the confirmation dialogs.

* version 1.0.0 :
  * Try to stop wifi before rebooting, to prevent bootloop on the QX1 stick.
  * Added "ACCESS_SUPERUSER" permission to prevent newer versions of Superuser and SuperSU from complaining.
  * Norwegian translation by pr0xZen.

* version 0.9.3 :
  * Polish translation by XTeK1.

* version 0.9.2 :
  * Move reboot code to main shared library.

* version 0.9.1 :
  * Bug fix. Reboot function did not reboot anymore in v0.9.0

* version 0.9.0 :
  * Cleaner shutdown : Unmount SDCARD and USB device if present, then try to remount everything else read-only before rebooting or halting

* version 0.8.1 :
  * Android target version : latest JB

* version 0.8.0 :
  * Dropped support for Android versions before 2.3.1. For prior versions please use v0.7.0 or ask for specific version support.
  * Supporting only android 2.3.1 and up should make UI compatibility better on higher versions.

* version 0.7.0 :
  * Support for Elektrik Allwinner GingerBread ROM

* version 0.6.2 :
  * Bug fix for Allwinner tab detection

* version 0.6.1 :
  * Minor bug fixes for log messages.
  * Try to unmount everything before rebooting.
  * Bug fix for Allwinner.

* version 0.6.0 :
  * More russian translation.
  * For Rockchip 30 based tabs : Reboot to Recovery uses again the misc image, in addition to the "reboot recovery command", because some RK30 roms need it.
  * Support for Allwinner tabs
  * Bug fix : sdcard unmounting before reboot was broken in last version.

* version 0.5.0 :
  * New app icon.
  * Russian translation.
  * New function for Rockchip 30 based tabs : Reboot to Bootloader (flash mode). Not supported on Rockchip 29 tabs
  * For Rockchip 30 based tabs : Reboot to Recovery doesn't use the misc image, but simply the "reboot recovery command", to avoid unnecessary flashes.

* version 0.4.0 :
  * No more recovery script : commands are launched directly from the app.
  * No more file copy to /system, it is used directly from the app internal storage.
  * As a consequence, there is no more need to remount /system read-only

* version 0.3.0 :
  * Bug Fix : On some roms, the reboot to recovery script is never installed. Try to call the normal "reboot recovery" command in this case.
  * In the reboot-recovery script, also use the "reboot recovery" command at the end, instead of just "reboot"
  
* version 0.2.4 :
  * Bug Fix for ICS : Cleaner file copying.

* version 0.2.3 :
  * Added Logcat debug messages.

* version 0.2.2 :
  * FileSystem Utils bug fix.

* version 0.2.1 :
  * Updated "Root and FileSystem Utils Internal library" : mainly a bug fix.

* version 0.2.0 :
  * Application is now a small Dialog at the center of the screen, rather than a full screen app.
  * Remount /system in Read/Write mode before copying script, if necessary. Then remount Read-Only
  * Removed confirmation before installing "reboot to recovery script" : The script is now silently installed if needed.

* version 0.1.0 :
  * First version
  * 4 choices : Reboot, Reboot-recovery (reboots to CWM), Power Off and Cancel
  * Reboot-recovery : if no script present to reboot into recovery : install it first
  * About menu
  * Changelog menu