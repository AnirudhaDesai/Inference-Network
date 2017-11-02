package com.Inference;

import com.company.RetrievalAPI;

import java.io.IOException;
import java.util.ArrayList;

public class TermNode extends RetrievalAPI implements QueryNode {
   private ArrayList<Integer>  postingList; // invertedList for term
   private int idx;  // current index pointer to posting list
   private int cqi;  // collection term frequency
    private int C;   // Total Size of Collection
    Utilities utilities;
    private String term;

    public TermNode(String term) throws IOException {
        if(term == null || term.length()==0) {
            System.out.println("TermNode initialized without any term!");
            System.exit(1);
        }
        this.term = term;
        postingList =  super.getDiskReader().getInvertedListForTerm(term);
        this.idx = 0;
        cqi = super.getTermFrequencies(term);
        C = super.getTotalWords();
        utilities = new Utilities();

    }

    public int nextCandidate(){
        if(idx >= this.postingList.size()) return -1;
        int res = this.postingList.get(idx);
//        idx += this.postingList.get(idx+1)+2;  // Go to next document in list

        return res;
    }

    public double scoreDocument(int docId){
        double score = 0;
        try {
            int cqi = super.getTermFrequencies(term);
            int fik = getDocFrequencyForTerm(this.postingList, docId);
            int dl = super.getDiskReader().getRetrievedDocToLengthMap().get(docId);

            score = Math.log((fik + (mu*(cqi/(double)C)))/(dl+mu));
        }catch (IOException e){
            e.printStackTrace();
        }

        return score;
    }
    public boolean hasMatch(int docId){

        int i = 0;
        while(this.postingList.get(i) < docId){
            i += this.postingList.get(i+1)+2;
        }

        return (this.postingList.get(i) == docId);
        //        return utilities.checkPostingListForDocId(postingList, docId);

    }

    public void skipPast(int docId){
        while(this.idx< this.postingList.size() && this.postingList.get(this.idx) <= docId)
            this.idx += this.postingList.get(this.idx+1)+2;

    }
    public void resetPointer(){
        this.idx = 0;
    }
    private int getDocFrequencyForTerm(ArrayList<Integer> invList, int docId){
        int i = 0;
        while(this.postingList.get(i) != docId){
            i += this.postingList.get(i+1)+2;
        }
        if(this.postingList.get(i) == docId) return this.postingList.get(i+1);
        return -1;
    }




}
