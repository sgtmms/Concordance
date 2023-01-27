package org.example;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WordCounter {

    private final static Logger logger = Logger.getLogger(WordCounter.class.getName());
    TreeMap<String, HashMap<Integer, String>> concordance;
    TreeMap<String, ArrayList<Integer>> runningTotals;
    String longestWord = "";

    void run(String fileName){
        concordance = new TreeMap<>();
        runningTotals = new TreeMap<>();
        readFile(fileName);
        concordance = createConcordance(runningTotals);

    }

    void readFile(String fileName){
        StringBuilder sentence = new StringBuilder();
        Integer sentenceCount = 1;
        try (FileReader fileReader = new FileReader(fileName)) {
            int c;

            while ((c = fileReader.read()) != -1) {
                char nextChar = (char) c;
                //filter nonAlphanumeric chars but allow spaces and periods
                if(Character.isAlphabetic(nextChar) || Character.isDigit(nextChar) || nextChar == ' ' || nextChar == '.') {
                    sentence.append(nextChar);

                }
                if(nextChar == '.' && !isEndingWithPartOfIPeriodEPeriod(sentence.toString())){
                    processSentence(sentence.toString(), sentenceCount);
                    sentence = new StringBuilder();
                    sentenceCount++;
                }

            }
        } catch (FileNotFoundException fnfe) {
            logger.log(Level.SEVERE, fnfe.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    void processSentence(String sentence, Integer sentenceNumber){
        //TreeMap<String, ArrayList<Integer>> runningTotals
        sentence = sentence.substring(0 , sentence.length() - 1);
        sentence = sentence.toLowerCase();
        System.out.println(sentence);
        String[] wordArray = sentence.split(" ");

        for(int i = 0; i< wordArray.length; i++){
            if(longestWord.length() < wordArray[i].length()){
                longestWord = wordArray[i];
            }
            if(wordArray[i].length() > 0) {
                if (runningTotals.containsKey(wordArray[i])) {
                    runningTotals.get(wordArray[i]).add(sentenceNumber);
                } else {
                    ArrayList<Integer> lineNumber = new ArrayList<>();
                    lineNumber.add(sentenceNumber);
                    runningTotals.put(wordArray[i], lineNumber);
                }
            }
        }
    }

    TreeMap<String, HashMap<Integer, String>> createConcordance(TreeMap<String, ArrayList<Integer>> runningTotals){
        Map<String, ArrayList<Integer>> sortedMap = new TreeMap<>(runningTotals);
        for (Map.Entry<String, ArrayList<Integer>>
                entry : sortedMap.entrySet()) {
            HashMap<Integer, String> occurrences = new HashMap<>();
            occurrences.put(entry.getValue().size(), entry.getValue().toString());
            concordance.put(entry.getKey(), occurrences);
        }
        logger.log(Level.INFO, concordance.toString());
        System.out.println(concordance.toString());
        writeConcordanceToConsoleAndFile(concordance);
        return concordance;
    }


    boolean isEndingWithPartOfIPeriodEPeriod(String sentence){
        if (sentence.endsWith(" i.") || sentence.endsWith(" i.e.")){
            return true;
        } else {
            return false;
        }
    }

    void writeConcordanceToConsoleAndFile(TreeMap<String, HashMap<Integer, String>> concordance){
        Path outputFile = Paths.get("results.txt");
        LinkedList<String> outputList = new LinkedList<>();
        System.out.println("\n\n\n");
        System.out.println("RESULTS FOLLOW:");
        for (Map.Entry<String, HashMap<Integer, String>>
                entry : concordance.entrySet()) {
            String dataRow = formatWord(entry.getKey())
                    + "\t" + formatData(entry.getValue().toString());
            System.out.println(dataRow);
            outputList.add(dataRow);
        }
        try {
            Files.write(outputFile, outputList);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR WRITING OUTPUT");
            logger.log(Level.SEVERE, e.getMessage());
        }



    }

    String formatWord(String word){
        while (word.length() < longestWord.length()){
            word = word.concat(" ");
        }
        return word;
    }


    String formatData(String list){
        list = list.replace('=', ':');
        list = list.replace('[', ' ');
        list = list.replace(']', ' ');
        list = list.replaceAll(" ", "");
        list = list.trim();
        return list;
    }

}
