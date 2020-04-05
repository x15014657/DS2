package impl;


import com.conorjc.proto.Vpn;
import com.conorjc.proto.VpnServiceGrpc;
import com.conorjc.proto.VpnStatusRequest;
import com.conorjc.proto.VpnStatusResponse;
import io.grpc.stub.StreamObserver;

public class VpnServiceImpl extends VpnServiceGrpc.VpnServiceImplBase {

    @Override
    public void vpnStatus(VpnStatusRequest request, StreamObserver<VpnStatusResponse> responseObserver) {
        Vpn vpnStatus = request.getStatus();
        boolean status = vpnStatus.getStatus();

        //create the  response
        String result;
        if(!status){
            result = "The Vpn is Offline";
        }else{
            result = "The Vpn is Online";
        }

        VpnStatusResponse response = VpnStatusResponse.newBuilder()
                .setResult(result)
                .build();

        //sends response to client
        responseObserver.onNext(response);

        //complete the RPC call
        responseObserver.onCompleted();
    }
}

