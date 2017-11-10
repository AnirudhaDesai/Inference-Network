package com.Inference;

public class BelNOT implements QueryNode {

    public BelNOT(QueryNode child) {
        if(child != null)
            children.add(child);
    }

    public int nextCandidate(){ return -1;}
    public double scoreDocument(int docId){
        double score = 0;
        score =  Math.log(1 - Math.exp(children.get(0).scoreDocument(docId)));
        return score; }
    public boolean hasMatch(int docId) { return false;}
    public void skipPast(int docId) {;}

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
}
