package com.Inference;

import java.util.ArrayList;

public class BelSUM implements QueryNode {
    public BelSUM(ArrayList<QueryNode> nodes) {
        children.addAll(nodes);
    }

    public int nextCandidate(){ return -1;}

    @Override
    public void updateToNextDoc() {

    }

    @Override
    public int nextPos() {
        return 0;
    }

    @Override
    public void skipPos(int position) {

    }

    @Override
    public void updatePos() {

    }

    @Override
    public boolean skipToDoc(int docId) {
        return false;
    }

    public double scoreDocument(int docId){
        double score = 0;
        for(QueryNode child : children) {
            score += Math.exp(child.scoreDocument(docId));
        }
        score /= (double)children.size();
        return score;
    }
    public boolean hasMatch(int docId) { return false;}
    public void skipPast(int docId) {;}
}
