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

import android.os.Build;
import android.util.Log;

import fr.petrus.tools.reboot.Constants;

/**
 * Detects important properties of the running device.
 *
 * @author Pierre Sagne
 */
public class Device {
    private static final String TAG = "Device";

    private int arch;
    private String productBoard;
    private String productDevice;
    private String productName;

    public Device() {
        productBoard = SystemUtils.getProp("ro.product.board");
        productDevice = SystemUtils.getProp("ro.product.device");
        productName = SystemUtils.getProp("ro.product.name");

        Log.v(TAG, "Detected arch : \"" + productBoard + "\"");
        Log.v(TAG, "Detected product name : \"" + productName + "\"");

        if (productBoard.startsWith("rk28")) {
            Log.v(TAG, "arch = ROCKCHIP_28");
            arch = Constants.ARCH_ROCKCHIP_28;
        } else if (productBoard.startsWith("rk29")) {
            Log.v(TAG, "arch = ROCKCHIP_29");
            arch = Constants.ARCH_ROCKCHIP_29;
        } else if (productBoard.startsWith("rk30")) {
            if (Build.VERSION.SDK_INT>=19 &&
                    ( productDevice.equalsIgnoreCase("rk3188") ||
                            productName.equalsIgnoreCase("rk3188") ) ) {
                Log.v(TAG, "arch = ROCKCHIP_31_KK");
                arch = Constants.ARCH_ROCKCHIP_31_KK;
            } else {
                Log.v(TAG, "arch = ROCKCHIP_30");
                arch = Constants.ARCH_ROCKCHIP_30;
            }
        } else if (productBoard.equalsIgnoreCase("crane")
                || productBoard.equalsIgnoreCase("nuclear")) {
            if (Build.VERSION.SDK_INT==9 || Build.VERSION.SDK_INT==10) {
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
    }

    public int getArch() {
        return arch;
    }

    public String getProductName() {
        return productName;
    }
}
