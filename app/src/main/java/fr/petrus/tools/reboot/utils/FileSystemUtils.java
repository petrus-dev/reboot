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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import eu.chainfire.libsuperuser.Shell;

/**
 * Some FileSystem related utility methods.
 *
 * @author Pierre Sagne
 */
public class FileSystemUtils {
	private static final String TAG = "FileSystemUtils";

	public static String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
	public static String USB_DISK_PATH = "/mnt/udisk";

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
        } finally {
            // Close the streams
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error when closing the input stream", e);
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error when closing the output stream", e);
            }
        }
    }

	public static void unmountAllExternalDisks() {
        List<String> commands = new ArrayList<>();

        commands.add("sync");

		String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
        	commands.add("umount "+SDCARD_PATH);
        }

        commands.add("umount "+USB_DISK_PATH);

		Shell.SU.run(commands);
	}

    public static List<MountPoint> getMountPoints() {
        List<MountPoint> mountPoints = new LinkedList<>();

        List<String> resultLines = Shell.SU.run("mount");
        for (String line : resultLines) {
            mountPoints.add(new MountPoint(line));
        }

        return mountPoints;
    }

	public static void remountEverythingRO() {
		List<MountPoint> mountPoints = getMountPoints();

		// sort the list in reverse path depth order
		Collections.sort(mountPoints, new Comparator<MountPoint>() {
            @Override
            public int compare(MountPoint m1, MountPoint m2) {
                return m1.getMountPointDepth() - m2.getMountPointDepth();
            }
        });

        // build the commands list
		List<String> commands = new ArrayList<>();
		for (MountPoint mountPoint : mountPoints) {
            if (mountPoint.isWritable()) {
                commands.add("sync");
                commands.add("mount -o remount,ro " + mountPoint.getDevName() + " " +
                        mountPoint.getMountPoint().getAbsolutePath());
            }
		}

        Shell.SU.run(commands);
	}
}
