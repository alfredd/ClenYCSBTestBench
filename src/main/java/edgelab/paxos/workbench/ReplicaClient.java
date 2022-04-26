package edgelab.paxos.workbench;

import com.google.protobuf.ByteString;
import edgelab.lc.ReadResponse;
import edgelab.paxos.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ReplicaClient {
    private final ManagedChannel channel;
    private final ReplicaGrpc.ReplicaBlockingStub blockingStub;

    public ReplicaClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    ReplicaClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = ReplicaGrpc.newBlockingStub(channel);
    }

    public KV write(String key, String value) {
        ByteString bsValue = ByteString.copyFromUtf8(value);
        ByteString bsKey = ByteString.copyFromUtf8(key);
        KV kv = KV.newBuilder().setKey(bsKey).setValue(bsValue).build();
        KV response = blockingStub.write(kv);
        return response;
    }


    public KV read(String key) {
        KV request = KV.newBuilder().setKey(ByteString.copyFromUtf8(key)).build();
        KV response = blockingStub.read(request);
        return response;
    }
}
