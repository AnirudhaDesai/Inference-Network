package com.Inference;

import java.io.IOException;
import java.util.ArrayList;

public class TestTermNode {
    public void RunTests() throws IOException{

        /* NOT tests */

//        RunBelNOTTests();
        /* OR tests */

//        RunBelORTests();
        /* AND tests */

//        RunBelANDTests();

        RunOrderedWindowTests();
    }
    public void RunOrderedWindowTests() throws IOException{
        System.out.println("Running Ordered Window Tests...");
        ArrayList<QueryNode> nodes  =  new ArrayList<>();
        nodes.add(new TermNode("another"));
        nodes.add(new TermNode("enter"));
//        nodes.add(new TermNode("royalty"));
        OrderedWindow orderedWindow = new OrderedWindow(nodes, 2);
        System.out.println("Posting List for OW : ");
        for(int n : orderedWindow.getPostingList())
            System.out.print(n + ",");
    }
    public void RunBelNOTTests() throws IOException{
        System.out.println("Running Tests For NOT...");
        TermNode termNode = new TermNode("king");
        BelNOT belNOT = new BelNOT(termNode);
        System.out.println(belNOT.scoreDocument(termNode.nextCandidate()));
    }
    public void RunBelANDTests() throws IOException{
        System.out.println("Running Tests For AND...");
        TermNode termNode1 = new TermNode("king");
        TermNode termNode2 = new TermNode("queen");
        ArrayList<QueryNode> children = new ArrayList<>();
        children.add(termNode1);
        children.add(termNode2);
        BelAND belAND = new BelAND(children);
        double score1 = belAND.scoreDocument(termNode1.nextCandidate());
        double score2 = belAND.scoreDocument(termNode2.nextCandidate());
        System.out.println("Score 1 : " + score1);
        System.out.println("Score 2 : "+ score2);

    }
    public void RunBelORTests() throws IOException{
        System.out.println("Running Tests For OR...");
        TermNode termNode1 = new TermNode("king");
        TermNode termNode2 = new TermNode("queen");
        ArrayList<QueryNode> children = new ArrayList<>();
        children.add(termNode1);
        children.add(termNode2);
        BelOR belOR = new BelOR(children);
//        double score1 = belOR.scoreDocument(termNode1.nextCandidate());
        double score2 = belOR.scoreDocument(termNode2.nextCandidate());
//        System.out.println("Score 1 : " + score1);
        System.out.println("Score 2 : "+ score2);

    }
}
