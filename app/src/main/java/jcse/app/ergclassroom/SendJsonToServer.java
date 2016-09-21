package jcse.app.ergclassroom;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Justine on 8/18/2016.
 */
public class SendJsonToServer implements Runnable{
final static String DEBUG_TAG="Send Json to Server";
String gotJsonString;

    public SendJsonToServer(String jsonString){
        this.gotJsonString=jsonString;
    }


    @Override
    public void run() {

        try {
            URL url = new URL("http://egr2.jcse-himat.com/api/timestamps");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);

           /* Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("firstParam",gotJsonString);
            String query = builder.build().getEncodedQuery();*/

            //OutputStream os = conn.getOutputStream();
            DataOutputStream writer = new DataOutputStream(
                    conn.getOutputStream ());
           //// BufferedWriter writer = new BufferedWriter(
           //         new OutputStreamWriter(os, "UTF-8"));
            writer.writeBytes(gotJsonString);

            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            if(response == 200){

            }
            writer.flush();
            writer.close();
            //os.close();

            conn.connect();
        }catch (MalformedURLException malformedURLException){

            Log.e("SendJsonToServer",malformedURLException.getMessage());

        }catch (IOException ioException){
            Log.e("SendJsonToServer",ioException.getMessage());
        }
    }

}
