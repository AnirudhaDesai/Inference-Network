package com.Inference;

import com.company.PlayData;
import com.company.RetrievalAPI;

import java.io.IOException;
import java.util.*;

public class booleanAND extends RetrievalAPI implements QueryNode {
    ArrayList<QueryNode> children = new ArrayList<>();

    private ArrayList<Integer> postingList; // invertedList for window
    private int idx;  // current index pointer to posting list
    private int[] docPostingList;    // Array holding the posting list for document pointed by idx
    private int pos;  // points to a position in the current document at idx
    private int cqi;
    private int C;

    @Override
    public void updateToNextDoc() {
        // moves index to next Doc
        if(idx < postingList.size())
            idx += postingList.get(idx+1)+2;
    }

    public booleanAND(ArrayList<QueryNode> nodes) {
        children = new ArrayList<>();
        children.addAll(nodes);
        int cqi = 0;
        C = super.getTotalWords();
        postingList = new ArrayList<>();
        if(nodes.size()>0)
            buildPostingList();
    }

    @Override
    public int nextPos(){
        //returns the next position in document sequence
        if(pos<docPostingList.length) {

            return docPostingList[pos];

        }
        return -1;

    }

    @Override
    public void skipPos(int position){
        while(pos<docPostingList.length && docPostingList[pos]<=position) pos++;
    }

    @Override
    public void updatePos(){
        pos++;
    }

    public ArrayList<Integer> getPostingList() {
        return postingList;
    }

    public void setPostingList(ArrayList<Integer> postingList) {
        this.postingList = postingList;
    }

    private void buildPostingList(){
        /* Build Posting List for this Unordered Window */
        boolean isEndOfList = false;
//        boolean moveToNextDoc = false;
        ArrayList<Integer> tempList = new ArrayList<>();  // tempList to hold doc wise posting List

        while(!isEndOfList){
            boolean skipDoc = false;
            int doc = children.get(0).nextCandidate();
            if(doc == -1) {
                // End of list. No more windows to collect. Exit.
                isEndOfList = true; break;
            }
            int distance = super.getDiskReader().getRetrievedDocToLengthMap().get(doc);
            // Check if doc exists in all other children.
            // If it exists, skip to that Document.
            for(QueryNode child : children){
                /* skipToDoc skips the pointer in child node to the current doc.
                 */
                if(!child.skipToDoc(doc)) {
                    skipDoc = true;
                    break;
                }

            }
            if(!skipDoc) {
            /* From here, all nodes have the document. Proceed to calculating number of windows in doc */

                tempList.clear();
                tempList.add(doc);  //
                tempList.add(0);  // doc term frequency. We will update this later.
                int fkid = 0;   // doc term frequency tracker
//                int i = startTermIndex+1;
                int minPosIndex = 0;   // holds the index of the node which has current minimum Pos
                int minPos = children.get(minPosIndex).nextPos(); // initialize min Pos.
                boolean isEndOfDoc = false;
                while(!isEndOfDoc) {
                    if(minPos == -1) break;  // End of document. Move to next doc
                    for (int i = 0; i < children.size(); i++) {
                        int pos = children.get(i).nextPos();
                        if(pos == -1){
                            isEndOfDoc = true;
                            break;
                        }
                        if (pos < minPos) {
                            minPos = pos;
                            minPosIndex = i;
                        }
                    }
                    if(isEndOfDoc) break;
                /*  Check for window size against min pos  */
                    for (int i = 0; i < children.size(); i++) {
                        int pos = children.get(i).nextPos();

                        if (pos - minPos < distance) {
                            // Valid candidate so far.
                            // check if last term.
                            if (i == children.size() - 1) {
                                tempList.add(children.get(minPosIndex).nextPos());
                                fkid++;
                                // update all the child node pointers to next position. No double dipping
                                for(QueryNode child : children)
                                    child.updatePos();


                            }

                        } else {
                            // Candidate failed.
                            // update minimum position to next pos and repeat process
                            children.get(minPosIndex).updatePos();
                            minPos = children.get(minPosIndex).nextPos();
                            break;

                        }
                    }

                }


            /* The document has been consumed. If the temp List size is > 2, we have collected windows.
             Add them to the posting list.
             */
                if (tempList.size() > 2) {
                    tempList.set(1, fkid);
                    cqi += fkid;
                    postingList.addAll(new ArrayList<>(tempList));
                }
            }
//            if(moveToNextDoc) continue;
            children.get(0).updateToNextDoc();
        }
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
    private int getDocFrequencyForTerm(ArrayList<Integer> invList, int docId){
        int i = 0;
        while(i < this.postingList.size() && this.postingList.get(i) != docId){
            i += this.postingList.get(i+1)+2;
        }
        if(i >= this.postingList.size())
            return 0;   // document does not exist in posting List. Return frequency = 0
        return this.postingList.get(i+1);

    }
    public boolean hasMatch(int docId){

        int i = 0;
        while(i< postingList.size() && this.postingList.get(i) < docId){
            i += this.postingList.get(i+1)+2;
        }

        return (i<postingList.size() && this.postingList.get(i) == docId);


    }
    public void skipPast(int docId){
        while(this.idx< this.postingList.size() && this.postingList.get(this.idx) <= docId)
            this.idx += this.postingList.get(this.idx+1)+2;
        if(idx < postingList.size())   // Document exists. Update the doc posting List array to current doc
            updateDocPostingList(postingList.get(idx));

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
        while(this.postingList.get(this.idx) != docId)
            this.idx += this.postingList.get(this.idx+1)+2;
        return true;
    }

    private HashMap<Integer, Double> scoreAll(){
        HashMap<Integer,Double> docIdToScores = new HashMap<>();
        for(int doc : this.getDocSet()){
            docIdToScores.put(doc, this.scoreDocument(doc));
        }

        return docIdToScores;

    }

    @Override
    public ArrayList<PlayData> RetrieveQuery() {

        HashMap<Integer, Double> docIdToScores =  scoreAll();
        if(docIdToScores.size()==0) return null;
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

    public List<Integer> getDocSet(){
        List<Integer> docSet = new ArrayList<>();
        int i = 0;
        while(i < postingList.size()){
            docSet.add(postingList.get(i));
            i += postingList.get(i+1)+2;
        }
        return docSet;
    }
    private ArrayList<TermNode> buildTermNodesFromQuery(String query) throws IOException {
        ArrayList<TermNode> nodes = new ArrayList<>();
        String[] terms = query.split("\\s+");
        for (int i = 1;i<terms.length;i++){
            nodes.add(new TermNode(terms[i]));
        }

        return nodes;

    }


}
