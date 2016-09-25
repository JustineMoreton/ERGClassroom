package jcse.app.ergclassroom;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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

            e4.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        finally {
            jsonArray=null;

            System.gc();
        }
    }
    public void appendJsonFile(Context context,String jsonArray, String filename){
        File file = new File(context.getFilesDir(), filename);
        BufferedOutputStream bufferedOutputStream;
        try {

            RandomAccessFile randomAccessFile = new RandomAccessFile(context.getFileStreamPath(filename),"rw");
            String randomString = randomAccessFile.readLine();
            System.out.print(randomString);
            Long randomLength = randomAccessFile.length();
            randomAccessFile.seek(randomLength);
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file,true));
            //if file is empty, add first json object
            if(randomLength<1){
                bufferedOutputStream.write((jsonArray).getBytes());
                //if file has other entires, add a comma before next entry
            }else{
                bufferedOutputStream.write((","+jsonArray).getBytes());
            }

            bufferedOutputStream.flush();
            bufferedOutputStream.close();

        } catch (FileNotFoundException e4) {

            e4.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        finally {
            jsonArray=null;

            System.gc();
        }
    }
    public boolean clearFileContents(Context context, String filename) {
        File file = new File(context.getFilesDir(), filename);
        Boolean deleted = file.delete();
        File file1 = new File(context.getFilesDir(),filename);
        return deleted;
    }
}
