// CMP SCI 2261 - Project 6 - Webcrawler
// Terry Ford Jr.
// I'm happy with how this turned out! I had a lot of issues with finding other wikipedia links to navigate to,
// It turns out wikipedia doesn't have full, explicit links to navigate from one article to another, in site links
// contain just the /wiki/Article_Name section of the url, so it was a bit more of a struggle to navigate from
// one in-site article to another than I originally thought it would be.
//
// The wordFreq hashmap is constantly printing and making the system output unreadable, that line is located at the
// bottom of the program, so you can comment it out and better see which wikipedia articles are being navigated to.

package edu.umsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static java.util.Arrays.asList;

public class WebCrawler {
    public static void main(String[] args) throws InterruptedException {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter a URL: ");
        String url = input.nextLine();
        crawler(url); // Traverse the Web from the starting url
    }

    public static void crawler(String startingURL) throws InterruptedException {

        ArrayList<String> listOfPendingURLs = new ArrayList<>();
        ArrayList<String> listOfTraversedURLs = new ArrayList<>();
        listOfPendingURLs.add(startingURL);
        Map<String, Integer> freqMap = new HashMap<>();          // Word Frequency Hashmap
        int count = 0;

        while (!listOfPendingURLs.isEmpty() &&
                listOfTraversedURLs.size() <= 1000) {
            String urlString = listOfPendingURLs.remove(0);
            count++;

            if (!listOfTraversedURLs.contains(urlString)) {
                listOfTraversedURLs.add(urlString);
                System.out.println("Crawl " + count + " " + urlString);

                try {
                    Thread.sleep(50);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

                for (String s : getSubURLs(urlString, freqMap)) {
                    if (!listOfTraversedURLs.contains(s))
                        listOfPendingURLs.add(s);
                }
            }
        }
    }

    public static ArrayList<String> getSubURLs(String urlString, Map<String, Integer> freqMap)
            throws InterruptedException {
        ArrayList<String> list = new ArrayList<>();

        try {
            java.net.URL url = new java.net.URL(urlString);
            Scanner input = new Scanner(url.openStream());
            int current = 0;
            while (input.hasNext()) {
                String line = input.nextLine();

                if (line.startsWith("<title>")) {              // TITLE PARSING USING JSOUP
                    Document doc = Jsoup.parse(line);
                    System.out.println("\tArticle Title: " + doc.title());
                }

                // Cleaning input to feed into freqMap word frequency hashmap
                org.jsoup.nodes.Document dom = Jsoup.parse(line);              // Parsing HTML Elements
                String text = dom.text();
                text = text.replaceAll("[^\\w\\s]","");        // Removing symbols
                text = text.replaceAll("[0-9]","");
                //text = text.replaceAll("[-+^]*", "");
                text = text.toLowerCase();                                     // Setting to lowercase

                asList(text.split(" ")).forEach(s -> {          // COUNTING WORDS, PLACING INTO HASHMAP
                    if (freqMap.containsKey(s)) {
                        Integer count = (Integer) freqMap.get(s);
                        freqMap.put(s, count + 1);
                    } else {
                        if (s.length() < 20)
                            freqMap.put(s, 1);
                    }
                });

                // Reads article line by line, searching for links
                //current = line.indexOf("https://en.wikipedia.org/", current);
                current = line.indexOf("/wiki/", current);
                while (current > 0) {
                    int endIndex = line.indexOf("\"", current);
                    if (endIndex > 0) { // Ensure that a correct URL is found
                        list.add("https://en.wikipedia.org" + line.substring(current, endIndex));
                        //System.out.println(line.substring(current, endIndex));
                        current = line.indexOf("http:", endIndex);
                    } else
                        current = -1;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            if (Thread.interrupted())  // Clears interrupted status!
                throw new InterruptedException();
        }

        System.out.println("\tWord Frequency: " + freqMap.toString());   // Print Word Frequency HashMap
                 // THIS ONE LINE MAKES THE PROGRAM EXTREMELY MESSY WHEN RUN. TO GET A CLEARER PICTURE OF
                 // WHAT IS HAPPENING, COMMENT OUT THIS LINE.
        return list;
    }
}