package com.Inference;

import java.util.ArrayList;

public class BelAND implements QueryNode {

    public BelAND(ArrayList<QueryNode> nodes) {
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
//        score =  Math.log(1 - Math.exp(children.get(0).scoreDocument(docId)));
        for(QueryNode child : children)
            score += child.scoreDocument(docId);
        return score;
    }
    public boolean hasMatch(int docId) { return false;}
    public void skipPast(int docId) {;}

}
