package impl;

import com.conorjc.proto.Thermo;
import com.conorjc.proto.ThermoRequest;
import com.conorjc.proto.ThermoResponse;
import com.conorjc.proto.ThermoServiceGrpc;
import io.grpc.stub.StreamObserver;


public class ThermoServiceImpl extends ThermoServiceGrpc.ThermoServiceImplBase{


    public void thermoStatus(ThermoRequest request, StreamObserver<ThermoResponse> responseObserver) {
        Thermo theroStat = request.getStatus();
        boolean status = theroStat.getStatus();

        //create the  response
        String result;
        if (!status) {
            result = "The Thermostat is Offline";
        } else {
            result = "The Thermostat is Online";
        }

        ThermoResponse response = ThermoResponse.newBuilder()
                .setResult(result)
                .build();

        //sends response to client
        responseObserver.onNext(response);

        //complete the RPC call
        responseObserver.onCompleted();
    }
}
