package lse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Simple drive for LSE
 */
public class LSEDriver {
    public static void main(String[] args) {
        LittleSearchEngine lse = new LittleSearchEngine();

        try {
            lse.makeIndex("docs.txt", "noisewords.txt");

            System.out.println(lse.keywordsIndex);
            System.out.println(lse.top5search("deep", "world"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
