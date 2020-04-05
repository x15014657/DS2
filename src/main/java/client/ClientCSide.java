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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientCSide {

    public static void main(String[] args) {

        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener("_printerServiceImpl._tcp.local.", new ClientCSide.SampleListener());



        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Client Side Streaming Client Interface Initialising...");


        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5000)
                .usePlaintext()
                .build();
        ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 5001)
                .usePlaintext()
                .build();
        ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 5002)
                .usePlaintext()
                .build();


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
            }
        });
        latch.countDown();

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
