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

package fr.petrus.tools.reboot.utils;

import java.io.IOException;
import java.util.List;

import android.util.Log;

import eu.chainfire.libsuperuser.Shell;

/**
 * Some System related utility methods.
 *
 * @author Pierre Sagne
 */
public class SystemUtils {
	public static final String TAG = "SystemUtils";

	public static String getProp(String key) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> resultLines = Shell.SU.run("getprop " + key);
		if (null!=resultLines) {
			for (String line : resultLines) {
				stringBuilder.append(line);
			}
		}
		return stringBuilder.toString();
	}

	public static String getImageMtdDev(String name) {
		List<String> resultLines = Shell.SH.run("cat /proc/mtd");
        if (null!=resultLines) {
            for (String line : resultLines) {
                String[] fields = line.split(" ");
                String mtdName = fields[fields.length - 1];
                if (mtdName.equals("\"" + name + "\"")) {
                    String mtdDev = fields[0];
                    String dev = mtdDev.substring(0, mtdDev.length() - 1);
                    return dev;
                }
            }
        }
		return null;
	}

	public static void reboot() {
		try {
			reboot(null);
		} catch (IOException e) {
			Log.e(TAG, "Error while powering off", e);
		}
	}

	public static void rebootBootloader() {
		try {
			reboot("bootloader");
		} catch (IOException e) {
			Log.e(TAG, "Error while rebooting to bootloader", e);
		}
	}

	public static void powerOff() {
		try {
			reboot("-p");
		} catch (IOException e) {
			Log.e(TAG, "Error while powering off", e);
		}
	}
	
	public static void reboot(String arg) throws IOException {
		FileSystemUtils.unmountAllExternalDisks();
        if (null!=arg && arg.length()>0) {
            Shell.SU.run("reboot "+arg);
        } else {
            Shell.SU.run("reboot");
        }
	}

	public static void softReboot() {
        Shell.SU.run("busybox pkill zygote");
	}
}
