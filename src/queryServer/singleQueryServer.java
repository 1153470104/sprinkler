package queryServer;

import BPlusTree.BPTKey.BPTKey;
import BPlusTree.keyType.MortonCode;
import dispatcher.dataTool;
import metadataServer.singleMetaServer;

import java.io.IOException;
import java.util.*;

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
    public void querying() throws IOException, NullPointerException{
        System.out.println("Query start: ");
        int queryTimeStart;
        int queryTimeEnd;
        MortonCode startKey;
        MortonCode endKey;
        while(scan.hasNext()){
            String s = scan.nextLine();
             List<String> queryValue;
             /* use no such element exception of get rid of the wrong input problem*/
            try {
                queryValue = queryLineParse(s);
             } catch (NoSuchElementException e) {
                System.out.println("Illegal input, please query again.");
                continue;
            }
            queryTimeStart = Integer.parseInt(queryValue.get(0));
            queryTimeEnd = Integer.parseInt(queryValue.get(1));
            startKey = new MortonCode(queryValue.get(2));
            endKey = new MortonCode(queryValue.get(3));
            System.out.println();
            List<BPTKey<MortonCode>> result = metaServer.searchKey(queryTimeStart, queryTimeEnd, startKey, endKey);
//            List<BPTKey<MortonCode>> fineResult = MortonCode.regionCut(result);
            System.out.println(dataTool.listToString(result)); // TODO maybe could make some output
//            System.out.println(dataTool.listToString(fineResult)); // TODO maybe could make some output

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
    private List<String> queryLineParse(String line) throws NoSuchElementException{
        List<String> queryContent = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(line, ";");
        queryContent.add(st.nextToken());
        queryContent.add(st.nextToken());
        queryContent.add(st.nextToken());
        queryContent.add(st.nextToken());
        return queryContent;
    }
}
