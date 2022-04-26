// CMP SCI 2261 - Project 6 - Webcrawler
// Terry Ford Jr.

package edu.umsl;

import java.util.ArrayList;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

        while (!listOfPendingURLs.isEmpty() &&
                listOfTraversedURLs.size() <= 1000) {
            String urlString = listOfPendingURLs.remove(0);

            if (!listOfTraversedURLs.contains(urlString)) {
                listOfTraversedURLs.add(urlString);
                System.out.println("Crawl " + urlString);

                try {
                    Thread.sleep(50);
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }

                for (String s : getSubURLs(urlString)) {
                    if (!listOfTraversedURLs.contains(s))
                        listOfPendingURLs.add(s);
                }
            }
        }
    }

    public static ArrayList<String> getSubURLs(String urlString) throws InterruptedException {
        ArrayList<String> list = new ArrayList<>();

        try {
            java.net.URL url = new java.net.URL(urlString);
            Scanner input = new Scanner(url.openStream());
            int current = 0;
            while (input.hasNext()) {
                String line = input.nextLine();
                // extra
                if (line.startsWith("<title>")) {       // TITLE PARSING
                    //String newline = line.replace("<title>", "");
                    //System.out.println("\tArticle Title: " + newline.replace("</title>", ""));

                    // USING JSOUP
                    Document doc = Jsoup.parse(line);
                    System.out.println("\tArticle Title: " + doc.title());
                }

                //System.out.println(line);
                // COUNT WORDS HERE, jsoup.parse will parse html if you do it right.

                current = line.indexOf("https://en.wikipedia.org/", current);
                while (current > 0) {
                    int endIndex = line.indexOf("\"", current);
                    if (endIndex > 0) { // Ensure that a correct URL is found
                        list.add(line.substring(current, endIndex));
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

        return list;
    }
}