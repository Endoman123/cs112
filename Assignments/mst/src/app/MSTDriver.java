package app;

import structures.*;

import java.io.*;
import java.util.*;

public class MSTDriver {
    public static void main(String[] args) {
        try {
            Graph g = new Graph("graph2.txt");
            ArrayList<Arc> result = PartialTreeList.execute(PartialTreeList.initialize(g));

            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
