package client;

import com.conorjc.proto.PrintServiceGrpc;
import com.conorjc.proto.Printer;
import com.conorjc.proto.PrinterStatusRequest;
import com.conorjc.proto.PrinterStatusResponse;
import com.proto.thermo.Thermo;
import com.proto.thermo.ThermoRequest;
import com.proto.thermo.ThermoResponse;
import com.proto.thermo.ThermoServiceGrpc;
import com.proto.vpn.Vpn;
import com.proto.vpn.VpnServiceGrpc;
import com.proto.vpn.VpnStatusRequest;
import com.proto.vpn.VpnStatusResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ClientUnary {

    public static  void main(String[] args) {

        System.out.println("Client Interface Initialising...");
        System.out.println("Building Channels...");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5000)
                .usePlaintext()
                .build();
        ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 5001)
                .usePlaintext()
                .build();
        ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 5002)
                .usePlaintext()
                .build();
        ManagedChannel channel3 = ManagedChannelBuilder.forAddress("localhost", 5003)
                .usePlaintext()
                .build();


        /*------Check Whether Printer is Off/On----- */

        PrintServiceGrpc.PrintServiceBlockingStub printClient = PrintServiceGrpc.newBlockingStub(channel);

        Printer printerStatus = Printer.newBuilder()
                .setStatus(true)
                .build();

        PrinterStatusRequest printerStatusRequest = PrinterStatusRequest.newBuilder()
                .setStatus(printerStatus)
                .build();

        PrinterStatusResponse printerStatusResponse = printClient.printerStatus(printerStatusRequest);
        System.out.println(printerStatusResponse.getResult());

        channel.shutdown();

        /* -----------Vpn Status-----------*/

        VpnServiceGrpc.VpnServiceBlockingStub vpnClient = VpnServiceGrpc.newBlockingStub(channel1);

        Vpn vpnStatus = Vpn.newBuilder()
                .setStatus(true)
                .build();

        VpnStatusRequest vpnStatusRequest = VpnStatusRequest.newBuilder()
                .setVpn(vpnStatus)
                .build();

        VpnStatusResponse vpnStatusResponse = vpnClient.vpnStatus(vpnStatusRequest);
        System.out.println(vpnStatusResponse.getResult());

        channel1.shutdown();

        /*------------ThermoStat Status------------*/

        ThermoServiceGrpc.ThermoServiceBlockingStub thermoClient = ThermoServiceGrpc.newBlockingStub(channel2);
        Thermo thermoStatus = Thermo.newBuilder()
                .setStatus(false)
                .build();

        ThermoRequest thermoRequest = ThermoRequest.newBuilder()
                .setStatus(thermoStatus)
                .build();

        ThermoResponse thermoResponse = thermoClient.thermoStatus(thermoRequest);
        System.out.println(thermoResponse.getResult());
      }
    }
