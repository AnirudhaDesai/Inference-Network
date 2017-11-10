package com.Inference;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException{
	// write your code here
        boolean isCompressed = false;



        if(args.length != 0 && (args[0].equals("c") || args[0].equals("C"))) {
            isCompressed = true;
            System.out.println("Running in Mode : Compressed");
        }
        else
            System.out.println("Running in Mode : Uncompressed");
        String mode = !isCompressed?"Uncompressed":"Compressed";

//        TestNodes testNodes = new TestNodes();
//        testNodes.RunTests();

    Evaluations evaluations = new Evaluations(isCompressed);
    evaluations.runOperatorEvaluation();

    }
}
