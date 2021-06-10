package edgelab.lc.workbench;

import edgelab.lc.EdgeNodeGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClenClient {
    private final ManagedChannel channel;
    private final EdgeNodeGrpc.EdgeNodeBlockingStub blockingStub;

    public ClenClient(String host, int port){
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    ClenClient(ManagedChannel channel){
        this.channel = channel;
        blockingStub = EdgeNodeGrpc.newBlockingStub(channel);
    }

    public void commit() {

    }
    public void read() {

    }
}
