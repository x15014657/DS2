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


@SuppressWarnings("ALL")
public class ClientSSide {



    public static void main(String[] args) throws IOException {

        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener("_printerServiceImpl._tcp.local.", new ClientUnary.SampleListener());

        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Server Side Streaming Client Interface Initialising...");


        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5000)
                .usePlaintext()
                .build();
        ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 5001)
                .usePlaintext()
                .build();
        ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 5002)
                .usePlaintext()
                .build();




        //ServerStreaming
        PrintServiceGrpc.PrintServiceBlockingStub printClient = PrintServiceGrpc.newBlockingStub(channel);

        //Server Streaming Side
        CheckPrinterRequest checkPrinterRequest = CheckPrinterRequest.newBuilder()
                .setStatus(Printer.newBuilder()
                        .setStatus(true))
                .build();

        //Server Streaming
        printClient.checkPrinter(checkPrinterRequest)
                .forEachRemaining(checkPrinterResponse -> {
                    System.out.println(checkPrinterResponse.getNetwork());
                    System.out.println(checkPrinterResponse.getCartridge());
                    System.out.println(checkPrinterResponse.getInk());
                    System.out.println(checkPrinterResponse.getResult());
                });

        channel.shutdown();



            class SampleListener implements ServiceListener {

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
    }
