package jcse.app.ergclassroom;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Justine on 2016/06/09.
 */
public class SaveJsonToFile {
    public SaveJsonToFile() {
    }
    public void createJsonFile(Context context,String jsonArray, String filename){
        File file = new File(context.getFilesDir(), filename);
        BufferedOutputStream bufferedOutputStream;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(jsonArray.getBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (FileNotFoundException e4) {
            // TODO Auto-generated catch block
            e4.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            jsonArray=null;
            //jParser=null;
            System.gc();
        }
    }
}
