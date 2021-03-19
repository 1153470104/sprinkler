package queryServer;

import BPlusTree.keyType.MortonCode;
import metadataServer.singleMetaServer;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

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

    /**
     * query function
     * the query line should be like that:
     * {srart time};{end time};{start coordinate};{end coordinate}
     */
    public void querying() {
        System.out.println("Query start: ");
        int queryTimeStart;
        int queryTimeEnd;
        MortonCode startKey;
        MortonCode endKey;
        while(scan.hasNext()){
            String s = scan.nextLine();
            List<String> queryValue = queryLineParse(s);
            queryTimeStart = Integer.parseInt(queryValue.get(0));
            queryTimeEnd = Integer.parseInt(queryValue.get(1));
            startKey = new MortonCode(queryValue.get(2));
            endKey = new MortonCode(queryValue.get(3));

            if(s.equals("exit")) {
                break;
            }
//            System.out.println("Query request: " + s);
        }
        scan.close();
    }

    /**
     * parse the query line into three strings
     * @param line the query line to be parsed
     * @return a list of four strings
     *     which is start-time end-time start-coordinate
     *     and end-coordinate respectively
     */
    private List<String> queryLineParse(String line) {
        List<String> queryContent = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(line, ";");
        queryContent.add(st.nextToken());
        queryContent.add(st.nextToken());
        queryContent.add(st.nextToken());
        queryContent.add(st.nextToken());
        return queryContent;
    }
}
