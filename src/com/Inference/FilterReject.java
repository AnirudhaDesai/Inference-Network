package com.Inference;

import com.company.PlayData;
import com.company.RetrievalAPI;

import java.io.IOException;
import java.util.*;
import java.util.function.DoubleBinaryOperator;

public class FilterReject  extends RetrievalAPI implements QueryNode {
    ArrayList<QueryNode> children = new ArrayList<>();
    public FilterReject(List<QueryNode> nodes) throws IOException {
        if(nodes.size()< 2) {
            System.err.println("Insufficient nodes for filter!");
            System.exit(1);
        }

        this.children.addAll(nodes);

    }

    @Override
    public List<Integer> getDocSet() {
        return null;
    }

    private HashMap<Integer, Double> scoreAll(){
        HashMap<Integer,Double> docIdToScores = new HashMap<>();
        QueryNode proxExpression = children.get(0);  // proximity expression is in the 1st index. - by design
        List<Integer> proxExpressionDocSet = proxExpression.getDocSet();
        Set<Integer> allDocSet = super.getDiskReader().getRetrievedDocToLengthMap().keySet();
        for(int doc : allDocSet){
            if(!proxExpressionDocSet.contains(doc)){   // reject all the docs for proximity Expression
                docIdToScores.put(doc, children.get(1).scoreDocument(doc));
            }
        }

        return docIdToScores;

    }
    public ArrayList<PlayData> RetrieveQuery(){
        /* All doc Id Scores for this query in HashMap. Now use priority queue to get top k */
        HashMap<Integer, Double> docIdToScores =  scoreAll();
        List<Integer> topKDocs = super.getTopKDocs(docIdToScores, docIdToScores.size());

        for(Integer docId : topKDocs){
            PlayData pObj = new PlayData();
            pObj.setSceneId(super.getDiskReader().getRetrievedDocToSceneIdMap().get(docId));
            pObj.setSceneNum(super.getDiskReader().getRetrievedDocToSceneMap().get(docId));
            pObj.setScore(docIdToScores.get(docId));

            super.getRetrievalResults().add(pObj);
        }
        super.printQueryResults(super.getRetrievalResults());
        ArrayList<PlayData> topDocs = super.getRetrievalResults();
        Collections.sort(topDocs, new Comparator<PlayData>() {
            @Override
            public int compare(PlayData p1, PlayData p2) {
                return Double.compare(p2.getScore(),p1.getScore());
            }
        });
        return  topDocs;

    }


    @Override
    public int nextCandidate() {
        return 0;
    }

    @Override
    public double scoreDocument(int docId) {
        return 0;
    }

    @Override
    public boolean hasMatch(int docId) {
        return false;
    }

    @Override
    public void skipPast(int docId) {

    }

    @Override
    public boolean skipToDoc(int docId) {
        return false;
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
    public void updateToNextDoc() {

    }
}
