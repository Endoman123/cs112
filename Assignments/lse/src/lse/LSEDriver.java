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
            Scanner sc = new Scanner(new FileInputStream("noisewords.txt"));

            while (sc.hasNext())
                lse.noiseWords.add(sc.nextLine().trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(lse.getKeyword("a!!"));
    }
}
