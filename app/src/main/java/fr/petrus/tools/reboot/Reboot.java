/*
 *  Copyright Pierre Sagne (6 november 2012)
 *
 * petrus.dev.fr@gmail.com
 *
 * This software is a computer program whose purpose is to provide a reboot
 * menu for Android rooted devices.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.petrus.tools.reboot;

import eu.chainfire.libsuperuser.Shell;
import fr.petrus.tools.reboot.utils.FileSystemUtils;
import fr.petrus.tools.reboot.utils.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class Reboot extends Activity implements OnClickListener {
    private static final String TAG = "Reboot";

    private static final boolean TITLE_IN_LAYOUT = true;

    private int arch = Constants.ARCH_UNKNOWN;
	private String product_name = "";
	private boolean stop_wifi = false;
	private boolean flash_misc = false;
	private boolean remount_everything_ro = false;

	private ImageView menuIcon = null;

	private Button rebootButton = null;
	private Button softRebootButton = null;
	private Button rebootRecoveryButton = null;
	private Button rebootBootloaderButton = null;
	private Button rebootDownloadButton = null;
	private Button powerOffButton = null;
	private Button cancelButton = null;

	private SharedPreferences sharedPref = null;
	private SharedPreferences.OnSharedPreferenceChangeListener prefListener = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!Shell.SU.available()) {
			showDialog(Constants.DIALOG_NO_ROOT_ID);
		}

		Window window = getWindow();

        if (TITLE_IN_LAYOUT) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.main_with_title);
        } else {
            window.requestFeature(Window.FEATURE_CUSTOM_TITLE);
            setContentView(R.layout.main_without_title);
            window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        }

		menuIcon = (ImageView) findViewById(R.id.title_menu_icon);
		rebootButton = (Button) findViewById(R.id.reboot_button);
		softRebootButton = (Button) findViewById(R.id.soft_reboot_button);
		rebootRecoveryButton = (Button) findViewById(R.id.reboot_recovery_button);
		rebootBootloaderButton = (Button) findViewById(R.id.reboot_bootloader_button);
		rebootDownloadButton = (Button) findViewById(R.id.reboot_download_button);
		powerOffButton = (Button) findViewById(R.id.power_off_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);

		menuIcon.setOnClickListener(this);

		rebootButton.setOnClickListener(this);			
		softRebootButton.setOnClickListener(this);			
		rebootRecoveryButton.setOnClickListener(this);			
		rebootBootloaderButton.setOnClickListener(this);
		rebootDownloadButton.setOnClickListener(this);
		powerOffButton.setOnClickListener(this);			
		cancelButton.setOnClickListener(this);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		String board = SystemUtils.getProp("ro.product.board");
		String product_device = SystemUtils.getProp("ro.product.device");
		String product_name = SystemUtils.getProp("ro.product.name");
		Log.v(TAG, "Detected product name : \"" + product_name + "\"");

		String sdk_version = SystemUtils.getProp("ro.build.version.sdk");
		int sdk_int_version = Integer.parseInt(sdk_version);
		Log.v(TAG, "Detected arch : \"" + board + "\"");
		if (board.startsWith("rk28")) {
			Log.v(TAG, "arch = ROCKCHIP_28");
			arch = Constants.ARCH_ROCKCHIP_28;
		} else if (board.startsWith("rk29")) {
			Log.v(TAG, "arch = ROCKCHIP_29");
			arch = Constants.ARCH_ROCKCHIP_29;
		} else if (board.startsWith("rk30")) {
			if (sdk_int_version>=19 &&
					( product_device.equalsIgnoreCase("rk3188") || 
					  product_name.equalsIgnoreCase("rk3188") ) ) {
				Log.v(TAG, "arch = ROCKCHIP_31_KK");
				arch = Constants.ARCH_ROCKCHIP_31_KK;
			} else {
				Log.v(TAG, "arch = ROCKCHIP_30");
				arch = Constants.ARCH_ROCKCHIP_30;
			}
		} else if (board.equalsIgnoreCase("crane")
				|| board.equalsIgnoreCase("nuclear")) {
			if (sdk_version.equals("9") || sdk_version.equals("10")) {
				Log.v(TAG, "arch = ALLWINNER_GB");
				arch = Constants.ARCH_ALLWINNER_GB;
			} else {
				Log.v(TAG, "arch = ALLWINNER");
				arch = Constants.ARCH_ALLWINNER;
			}
		} else {
			Log.v(TAG, "arch = UNKNOWN");
			arch = Constants.ARCH_UNKNOWN;
		}

		updatePrefs();

        // Use instance field for listener
        // It will not be gc'd as long as this instance is kept referenced
        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        		Reboot.this.updatePrefs();
        	}
        };

		sharedPref.registerOnSharedPreferenceChangeListener(prefListener);
	}

	public void updatePrefs() {
		String reboot_vis = sharedPref.getString("reboot_button", "show");
		if (reboot_vis.equals("show")) {
			rebootButton.setVisibility(View.VISIBLE);			
		} else {
			rebootButton.setVisibility(View.GONE);			
		}
		
		String soft_reboot_vis = sharedPref.getString("soft_reboot_button", "show");
		if (soft_reboot_vis.equals("show")) {
			softRebootButton.setVisibility(View.VISIBLE);			
		} else {
			softRebootButton.setVisibility(View.GONE);			
		}

		String reboot_recovery_vis = sharedPref.getString("reboot_recovery_button", "show");
		if (reboot_recovery_vis.equals("show")) {
			rebootRecoveryButton.setVisibility(View.VISIBLE);			
		} else {
			rebootRecoveryButton.setVisibility(View.GONE);			
		}

		String reboot_bootloader_vis = sharedPref.getString("reboot_bootloader_button", "auto");
		if (reboot_bootloader_vis.equals("auto")) {
			switch (arch) {
				case Constants.ARCH_ROCKCHIP_30:
				case Constants.ARCH_ROCKCHIP_31_KK:
				case Constants.ARCH_UNKNOWN:
					rebootBootloaderButton.setVisibility(View.VISIBLE);
					break;
				default:
					rebootBootloaderButton.setVisibility(View.GONE);
			}
		} else if (reboot_bootloader_vis.equals("show")) {
			rebootBootloaderButton.setVisibility(View.VISIBLE);			
		} else {
			rebootBootloaderButton.setVisibility(View.GONE);			
		}

		String reboot_download_vis = sharedPref.getString("reboot_download_button", "hide");
		if (reboot_download_vis.equals("show")) {
			rebootDownloadButton.setVisibility(View.VISIBLE);
		} else {
			rebootDownloadButton.setVisibility(View.GONE);
		}

		String power_off_vis = sharedPref.getString("power_off_button", "show");
		if (power_off_vis.equals("show")) {
			powerOffButton.setVisibility(View.VISIBLE);			
		} else {
			powerOffButton.setVisibility(View.GONE);			
		}
		
		String stop_wifi_pref = sharedPref.getString("stop_wifi", "auto");
		if (stop_wifi_pref.equals("auto")) {
			if (product_name.equals("iMito QX1")) {
				stop_wifi = true;
			} else {
				stop_wifi = false;
			}
		} else if (stop_wifi_pref.equals("no")) {
			stop_wifi = false;
		} else {
			stop_wifi = true;
		}
		
		String flash_misc_pref = sharedPref.getString("flash_misc", "auto");
		if (flash_misc_pref.equals("auto")) {
			switch (arch) {
				case Constants.ARCH_ROCKCHIP_28:
				case Constants.ARCH_ROCKCHIP_29:
				case Constants.ARCH_ROCKCHIP_30:
				case Constants.ARCH_ALLWINNER:
				case Constants.ARCH_ALLWINNER_GB:
					flash_misc = true;
					break;
				case Constants.ARCH_ROCKCHIP_31_KK:
					flash_misc = false;
					break;
				default:
					flash_misc = false;
					break;
			}
		} else if (flash_misc_pref.equals("no")) {
			flash_misc = false;
		} else {
			flash_misc = true;
		}
		
		remount_everything_ro = sharedPref.getBoolean("remount_everything_ro", false);

        boolean check_old_app = sharedPref.getBoolean("check_old_app", true);
        if (check_old_app && isOldApplicationInstalled()) {
            showDialog(Constants.DIALOG_REMOVE_OLD_APP_ID);
        }
    }
	
	//@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		boolean confirm_actions = sharedPref.getBoolean("confirm_actions", true);
		if (v == rebootButton) {
			if (confirm_actions) {
				showDialog(Constants.DIALOG_REBOOT_ID);
			} else {
				reboot();
			}
		} else if (v == softRebootButton) {
			if (confirm_actions) {
				showDialog(Constants.DIALOG_SOFT_REBOOT_ID);
			} else {
				softReboot();
			}
		} else if (v == rebootRecoveryButton) {
			if (confirm_actions) {
				showDialog(Constants.DIALOG_REBOOT_RECOVERY_ID);
			} else {
				rebootRecovery();
			}
		} else if (v == rebootBootloaderButton) {
			if (confirm_actions) {
				showDialog(Constants.DIALOG_REBOOT_BOOTLOADER_ID);
			} else {
				rebootBootloader();
			}
		} else if (v == rebootDownloadButton) {
			if (confirm_actions) {
				showDialog(Constants.DIALOG_REBOOT_DOWNLOAD_ID);
			} else {
				rebootDownload();
			}
		} else if (v == powerOffButton) {
			if (confirm_actions) {
				showDialog(Constants.DIALOG_POWER_OFF_ID);
			} else {
				powerOff();
			}
		} else if (v == cancelButton) {
			finish();
		} else if (v == menuIcon) {
			registerForContextMenu(v); 
		    openContextMenu(v);
		    unregisterForContextMenu(v);
		}
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder;
		switch (id) {
			case Constants.DIALOG_NO_ROOT_ID:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.message_no_root)
						.setCancelable(false)
						.setPositiveButton(R.string.exit,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										Reboot.this.finish();
									}
								});
				dialog = builder.create();
				break;
			case Constants.DIALOG_REBOOT_ID:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.message_confirm_reboot)
						.setCancelable(false)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										Reboot.this.reboot();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
				dialog = builder.create();
				break;
			case Constants.DIALOG_SOFT_REBOOT_ID:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.message_confirm_soft_reboot)
						.setCancelable(false)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										Reboot.this.softReboot();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										dialog.cancel();
									}
								});
				dialog = builder.create();
				break;
			case Constants.DIALOG_REBOOT_RECOVERY_ID:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.message_confirm_reboot_recovery)
						.setCancelable(false)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										Reboot.this.rebootRecovery();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										dialog.cancel();
									}
								});
				dialog = builder.create();
				break;
			case Constants.DIALOG_REBOOT_BOOTLOADER_ID:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.message_confirm_reboot_bootloader)
						.setCancelable(false)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										Reboot.this.rebootBootloader();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										dialog.cancel();
									}
								});
				dialog = builder.create();
				break;
			case Constants.DIALOG_REBOOT_DOWNLOAD_ID:
					builder = new AlertDialog.Builder(this);
					builder.setMessage(R.string.message_confirm_reboot_download)
							.setCancelable(false)
							.setPositiveButton(R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,	int id) {
											Reboot.this.rebootDownload();
										}
									})
							.setNegativeButton(R.string.no,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,	int id) {
											dialog.cancel();
										}
									});
					dialog = builder.create();
					break;
			case Constants.DIALOG_POWER_OFF_ID:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.message_confirm_power_off)
						.setCancelable(false)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										Reboot.this.powerOff();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,	int id) {
										dialog.cancel();
									}
								});
				dialog = builder.create();
				break;
			case Constants.DIALOG_REMOVE_OLD_APP_ID:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.message_confirm_old_app_removal)
						.setCancelable(false)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										Reboot.this.removeOldApplication();
									}
								})
						.setNegativeButton(R.string.no,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});
				dialog = builder.create();
				break;
			default:
				dialog = null;
		}
		return dialog;
	}

    private boolean isOldApplicationInstalled() {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo(Constants.OLD_APPLICATION_PACKAGE_NAME, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void removeOldApplication() {
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo applicationInfo =
                    packageManager.getApplicationInfo(Constants.OLD_APPLICATION_PACKAGE_NAME, 0);
            Log.i(TAG, "dataDir : "+applicationInfo.dataDir);
            Log.i(TAG, "sourceDir : "+applicationInfo.sourceDir);
            Log.i(TAG, "publicSourceDir : "+applicationInfo.publicSourceDir);
            Log.i(TAG, "nativeLibraryDir : "+applicationInfo.nativeLibraryDir);
            if (null!=applicationInfo.sourceDir) {
                List<String> commands = new ArrayList<>();
                commands.add("rm "+applicationInfo.sourceDir);
                if (null!=applicationInfo.dataDir) {
                    commands.add("rm -rf " + applicationInfo.dataDir);
                }
                if (null!=applicationInfo.nativeLibraryDir) {
                    commands.add("rm -rf " + applicationInfo.nativeLibraryDir);
                }
                commands.add("rm /data/app/"+Constants.OLD_APPLICATION_PACKAGE_NAME+".apk");
                commands.add("rm /data/app/"+Constants.OLD_APPLICATION_PACKAGE_NAME+"-*.apk");
                commands.add("mount -o remount,rw /system");
                commands.add("rm /system/app/"+Constants.OLD_APPLICATION_PACKAGE_NAME+".apk");
                commands.add("rm /system/app/"+Constants.OLD_APPLICATION_PACKAGE_NAME+"-*.apk");
                commands.add("rm /system/priv-app/"+Constants.OLD_APPLICATION_PACKAGE_NAME+".apk");
                commands.add("rm /system/priv-app/"+Constants.OLD_APPLICATION_PACKAGE_NAME+"-*.apk");
                commands.add("mount -o remount,ro /system");
				Shell.SU.run(commands);
                softReboot();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Reboot", "Package \""+Constants.OLD_APPLICATION_PACKAGE_NAME+"\" not found", e);
        }
    }

    private void stopWifi() {
		if (stop_wifi) {
            Shell.SU.run("svc wifi disable");
		}
	}
	
	private void remountEverythingRO() {
		if (remount_everything_ro) {
            FileSystemUtils.remountEverythingRO();
		}
	}

	public void reboot() {
		stopWifi();
		remountEverythingRO();
		SystemUtils.reboot();
	}

	public void softReboot() {
		SystemUtils.softReboot();
	}

	public void rebootRecovery() {
		stopWifi();
		if (flash_misc) {
			switch (arch) {
                case Constants.ARCH_ROCKCHIP_28:
                case Constants.ARCH_ROCKCHIP_29:
                case Constants.ARCH_ROCKCHIP_30:
                case Constants.ARCH_ROCKCHIP_31_KK:
                    File recoveryImageFile = extractRecoveryImage();
                    String miscMtdDev = SystemUtils.getImageMtdDev("misc");

                    if (null != recoveryImageFile && null != miscMtdDev) {
                        Shell.SU.run("busybox dd if=" + recoveryImageFile.getAbsolutePath() + " of=/dev/mtd/" + miscMtdDev);
                    }

                    remountEverythingRO();
                    try {
                        SystemUtils.reboot("recovery");
                    } catch (IOException e) {
                        Log.e(TAG, "Error while rebooting to recovery", e);
                    }
                    break;
                case Constants.ARCH_ALLWINNER:
                    Shell.SU.run("echo -n boot-recovery | busybox dd of=/dev/block/nandf count=1 conv=sync");
                    SystemUtils.reboot();
                    break;
                case Constants.ARCH_ALLWINNER_GB:
                    Shell.SU.run("echo -n boot-recovery | busybox dd of=/dev/block/nande count=1 conv=sync");
                    remountEverythingRO();
                    SystemUtils.reboot();
                    break;
                default: {
                    remountEverythingRO();
                    try {
                        SystemUtils.reboot("recovery");
                    } catch (IOException e) {
                        Log.e(TAG, "Error while rebooting to recovery", e);
                    }
                }
			}
		} else {
			remountEverythingRO();
			try {
				SystemUtils.reboot("recovery");
			} catch (IOException e) {
				Log.e(TAG, "Error while rebooting to recovery", e);
			}
		}
	}

	public void rebootBootloader() {
		stopWifi();
		remountEverythingRO();
		SystemUtils.rebootBootloader();
	}

	public void rebootDownload() {
		stopWifi();
		remountEverythingRO();
		try {
			SystemUtils.reboot("download");
		} catch (IOException e) {
			Log.e(TAG, "Error while rebooting in download mode", e);
		}
	}

	public void powerOff() {
		stopWifi();
		remountEverythingRO();
		SystemUtils.powerOff();
	}

	private File extractRecoveryImage() {
		File recoveryImageFile = new File(getApplicationContext().getFilesDir(),
                Constants.REBOOT_RECOVERY_IMG_FILENAME);
		if (!recoveryImageFile.exists()) {
			try {
                FileSystemUtils.copy(
                        getResources().openRawResource(R.raw.misc_recovery_img),
                        openFileOutput(Constants.REBOOT_RECOVERY_IMG_FILENAME, Context.MODE_PRIVATE));
			} catch (IOException e) {
				Log.e(TAG, "Error while extracting recovery image to "
						+ recoveryImageFile.getAbsolutePath(), e);
				return null;
			}
		}
		return recoveryImageFile;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
	}
	
	@Override  
	public boolean onContextItemSelected(MenuItem item) {  
		switch (item.getItemId()) {
			case R.id.menu_settings:
				Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
				startActivity(settingsIntent);
				return true;
			case R.id.menu_about:
				Intent aboutIntent = new Intent(getBaseContext(), WebViewActivity.class);
				aboutIntent.putExtra(WebViewActivity.FILE_RES_ID, R.raw.about);
				startActivity(aboutIntent);
				return true;
			case R.id.menu_changelog:
				Intent changelogIntent = new Intent(getBaseContext(), WebViewActivity.class);
				changelogIntent.putExtra(WebViewActivity.FILE_RES_ID, R.raw.changelog);
				startActivity(changelogIntent);
				return true;
			case R.id.menu_close:
				Reboot.this.finish();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}  
}