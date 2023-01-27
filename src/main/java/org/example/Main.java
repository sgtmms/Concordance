package org.example;

public class Main {
    public static void main(String[] args) {
        WordCounter wordCounter = new WordCounter();
        if (args.length > 0) {
            wordCounter.run(args[0]);
        } else{
            wordCounter.run("src/main/resources/arbitrary.txt");
        }

    }
}