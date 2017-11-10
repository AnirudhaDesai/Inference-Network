package com.Inference;

import com.company.PlayData;
import com.company.RetrievalAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TermNode extends RetrievalAPI implements QueryNode {
   private ArrayList<Integer>  postingList; // invertedList for term
   private int idx;  // current index pointer to posting list
   private int cqi;  // collection term frequency
    private int C;   // Total Size of Collection
    Utilities utilities;
    private String term;
    private int[] docPostingList;    // Array holding the posting list for document pointed by idx

    private int pos;  // points to a position in the current document at idx

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
    public void updateToNextDoc(){
        // moves index to next Doc
        if(idx < postingList.size())
            idx += postingList.get(idx+1)+2;
    }

    public int nextCandidate(){
        if(idx >= this.postingList.size()) return -1;
        int res = this.postingList.get(idx);

//        idx += this.postingList.get(idx+1)+2;  // Go to next document in list

        return res;
    }

    public double scoreDocument(int docId){
        double score = 0;
        int fik = getDocFrequencyForTerm(this.postingList, docId);
        int dl = super.getDiskReader().getRetrievedDocToLengthMap().get(docId);
        score = Math.log((fik + (mu*(cqi/(double)C)))/(dl+mu));

        return score;
    }
    public boolean hasMatch(int docId){

        int i = 0;
        while(i< postingList.size() && this.postingList.get(i) < docId){
            i += this.postingList.get(i+1)+2;
        }

        return (i<postingList.size() && this.postingList.get(i) == docId);


    }
    public int skipPastPosition(int pos){
        // skips past the position in docPostingList. Returns -1 if end of list
        int p = 0;
        while(p<docPostingList.length && docPostingList[p]<=pos) p++;
        if(p>=docPostingList.length) return  -1;  // end of list.
        return docPostingList[p];

    }
    public boolean skipToDoc(int docId){
        // Skips idx to doc. Should preferably be called after hasMatch()
        if(!this.hasMatch(docId)) return false;
        this.idx = getIndexForDocument(docId);
//        while(this.postingList.get(this.idx) != docId)
//            this.idx += this.postingList.get(this.idx+1)+2;
        updateDocPostingList(docId);
        return true;
    }
    public void skipPos(int position){
        while(pos<docPostingList.length && docPostingList[pos]<=position) pos++;
    }
    public int nextPos(){
        //returns the next position in document sequence
        if(pos<docPostingList.length) {

            return docPostingList[pos];

        }
        return -1;

    }
    public void updatePos(){
        pos++;
    }

    public void skipPast(int docId){
        while(this.idx< this.postingList.size() && this.postingList.get(this.idx) <= docId)
            this.idx += this.postingList.get(this.idx+1)+2;
        if(idx < postingList.size())   // Document exists. Update the doc posting List array to current doc
            updateDocPostingList(postingList.get(idx));

    }
    public void resetPointer(){
        this.idx = 0;
    }
    private int getDocFrequencyForTerm(ArrayList<Integer> invList, int docId){
        int i = 0;
        while(i < this.postingList.size() && this.postingList.get(i) != docId){
            i += this.postingList.get(i+1)+2;
        }
        if(i >= this.postingList.size())
            return 0;   // document does not exist in posting List. Return frequency = 0
        return this.postingList.get(i+1);

    }
    private void updateDocPostingList(int docId){
        // Updates the docPostingList array to the document
        int i =  getIndexForDocument(docId);
        if(i==-1){
            // document does not exist in posting list. Error out.
            System.err.println("Document not in List");
            updateDocPostingList(this.postingList.get(0));
        }
        int size = this.postingList.get(i+1);
        this.docPostingList = new int[size];
        int k = 0;
        for(int j = i+2; j<size+i+2;j++)
            this.docPostingList[k++] = this.postingList.get(j);
        this.pos = 0;

    }
    private int getIndexForDocument(int docId){
        // Returns the index at which this document exists in the posting List

        int i = 0;
        while(i < this.postingList.size() && this.postingList.get(i) != docId){
            i += this.postingList.get(i+1)+2;
        }
        if(i >= this.postingList.size())
            return -1;   // document does not exist in posting List. Return -1
        return i;
    }

    @Override
    public ArrayList<PlayData> RetrieveQuery() {
        return null;
    }

    public List<Integer> getDocSet(){
        List<Integer> docSet = new ArrayList<>();
        int i = 0;
        while(i < postingList.size()){
            docSet.add(postingList.get(i));
            i += postingList.get(i+1)+2;
        }
        return docSet;
    }

}
