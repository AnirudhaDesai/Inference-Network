package com.Inference;

import com.company.PlayData;
import com.company.RetrievalAPI;

import java.util.ArrayList;
import java.util.List;

public class OrderedWindow extends RetrievalAPI implements QueryNode {

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

    public OrderedWindow(ArrayList<QueryNode> nodes, int distance) {
            children.addAll(nodes);
            int cqi = 0;
            C = super.getTotalWords();
            postingList = new ArrayList<>();
            if(nodes.size()>0)
                buildPostingList(distance);
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

    private void buildPostingList(int distance){
        /* Build Posting List for this Ordered Window */
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
                int startTermIndex = 0;
                int startPos = children.get(startTermIndex).nextPos();
                // Points to the index of the current start child
                if (startPos == -1) continue;
                tempList.clear();
                tempList.add(doc);  //
                tempList.add(0);  // doc term frequency. We will update this later.
                int fkid = 0;   // doc term frequency tracker
//                int i = startTermIndex+1;
                for (int i = startTermIndex + 1; i >= 0 && i < children.size(); i++) {
//                while(i >= 0 && i < children.size()){
                    if (startPos == -1) break;   // Move to next document. Posting List Consumed

                    children.get(i).skipPos(startPos);
                    int nextPos = children.get(i).nextPos();
                    if (nextPos == -1) {
                        // end of position list for this document for this term. Move to next Document
//                    moveToNextDoc = true;
                        break;
                    }
                    if (nextPos - startPos <= distance) {
                        startPos = nextPos;   // still a good candidate. proceed further.
                        startTermIndex = i;
                        if (i == children.size() - 1) {  // Candidate passed. Collect Window
                            tempList.add(children.get(0).nextPos());
                            fkid++;
                            startTermIndex = 0;
                            children.get(startTermIndex).updatePos();
                            startPos = children.get(startTermIndex).nextPos();
                            i = startTermIndex;
                        }

                        continue;
                    }
                    else {
                        // the distance is higher. Candidate failed. Reset Start to previous valid and try again.

                        while (startTermIndex > 0) {
                            children.get(startTermIndex).updatePos();  // move To next position
                            startPos = children.get(startTermIndex).nextPos(); // get the position
                            if (startPos - children.get(startTermIndex - 1).nextPos() > distance)
                                startTermIndex--;
                            else break;

                        }
                        if (startTermIndex == 0) {
                            children.get(startTermIndex).updatePos(); // This would not have executed in while loop if 0
                            startPos = children.get(startTermIndex).nextPos();
                        }
                        i = startTermIndex;

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
