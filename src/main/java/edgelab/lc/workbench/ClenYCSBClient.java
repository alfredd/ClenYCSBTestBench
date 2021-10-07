package edgelab.lc.workbench;

import com.google.protobuf.ByteString;
import edgelab.lc.KeyVal;
import edgelab.lc.ReadResponse;
import edgelab.lc.ResponseStatus;
import site.ycsb.ByteArrayByteIterator;
import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.Status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ClenYCSBClient extends DB {

    private final ClenClient clenClient;
    private final ClenClient clenReadClient;

    public ClenYCSBClient() {
        String hostPort = System.getenv("CLEN_LEADER");
        String[] hostPortTuple = extractHostAndPort(hostPort);
        String host = hostPortTuple[0];
        String port = hostPortTuple[1];
        String readPort = System.getenv("CLENRC");
        if (!port.equals("")) {

            int intPort = Integer.parseInt(port);
            clenClient = new ClenClient("localhost", intPort);
        } else {
            throw new RuntimeException("Cannot load leader port");
        }
        if (!readPort.equals("")) {

            int intPort = Integer.parseInt(readPort);
            clenReadClient = new ClenClient("localhost", intPort);
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
    public Status read(String table, String key, Set<String> fields, Map<String, ByteIterator> result) {
        String clenKey = getClenKey(table, key);
//        ReadResponse response = clenReadClient.read(clenKey);
        ReadResponse response = clenClient.read(clenKey);
        KeyVal data = response.getData();
        byte[] valueBytes = data.getValue().toByteArray();
//        deserializeValues(valueBytes,fields,result);
        if (!response.getStatus().equals(ResponseStatus.SUCCESS)) {
            return Status.ERROR;
        } else {
            return Status.OK;
        }
    }

    @Override
    public Status scan(String table, String startKey, int recordCount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Status update(String table, String key, Map<String, ByteIterator> values) {
        try {
            byte[] valueBytes = serializeValues(values);
            String clenKey = getClenKey(table, key);
            String data = ByteString.copyFrom(valueBytes).toString();
            clenClient.commit(clenKey, data);
            return Status.OK;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Status.ERROR;
    }

    private String getClenKey(String table, String key) {
        return table + "." + key;
    }

    @Override
    public Status insert(String table, String keys, Map<String, ByteIterator> values) {
        return update(table, keys, values);
    }

    @Override
    public Status delete(String table, String key) {
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

    private Map<String, ByteIterator> deserializeValues(final byte[] values, final Set<String> fields,
                                                        final Map<String, ByteIterator> result) {
        final ByteBuffer buf = ByteBuffer.allocate(4);

        int offset = 0;
        while (offset < values.length) {
            buf.put(values, offset, 4);
            buf.flip();
            final int keyLen = buf.getInt();
            buf.clear();
            offset += 4;

            final String key = new String(values, offset, keyLen);
            offset += keyLen;

            buf.put(values, offset, 4);
            buf.flip();
            final int valueLen = buf.getInt();
            buf.clear();
            offset += 4;

            if (fields == null || fields.contains(key)) {
                result.put(key, new ByteArrayByteIterator(values, offset, valueLen));
            }

            offset += valueLen;
        }

        return result;
    }
}
