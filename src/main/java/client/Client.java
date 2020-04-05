package client;

import com.conorjc.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import jmdns.GetRequest;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


    public class Client {

        public static void main(String[] args) {
            Client main = new Client();
            main.run();

            try {
                // Create a JmDNS instance
                JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
                // Add a service listener
                jmdns.addServiceListener("_printerServiceImpl._tcp.local.", new SampleListener());
                jmdns.addServiceListener("_thermoServiceImpl._tcp.local.", new SampleListener());
                jmdns.addServiceListener("_lightingServiceImpl._tcp.local.", new SampleListener());
                jmdns.addServiceListener("_vpnServiceImpl._tcp.local.", new SampleListener());
            } catch (UnknownHostException e) {

                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void run() {
            System.out.println("Client Interface Initialising...");

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


            // Comment & Un-Comment To Use Different Streams

           UnaryServices(channel, channel1, channel2, channel3);
           // PrintTestPageService(channel);
            // PrinterTestServerSide(channel);
           // PrintDocuments(channel);

            //HeatUpServiceServerSide(channel);
            //CoolDownServiceClientSide(channel);
            //ThermoMonitorService(channel);


        }

        //Unary Services - One Request and One Response
        private void UnaryServices(ManagedChannel channel, ManagedChannel channel1, ManagedChannel channel2, ManagedChannel channel3) {

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


        //The Printer Services - SelfTest, TestPage, PrintQueueDocuments

        private void PrintTestPageService(ManagedChannel channel) {

            /*------- Print Test Page------*/

            PrintServiceGrpc.PrintServiceStub asyncClient = PrintServiceGrpc.newStub(channel);
            CountDownLatch latch = new CountDownLatch(1);

            StreamObserver<PrintTestRequest> requestObserver = asyncClient.printTest(new StreamObserver<PrintTestResponse>() {
                @Override
                public void onNext(PrintTestResponse value) {
                    //we get a response from the server
                    System.out.println("Received Response from the server");
                    System.out.println(value.getResult());
                    //onNext will only be called once
                }

                @Override
                public void onError(Throwable t) {
                    //we get an error from the server
                }

                @Override
                public void onCompleted() {
                    //the server is done sending us data
                    //onCompleted will be called right after onNext()
                    System.out.println("Server has completed sending data");
                    latch.countDown();
                }
            });
            //Sending Message 1
            requestObserver.onNext(PrintTestRequest.newBuilder()
                    .setStatus(Printer.newBuilder()
                            .setTestpage("\nThis Is The Test Page")
                            .build())
                    .build());

            //Sending Message 2
            requestObserver.onNext(PrintTestRequest.newBuilder()
                   .setStatus(Printer.newBuilder()
                            .setTestpage("\nIf this page is visible to\n" +
                                    "you then you are right in \n" +
                                    "presuming that the printer\n" +
                                    "is working correctly!!!   \n" +
                                    "White Ink Test Complete   \n")
                            .build())
                    .build());

            //Sending Message 3
            requestObserver.onNext(PrintTestRequest.newBuilder()
                 .setStatus(Printer.newBuilder()
                            .setTestpage("End of TestPage \n")
                            .build())
                    .build());

            //we tell the server that that client is done sending data
            requestObserver.onCompleted();
            try {
                latch.await(3L, TimeUnit.SECONDS);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.shutdown();
        }

        private void PrinterTestServerSide(ManagedChannel channel) {

            /*------- Check Printer (Tests)------*/

            PrintServiceGrpc.PrintServiceBlockingStub printClient = PrintServiceGrpc.newBlockingStub(channel);
            //Server Streaming Side
            CheckPrinterRequest checkPrinterRequest = CheckPrinterRequest.newBuilder()
                    .setStatus(Printer.newBuilder()
                            .setStatus(true))
                    .build();
            //Server Streaming
            printClient.checkPrinter(checkPrinterRequest)
                    .forEachRemaining(checkPrinterResponse -> {
                        System.out.println(checkPrinterResponse.getNetwork() + checkPrinterResponse.getCartridge() + checkPrinterResponse.getInk()+ checkPrinterResponse.getResult() );
                    });
            channel.shutdown();
        }
        private void PrintDocuments(ManagedChannel channel) {

            /*------- Print Documents BiDi Streaming------*/

            PrintServiceGrpc.PrintServiceStub asyncClient = PrintServiceGrpc.newStub(channel);
            CountDownLatch latch = new CountDownLatch(1);
            StreamObserver<DocumentRequest> requestObserver = asyncClient.document(new StreamObserver<DocumentResponse>() {
                @Override
                public void onNext(DocumentResponse value) {
                    System.out.println("Printing Queue..." + value.getResult());
                }

                @Override
                public void onError(Throwable t) {
                }

                @Override
                public void onCompleted() {
                    System.out.println("Server is done sending data");

                }
            });
            Arrays.asList("Document 1", "Document 2", "Document 3", "Document 4").forEach(
                    documents -> {
                        System.out.println("Sending: " + documents);
                        requestObserver.onNext(DocumentRequest.newBuilder()
                                .setDts(Printer.newBuilder()
                                        .setDocuments(documents)
                                        .build())
                                .build());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
            requestObserver.onCompleted();
            try {
                latch.await(3L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
          e.printStackTrace();
            }
        }


        //ThermoStat Services - HeatUp, CoolDown, ThermoMonitor
        private void HeatUpServiceServerSide(ManagedChannel channel) {

            /*-------Heat Up Service------*/
            ThermoServiceGrpc.ThermoServiceBlockingStub thermoClient = ThermoServiceGrpc.newBlockingStub(channel);
            //Server Streaming Side
            HeatUpRequest heatUpRequest = HeatUpRequest.newBuilder()
                    .setStat(Thermo.newBuilder()
                            .setStatus(true))
                    .build();

            //Server Streaming
            thermoClient.warmAirService(heatUpRequest)
                    .forEachRemaining(heatUpResponse -> {
                        System.out.println(heatUpResponse.getLevel1() + heatUpResponse.getLevel2() + heatUpResponse.getLevel3() + heatUpResponse.getResult() );
                    });
            channel.shutdown();
        }
        private void ThermoMonitorService(ManagedChannel channel) {
            ThermoServiceGrpc.ThermoServiceStub asyncClient = ThermoServiceGrpc.newStub(channel);
            CountDownLatch latch = new CountDownLatch(1);
            StreamObserver<ThermoMonitorRequest> requestObserver = asyncClient.thermoMonitorService(new StreamObserver<ThermoMonitorResponse>() {
                @Override
                public void onNext(ThermoMonitorResponse value) {
                    String result = "Sending Temperature" + value.getResult();
                }

                @Override
                public void onError(Throwable t) {
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    System.out.println("Server has finished sending the data");
                    System.out.println("Your room is 23 degrees celsius, result: ");
                }
            });
            Arrays.asList("Temp Reading 1", "Temp reading 2", "Temp Reading 3", "Temp Reading 4").forEach(
                    sensorRead -> {
                        System.out.println("Sending: " + sensorRead);
                        requestObserver.onNext(ThermoMonitorRequest.newBuilder()
                                .setMon(Thermo.newBuilder()
                                        .setSensor(sensorRead)
                                        .build())
                                .build());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
            requestObserver.onCompleted();

            try {
                latch.await(3L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




        private static class SampleListener implements ServiceListener {
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
