package com.Inference;

import java.util.ArrayList;

public class BelOR implements QueryNode {
    public BelOR(ArrayList<QueryNode> nodes) {
        children.addAll(nodes);
    }

    public int nextCandidate(){ return -1;}
    public double scoreDocument(int docId){
        double score = 0;
        for(QueryNode child : children) {
            score += Math.log(1 - Math.exp(child.scoreDocument(docId)));
            score = 1 - Math.exp(score);

        }
        return score;
    }
    public boolean hasMatch(int docId) { return false;}
    public void skipPast(int docId) {;}
}
