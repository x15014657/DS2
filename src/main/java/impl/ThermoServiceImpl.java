package impl;

import com.proto.thermo.Thermo;
import com.proto.thermo.ThermoRequest;
import com.proto.thermo.ThermoResponse;
import com.proto.thermo.ThermoServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ThermoServiceImpl extends ThermoServiceGrpc.ThermoServiceImplBase{

    @Override //Unary
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
