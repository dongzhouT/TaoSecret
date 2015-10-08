package secret.com.tao.taosecret;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import secret.com.tao.taosecret.net.HttpMethod;
import secret.com.tao.taosecret.net.NetConnection;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        testHttp();
    }

    private void testHttp(){

        NetConnection nc=new NetConnection(NetConnection.URL, HttpMethod.GET,new Succ(),new Fail());

    }
    class Succ implements NetConnection.SuccessCallback{

        @Override
        public void onSuccess(String result) {
            Log.v("succ","result--->"+result);
        }
    }
    class Fail implements NetConnection.FailCallback{

        @Override
        public void onFail() {
            Log.v("succ","failed");
        }
    }
}