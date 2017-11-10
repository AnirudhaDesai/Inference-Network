package com.Inference;

import com.company.PlayData;

import java.util.ArrayList;
import java.util.List;

public class BelWSUM implements QueryNode {
    ArrayList<QueryNode> children = new ArrayList<>();
    private ArrayList<Double> nodeWeights;
    public BelWSUM(ArrayList<QueryNode> nodes, ArrayList<Double> weights) {
        children.addAll(nodes);
        nodeWeights.addAll(weights);
        if(children.size() != nodeWeights.size()) {
            System.err.println("Number of Weights do not match number of Nodes!");
            System.exit(1);
        }
    }

    public int nextCandidate(){ return -1;}

    @Override
    public int nextPos() {
        return 0;
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
        double sumOfWeights = 0;
        int i = 0;
//        score =  Math.log(1 - Math.exp(children.get(0).scoreDocument(docId)));
        for(QueryNode child : children) {
            score += nodeWeights.get(i)*Math.exp(child.scoreDocument(docId));
            sumOfWeights += nodeWeights.get(i++);
        }
        score /= sumOfWeights;
        return score;
    }
    public boolean hasMatch(int docId) { return false;}
    public void skipPast(int docId) {;}
}
