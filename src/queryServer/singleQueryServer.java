package queryServer;

import metadataServer.singleMetaServer;

import java.util.Scanner;

/**
 * query server
 */
public class singleQueryServer {
    Scanner scan;
    public singleMetaServer metaServer;
    public singleQueryServer(singleMetaServer metaServer) {
        scan = new Scanner(System.in);
        this.metaServer = metaServer;
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
