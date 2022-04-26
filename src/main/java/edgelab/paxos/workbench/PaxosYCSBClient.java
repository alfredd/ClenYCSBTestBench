package edgelab.paxos.workbench;

import com.google.protobuf.ByteString;
import edgelab.paxos.KV;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.Status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PaxosYCSBClient extends DB {

    private final ReplicaClient replicaClient;

    public PaxosYCSBClient() {
        String hostPort = System.getenv("PAXOS");
        String[] hostPortTuple = extractHostAndPort(hostPort);
        String host = hostPortTuple[0];
        String port = hostPortTuple[1];
        if (!port.equals("")) {

            int intPort = Integer.parseInt(port);
            replicaClient = new ReplicaClient(host, intPort);
        } else {
            throw new RuntimeException("Cannot load leader port");
        }
    }

    protected static String[] extractHostAndPort(String hostPort) {
        String[] hostAndPort = {"localhost", "35001"};
        if (hostPort!=null && !hostPort.equals("")){
            String[] hp = hostPort.split(":");
            if (hp.length>0) {
                if (hp!= null && !hp[0].equals("")) {
                    hostAndPort[0]=hp[0];
                }
                if (!hp[1].equals("")) {
                    hostAndPort[1]=hp[1];
                }
            }
        }
        return hostAndPort;
    }

    @Override
    public Status read(String table, String key, Set<String> set, Map<String, ByteIterator> map) {
        KV response = replicaClient.read(table+"."+key);
        return Status.OK;
    }

    @Override
    public Status scan(String s, String s1, int i, Set<String> set, Vector<HashMap<String, ByteIterator>> vector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status update(String table, String key, Map<String, ByteIterator> values) {
        try {
            byte[] valueBytes = serializeValues(values);
            String clenKey = table+"."+key;
            String data = ByteString.copyFrom(valueBytes).toString();
            replicaClient.write(clenKey, data);
            return Status.OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.ERROR;
    }

    @Override
    public Status insert(String table, String key, Map<String, ByteIterator> values) {
        return update(table, key, values);
    }

    @Override
    public Status delete(String s, String s1) {
        throw new UnsupportedOperationException();
    }


    /**
     * Code for serializeValues and deserailizeValues taken from:
     * https://github.com/brianfrankcooper/YCSB/blob/0.17.0/rocksdb/src/main/java/site/ycsb/db/rocksdb/RocksDBClient.java
     *
     * @param values
     * @return
     * @throws IOException
     */
    private byte[] serializeValues(final Map<String, ByteIterator> values) throws IOException {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final ByteBuffer buf = ByteBuffer.allocate(4);

            for (final Map.Entry<String, ByteIterator> value : values.entrySet()) {
                final byte[] keyBytes = value.getKey().getBytes(UTF_8);
                final byte[] valueBytes = value.getValue().toArray();

                buf.putInt(keyBytes.length);
                baos.write(buf.array());
                baos.write(keyBytes);

                buf.clear();

                buf.putInt(valueBytes.length);
                baos.write(buf.array());
                baos.write(valueBytes);

                buf.clear();
            }
            return baos.toByteArray();
        }
    }

}
