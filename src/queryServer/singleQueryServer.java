package queryServer;

import java.util.Scanner;

/**
 * query server
 */
public class singleQueryServer {
    Scanner scan;
    public singleQueryServer() {
        scan = new Scanner(System.in);
    }

    public void querying() {
        System.out.println("Query start: ");
        while(scan.hasNext()){
            String s = scan.nextLine();
            if(s.equals("exit")) {
                break;
            }
            System.out.println("Query request: " + s);
        }
        scan.close();
    }
}
