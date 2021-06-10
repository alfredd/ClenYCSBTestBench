package edgelab.lc.workbench;

import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.Status;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ClenYCSBClient extends DB {
    @Override
    public Status read(String s, String s1, Set<String> set, Map<String, ByteIterator> map) {
        return null;
    }

    @Override
    public Status scan(String s, String s1, int i, Set<String> set, Vector<HashMap<String, ByteIterator>> vector) {
        return null;
    }

    @Override
    public Status update(String s, String s1, Map<String, ByteIterator> map) {
        return null;
    }

    @Override
    public Status insert(String s, String s1, Map<String, ByteIterator> map) {
        return null;
    }

    @Override
    public Status delete(String s, String s1) {
        return null;
    }
}
