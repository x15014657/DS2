package impl;

import com.proto.vpn.Vpn;
import com.proto.vpn.VpnServiceGrpc;
import com.proto.vpn.VpnStatusRequest;
import com.proto.vpn.VpnStatusResponse;
import io.grpc.stub.StreamObserver;

public class VpnServiceImpl extends VpnServiceGrpc.VpnServiceImplBase {

    @Override
    public void vpnStatus(VpnStatusRequest request, StreamObserver<VpnStatusResponse> responseObserver) {
        Vpn vpnStatus = request.getVpn();
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

