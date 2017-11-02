package com.Inference;

import java.util.ArrayList;

public interface QueryNode {

    int nextCandidate();
    double scoreDocument(int docId);
    boolean hasMatch(int docId);
    void skipPast(int docId);
    static final double mu = 2000;
    ArrayList<QueryNode> children = new ArrayList<>();


}
