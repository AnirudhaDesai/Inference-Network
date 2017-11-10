package com.Inference;

import com.company.PlayData;

import java.util.ArrayList;
import java.util.List;

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
    public ArrayList<PlayData> RetrieveQuery() {
        return null;
    }

    @Override
    public List<Integer> getDocSet() {
        return null;
    }

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
