package client;

import com.conorjc.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jmdns.GetRequest;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientUnary {

    public static  void main(String[] args) {

        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener("_printerServiceImpl._tcp.local.", new SampleListener());

        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Unary Client Interface Initialising...");
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
                .setStatus(vpnStatus)
                .build();

        VpnStatusResponse vpnStatusResponse = vpnClient.vpnStatus(vpnStatusRequest);
        System.out.println(vpnStatusResponse.getResult());

        channel1.shutdown();

        /*------------ThermoStat Status------------*/

        ThermoServiceGrpc.ThermoServiceBlockingStub thermoClient = ThermoServiceGrpc.newBlockingStub(channel2);

        Thermo thermoStatus = Thermo.newBuilder().setStatus(true).build();

        ThermoRequest thermoRequest = ThermoRequest.newBuilder()
                .setStatus(thermoStatus).build();

        ThermoResponse thermoResponse = thermoClient.thermoStatus(thermoRequest);
        System.out.println(thermoResponse.getResult());

        channel1.shutdown();

        /*------------Lights Status------------*/

        LightsServiceGrpc.LightsServiceBlockingStub lightClient = LightsServiceGrpc.newBlockingStub(channel3);
        Lights lightStatus = Lights.newBuilder()
                .setStatus(false)
                .build();

            LightStatusRequest lightStatusRequest = LightStatusRequest.newBuilder()
                    .setStatus(lightStatus)
                    .build();

        LightStatusResponse lightStatusResponse = lightClient.lightService(lightStatusRequest);
        System.out.println(lightStatusResponse.getResult());
      }

    static class SampleListener implements ServiceListener {

        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            ServiceInfo info = event.getInfo();
            int port = info.getPort();
            String path = info.getNiceTextString().split("=")[1];
            GetRequest.request("localhost:" + port + "/" + path);
        }
    }
}
