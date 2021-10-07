package edgelab.lc.workbench;

import org.junit.Assert;
import org.junit.Test;

public class TestClenYCSBClient {

    @Test
    public void test1() {
        String[] result = ClenYCSBClient.extractHostAndPort("1.2.3.4:345");
        Assert.assertEquals("1.2.3.4", result[0]);
        Assert.assertEquals("345", result[1]);
    }

    @Test
    public void test2() {
        String[] result = ClenYCSBClient.extractHostAndPort(":");
        Assert.assertEquals("localhost", result[0]);
        Assert.assertEquals("35001", result[1]);
    }
}
