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
 * Created by Justine on 8/18/2016.
 */
public class SendJsonToServer implements Runnable{
final static String DEBUG_TAG="Send Json to Server";
String gotJsonString;
Context mContext;
    public SendJsonToServer(String jsonString){
        this.gotJsonString=jsonString;

    }
    public SendJsonToServer(String jsonString, Context context){
        this.gotJsonString=jsonString;
        this.mContext=context;

    }


    @Override
    public void run() {

        try {

            URL url = new URL("http://egr.tshimologong.net/useractivity/useractivities");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            //conn.setChunkedStreamingMode(0);
            conn.connect();

           byte[] bytes =gotJsonString.getBytes("UTF-8");
            DataOutputStream writer = new DataOutputStream(
                    conn.getOutputStream ());
           // writer.writeInt(bytes.length);
           // writer.write(bytes);
            /*DataOutputStream writer = new DataOutputStream(
                    conn.getOutputStream ());
            writer.writeUTF(gotJsonString);*/
            writer.write(bytes);
            Log.d("userJsonString",gotJsonString);
            //Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            //writer.write(gotJsonString);



            int response = conn.getResponseCode();
/*            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }*/
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
            in.putExtra("TYPE",response);
            in.setAction("NOW");
//sendBroadcast(in);
            LocalBroadcastManager.getInstance(this.mContext).sendBroadcast(in);
            Log.d(DEBUG_TAG, "The response is: " + response);
            if(response == 201){

                SaveJsonToFile saveJsonToFile = new SaveJsonToFile();
                Boolean clearContents= saveJsonToFile.clearFileContents(mContext,"timestamps.txt");
                Log.d(DEBUG_TAG, "Timestamps clear: "+clearContents);

            }else{
                Log.d(DEBUG_TAG,"USER_RESPONSE "+message);
            }

            writer.flush();
            writer.close();
            conn.disconnect();

        }catch (MalformedURLException malformedURLException){

            Log.e("SendJsonToServer",malformedURLException.getMessage());

        }catch (IOException ioException){
            Log.e("SendJsonToServer",ioException.getMessage());
        }
    }

}
