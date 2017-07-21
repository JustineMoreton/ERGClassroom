package jcse.app.ergclassroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Justine on 6/12/2017.
 */
public class ConnectActivity extends Activity {
    private ConnectionHandler handler;
    Boolean connected=false;
    ProgressDialog dialog;
    int fromActivity;
    Intent fromIntent;
    class ConnectionHandler extends Handler{
        private ConnectActivity mConnectActivity;
        public ConnectionHandler(ConnectActivity connectActivity){
            mConnectActivity =connectActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mConnectActivity.handleMessage(msg);
        }

        Thread networkThread = new Thread() {
            public void run() {
                Looper.prepare();
                ConnectivityManager connMgr = (ConnectivityManager)
                        ConnectActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    connected = true;
                    handler.handleMessage(obtainMessage(0));

                } else {
                    connected = false;
                    handler.handleMessage(obtainMessage(1));
                }
                Looper.loop();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        fromIntent =getIntent();
        fromActivity =fromIntent.getIntExtra("fromActivity",0);
        handler = new ConnectionHandler(this);
        dialog = ProgressDialog.show(ConnectActivity.this, "","Connecting..Wait.." , true);
        dialog.show();

        handler.networkThread.start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        ConnectActivity.this.finish();
        return;
    }

    public void handleMessage(Message msg) {
        switch(msg.what) {
            case 0:

                dialog.dismiss();
                setAlert();
                //this.finish();
                break;
            case 1:
                dialog.dismiss();
                setToast();
                ConnectActivity.this.finish();
                break;
        }
    }
    public void setAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ConnectActivity.this);
        builder.setMessage("Are you sure you want to download the entire lesson plan? This process may take up to an hour and you will not be able to use the tablet.").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    public void setToast(){
        Toast.makeText(this, "There is no network connection, please try again later", Toast.LENGTH_LONG).show();
    }
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(getApplication(),HttpActivity.class);
                    intent.putExtra("fromActivity",fromActivity);
                    startActivity(intent);
                    ConnectActivity.this.finish();

                case DialogInterface.BUTTON_NEGATIVE:
                    ConnectActivity.this.finish();
            }
        }
    };


}
