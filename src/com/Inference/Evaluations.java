package com.Inference;

import com.company.PlayData;

import java.io.*;
import java.util.ArrayList;

public class Evaluations{
    private boolean isCompressed;
    private static final String OW = "od1";
    private static final String UW = "uw";

    private String path;
    private BufferedReader bufferedReader;
    private RandomAccessFile writer;
        public Evaluations(boolean isCompressed) throws IOException {
        this.isCompressed = isCompressed;

        path = "..//";

        InitFileHandling();

    }
    private void InitFileHandling() throws IOException{
        try {
            bufferedReader = new BufferedReader(new FileReader("Queries.txt"));


        }catch (FileNotFoundException f){
            f.printStackTrace();
        }

    }

    public void runOperatorEvaluation() throws IOException{
        System.out.println("Evaluation Started... ");

        QueryRetrievalByOperation(1);


//        QueryRetrievalByOperation(2);



        writer.close();

    }

    public void QueryRetrievalByOperation(Integer mode) throws IOException{
        /* 1 - Ordered Window
           2 - Unordered Window

         */

        String query = null;
        ArrayList<PlayData> results;


        switch (mode){
            case 1:
                /* Ordered Window */


                bufferedReader = new BufferedReader(new FileReader("Queries.txt"));
                writer = new RandomAccessFile(new File("od1.trecrun"), "rw");
                writer.setLength(0);

                while ((query = bufferedReader.readLine())!=null) {
//                    ArrayList<QueryNode> nodes = new ArrayList<>(buildTermNodesFromQuery(query));
                    ArrayList<QueryNode> nodes = new ArrayList<>();
                    String[] terms = query.split("\\s+");
                    for (int i = 1;i<terms.length;i++){
                        nodes.add(new TermNode(terms[i]));
                    }
                    OrderedWindow orderedWindow = new OrderedWindow(nodes,1);
                    results = orderedWindow.RetrieveQuery();
                    if(results != null) {
                        writeResultsTRECRunFormat(results, query.trim().split("\\s+")[0], "OW");
                        results.clear();
                    }


                }
                writer.close();
                break;
            case 2:
                /* Unordered Window */
                bufferedReader = new BufferedReader(new FileReader("Queries.txt"));
                writer = new RandomAccessFile(new File("uw.trecrun"), "rw");
                writer.setLength(0);

                while ((query = bufferedReader.readLine())!=null) {
//                    ArrayList<QueryNode> nodes = new ArrayList<>(buildTermNodesFromQuery(query));
                    ArrayList<QueryNode> nodes = new ArrayList<>();
                    String[] terms = query.split("\\s+");
                    for (int i = 1;i<terms.length;i++){
                        nodes.add(new TermNode(terms[i]));
                    }
                    UnorderedWindow unorderedWindow = new UnorderedWindow(nodes,nodes.size());
                    results = unorderedWindow.RetrieveQuery();
                    if(results != null) {
                        writeResultsTRECRunFormat(results, query.trim().split("\\s+")[0], "UW");

                        results.clear();
                    }

                }
                writer.close();
                break;


            default:
                System.out.println("Model Not found!");
                break;

        }
    }
    private ArrayList<TermNode> buildTermNodesFromQuery(String query) throws IOException{
            ArrayList<TermNode> nodes = new ArrayList<>();
            String[] terms = query.split("\\s+");
            for (int i = 1;i<terms.length;i++){
                nodes.add(new TermNode(terms[i]));
            }

            return nodes;

    }
    private void writeResultsTRECRunFormat(ArrayList<PlayData> results, String topic,String mode) throws IOException{
        StringBuilder line;
        String param2 = "skip";
        int rank=1;
        if(results == null) return;
        for(PlayData res : results) {
            line = new StringBuilder();

            line.append(String.format("%-4s", topic));
            line.append(String.format("%-5s", param2));
            String scene = String.format("%-50s",res.getSceneId()+" ");
            line.append(scene);
            line.append(String.format("%-4s", rank++));
            line.append(String.format("%-15s", (float)res.getScore()));
            if(mode.equals("OW"))
                line.append(String.format("%-50s","anirudhadesa-"+mode+"$"));
            else if(mode.equals("UW"))
                line.append(String.format("%-50s","anirudhadesa-"+mode+"$"));

            writer.writeChars(line.toString()+"\n");
        }
        writer.writeChars("\n\n");


    }



}
