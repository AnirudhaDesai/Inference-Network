package com.Inference;

import com.company.PlayData;

import java.util.ArrayList;
import java.util.List;

public interface QueryNode {

    int nextCandidate();
    double scoreDocument(int docId);
    boolean hasMatch(int docId);
    void skipPast(int docId);
    static final double mu = 2000;


    boolean skipToDoc(int docId);
    int nextPos();  // returns the next position in document sequence
    void skipPos(int position);
    void updatePos();
    void updateToNextDoc();
    List<Integer> getDocSet();
    ArrayList<PlayData> RetrieveQuery();

}
