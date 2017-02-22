package jcse.app.ergclassroom;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Justine on 1/12/2017.
 */
public class SendResJsonToServer implements Runnable{
    final static String DEBUG_TAG="Send Res to Server";
    String gotJsonString;
    Context mContext;
    public SendResJsonToServer(String jsonString){
        this.gotJsonString=jsonString;

    }
    public SendResJsonToServer(String jsonString, Context context){
        this.gotJsonString=jsonString;
        this.mContext=context;

    }
    @Override
    public void run() {

        try {

            URL url = new URL("http://egr2.jcse-himat.com/resourcetracking/resourcetrackings");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();

            byte[] bytes =gotJsonString.getBytes("UTF-8");
            DataOutputStream writer = new DataOutputStream(
                    conn.getOutputStream ());

            /*DataOutputStream writer = new DataOutputStream(
                    conn.getOutputStream ());*/
            //Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            //writer.write(gotJsonString);
            writer.write(bytes);

            int response = conn.getResponseCode();
            String message="";
            InputStream stream = conn.getErrorStream();
            if (stream == null) {
                stream = conn.getInputStream();
            }
            try (Scanner scanner = new Scanner(stream)) {
                scanner.useDelimiter("\\Z");
                message= scanner.next();
            }
            Intent in = new Intent();
            in.putExtra("RESTYPE",response);
            in.setAction("RESNOW");
//sendBroadcast(in);
            LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(in);
            Log.d(DEBUG_TAG, "Res response is: " + response);
            if(response == 201){

                SaveJsonToFile saveJsonToFile = new SaveJsonToFile();
                Boolean clearContents= saveJsonToFile.clearFileContents(mContext,"resourceUse.txt");

                Log.d(DEBUG_TAG, "Resource used clear: "+clearContents);

            }else{
                Log.d(DEBUG_TAG,"RES_RESPONSE "+message);
            }

            writer.flush();
            writer.close();
            conn.disconnect();

            //conn.connect();
        }catch (MalformedURLException malformedURLException){

            Log.e("SendJsonToServer",malformedURLException.getMessage());

        }catch (IOException ioException){
            Log.e("SendJsonToServer",ioException.getMessage());
        }
    }
}
