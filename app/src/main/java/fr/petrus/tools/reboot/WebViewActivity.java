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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.markdownj.MarkdownProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class WebViewActivity extends Activity {
	public static final String TAG = "WebViewActivity";

	public static final String FILE_RES_ID = "FILE_RES_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.webview);

        int markdownTextFileId = getIntent().getIntExtra(FILE_RES_ID, R.raw.changelog);

        String markdownText = readText(markdownTextFileId);

        MarkdownProcessor m = new MarkdownProcessor();
        String htmlText = m.markdown(markdownText);
        Log.d(TAG, htmlText);

		WebView webView = (WebView) findViewById(R.id.web_view);
		webView.getSettings().setJavaScriptEnabled(false);
        webView.loadData(htmlText, "text/html; charset=UTF-8", null);
    }

	private String readText(int fileResId) {
		String result = "";
		try {
			String str;
			BufferedReader in = new BufferedReader(
					new InputStreamReader(getResources().openRawResource(fileResId), "UTF-8"));
			while ((str = in.readLine()) != null) {
				result += str + "\n";
			}
			in.close();
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Error when reading text file", e);
		} catch (IOException e) {
			Log.e(TAG, "Error when reading text file", e);
		} catch (Exception e) {
			Log.e(TAG, "Error when reading text file", e);
		}
		return result;
	}
}
