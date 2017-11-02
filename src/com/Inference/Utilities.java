package com.Inference;

import java.util.ArrayList;

public class Utilities {

    public boolean checkPostingListForDocId(ArrayList<Integer> pList, int docId){
        int i = 0;
        while(pList.get(i) < docId){
            i += pList.get(i+1)+2;
        }

        return (pList.get(i) == docId);

    }

//    public int getDocTermFrequency()
}
