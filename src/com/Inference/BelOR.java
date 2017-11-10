package com.Inference;

import com.company.PlayData;

import java.util.ArrayList;
import java.util.List;

public class BelOR implements QueryNode {
    public BelOR(ArrayList<QueryNode> nodes) {
        children.addAll(nodes);
    }

    public int nextCandidate(){ return -1;}

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
    public boolean skipToDoc(int docId) {
        return false;
    }

    public double scoreDocument(int docId){
        double score = 0;
        for(QueryNode child : children) {
            score += Math.log(1 - Math.exp(child.scoreDocument(docId)));
            score = Math.log(1 - Math.exp(score));
            System.out.print("t");
        }
        return score;
    }
    public boolean hasMatch(int docId) { return false;}
    public void skipPast(int docId) {;}
}
