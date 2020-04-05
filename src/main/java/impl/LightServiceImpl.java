package impl;

import com.conorjc.proto.*;
import io.grpc.stub.StreamObserver;


public class LightServiceImpl extends LightsServiceGrpc.LightsServiceImplBase {

    @Override
    public void lightService(LightStatusRequest request, StreamObserver<LightStatusResponse> responseObserver) {

        Lights lightStatus = request.getStatus();
        boolean status = lightStatus.getStatus();

        //create the response
        String result;
        if (!status) {
            result = "Lights Are Currently off";
            System.out.println("Switching On Lights Now");
        } else {
            result = "Lights Are Currently On";
        }

        LightStatusResponse response = LightStatusResponse.newBuilder()
                .setResult(result)
                .build();

        //sends response to client
        responseObserver.onNext(response);
        //complete the RPC call
        responseObserver.onCompleted();
    }
}

