package jcse.app.ergclassroom;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Justine on 11/23/2016.
 */
public class ValueMap implements Serializable {
    HashMap<String,String> hashMap;

    public HashMap<String, String> getHashMap(){
        return hashMap;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }
}
