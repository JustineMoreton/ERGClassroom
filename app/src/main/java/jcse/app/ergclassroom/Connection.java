package jcse.app.ergclassroom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Justine on 2016/06/06.
 */
public class Connection {
    Context connectionContext;
    public Connection(Context activityContext) {
    this.connectionContext=activityContext;
    }
    public void createConnection(){
        ConnectivityManager connMgr = (ConnectivityManager)
                connectionContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


        } else {
            // display error
        }
    }
}
