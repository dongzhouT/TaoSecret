package secret.com.tao.taosecret.net;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import secret.com.tao.taosecret.Config;

/**
 * Created by Adminstrator on 2015/9/21.
 */
public class NetConnection {
    public NetConnection(final String url, final HttpMethod method, final SuccessCallback successCallback,
                         final FailCallback failCallback, final String... kvs) {
        final StringBuffer paramsStr = new StringBuffer();
        for (int i = 2; i < kvs.length; i += 2) {
            paramsStr.append(kvs[i]).append("=").append(kvs[i + 1]).append("&");
        }
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String s) {
                if (s != null) {
                    if (successCallback != null) {
                        successCallback.onSuccess(s);
                    }
                } else {
                    if (failCallback != null) {
                        failCallback.onFail();
                    }
                }
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                URLConnection uc;
                System.out.println("Request url-->" + url+kvs[1]);
                try {
                    switch (method) {
                        case POST:
                            uc = new URL(url+kvs[1]).openConnection();
                            uc.setDoOutput(true);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(uc.getOutputStream(), Config.CHARSET));
                            bw.write(paramsStr.toString());
                            bw.flush();
                            break;
                        default:
                            uc = new URL(url+kvs[1] + "?" + paramsStr.toString()).openConnection();
                            break;
                    }

                    System.out.println("request data-->" + paramsStr.toString());

                    BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), Config.CHARSET));
                    String line = null;
                    StringBuffer result = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println("result -->" + result);
                    return result.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public interface SuccessCallback {
        void onSuccess(String result);
    }

    public interface FailCallback {
        void onFail();
    }
}
