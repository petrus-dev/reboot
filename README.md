# Reboot #
Reboot is an Android app for rooted devices which provides a reboot menu.

## Description ##
* Open source
* Compatible with android 1.6+
* Reboot your device
* Soft Reboot (faster)
* Reboot to recovery
* Reboot to bootloader
* Reboot to download mode
* Power off

## Notice ##
This app was made for some rockchip and allwinner based devices, which can't reboot to recovery easily.
It uses some tricks to do this.

But if your device is not detected as one of these faulty devices, the app uses usual root commands to do the job, so it should work on any rooted device.

It has been tested on many android devices, but of course not every single one.
Use this app at your own risk.
Make sure to do a full backup before trying it.
I am not responsible for any any issues this app may cause. 

Root access to your device is required to use this application. If you are not aware whether or not your device has root access, you probably don't have root access.

This app runs on Android 1.6+. It is quite an old version, so the code uses some deprecated methods.
One solution could be to use the Android support libraries, but I wanted to keep the app very light, so I only use Android APIs 1.6 for now.
Don't be surprise when you look at the code if you see deprecated methods.

## Supported languages ##
* English
* French
* Polish
* Russian (incomplete)
* Spanish (incomplete)
* Norwegian (incomplete)
* Portuguese

## Web Site ##
[www.freaktab.com](http://www.freaktab.com)

## Thanks to ##
* all members on www.freaktab.com for all the testing and support.
* "Finless" Bob for the awesome roms this tool was made for.
* Mike Anderson (aka tattman65) for the app icon
* "Sowa" for the Russian translation
* "XTeK1" and "dregov" for the Polish translation
* "pr0xZen" for the Norwegian translation
* "litry" for the Spanish translation
* "ViperRunner" for the Portuguese translation
* all the members for the testing and the kind support. I'm glad to be part of this community :)

## Google Play ##
You can install it from [Google Play](https://play.google.com/store/apps/details?id=fr.petrus.tools.reboot)

## Build it ##
The sources are available here, so you can build it yourself if you like.

1. Get the sources
```bash
git clone https://github.com/petrus-dev/storagecrypt.git
```
2. Import the project in Android Studio
3. Run it on your device from Android Studio, by running the "app" run task.
