package com.Inference;

import java.io.IOException;

public class TestTermNode {
    public void RunTests() throws IOException{
        TermNode termNode = new TermNode("king");
        System.out.println(termNode.nextCandidate());
        System.out.println(termNode.scoreDocument(termNode.nextCandidate()));
        System.out.println(termNode.hasMatch(termNode.nextCandidate()+1));
        termNode.skipPast(termNode.nextCandidate());
        System.out.println(termNode.nextCandidate());
        for(int i = 0;i<1000;i++){
            termNode.skipPast(termNode.nextCandidate());
            System.out.println(termNode.nextCandidate());
        }
        System.out.println(termNode.nextCandidate());
    }
}
