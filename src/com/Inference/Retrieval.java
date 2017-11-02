package com.Inference;

import com.company.PlayData;
import com.company.RetrievalAPI;

import java.io.IOException;
import java.util.*;

public class Retrieval extends RetrievalAPI {
    private double mu;
    private int C;
    HashMap<String, Integer> queryFrequency;
    public Retrieval(boolean isCompressed) throws IOException {
        super(isCompressed);
        mu = 1729;
        C = super.getTotalWords();
        queryFrequency = new HashMap<>();

    }

    public double getMu() {
        return mu;
    }

    public void setMu(double mu) {
        this.mu = mu;
    }

    public ArrayList<PlayData> RetrieveQuery(String query, int k) throws IOException{
        HashMap<Integer, Double> docIdToScores = new HashMap<>();

        ArrayList<ArrayList<Integer>> queryInvLists = new ArrayList<>();
        HashMap<String, ArrayList<Integer>> querySetInvList = new HashMap<>();
        queryFrequency = new HashMap<>();

        query = query.trim();
        String[] queryWords = query.split("\\s+");
        if(queryWords.length==0){
            System.out.println("Blank Query");
            return null;
        }
        for(int idx=1; idx<queryWords.length;idx++){
            queryInvLists.add(super.getDiskReader().getInvertedListForTerm(queryWords[idx]));

            if(!querySetInvList.containsKey(queryWords[idx])){
                querySetInvList.put(queryWords[idx], super.getDiskReader().getInvertedListForTerm(queryWords[idx]));
            }
            /* Add it to Query Frequency Map */
            queryFrequency.put(queryWords[idx], queryFrequency.getOrDefault(queryWords[idx],0)+1);
        }
        for(HashMap.Entry<String, ArrayList<Integer>> pair : querySetInvList.entrySet()){
            ArrayList<Integer> invList = pair.getValue();
            String queryTerm = pair.getKey();

            int i = 0;
            while(i<invList.size()){
                int dl = super.getDiskReader().getRetrievedDocToLengthMap().get(invList.get(i));
                double score = scoreDocument(queryTerm,invList.get(i+1), dl);
                docIdToScores.put(invList.get(i),docIdToScores.getOrDefault(invList.get(i),new Double(0))+score);
                i = i+invList.get(i+1)+2;
            }

        }

        /* All doc Id Scores for this query in HashMap. Now use priority queue to get top k */

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
    private double scoreDocument(String term, Integer fik, Integer dl){
        double score = 0;

        try {
            int cqi = super.getTermFrequencies(term);
            score = Math.log((fik + (mu*(cqi/(double)C)))/(dl+mu));
        }catch (IOException e){
            e.printStackTrace();
        }

        return score;
    }
}
