package jcse.app.ergclassroom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Justine on 11/23/2016.
 */
public class ValueArray implements Serializable {

    ArrayList<HashMap<String,String>> arrayList;

    public ArrayList<HashMap<String,String>> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<HashMap<String,String>> arrayList) {
        this.arrayList = arrayList;
    }

}
