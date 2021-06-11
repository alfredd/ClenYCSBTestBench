package edgelab.lc.workbench;

import com.google.protobuf.ByteString;
import edgelab.lc.*;
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

    public Dummy commit(String key, String value) {
        ByteString bsValue = ByteString.copyFromUtf8(value);
        ByteString bsKey = ByteString.copyFromUtf8(key);;
        CommitData request = CommitData.newBuilder().addData(KeyVal.newBuilder().setKey(bsKey).setValue(bsValue).build()).build();
        Dummy response = blockingStub.commit(request);
        return response;
    }


    public ReadResponse read(String key) {
        KeyVal request = KeyVal.newBuilder().setKey(ByteString.copyFromUtf8(key)).build();
        ReadResponse response = blockingStub.read(request);
        return response;
    }
}
