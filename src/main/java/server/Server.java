package server;

import impl.PrinterServiceImpl;
import impl.ThermoServiceImpl;
import impl.VpnServiceImpl;
import io.grpc.ServerBuilder;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Servers Initialising...");
        System.out.println("Adding services...");

        io.grpc.Server server =
                ServerBuilder.forPort(5000)
                        .addService(new PrinterServiceImpl())
                        .build();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        io.grpc.Server server1 =
                ServerBuilder.forPort(5001)
                        .addService(new VpnServiceImpl())
                        .build();
        try {
            server1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        io.grpc.Server server2 =
                ServerBuilder.forPort(5002)
                        .addService(new ThermoServiceImpl())
                        .build();
        try {
            server2.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Server Initialised");

        server.awaitTermination();
        server1.awaitTermination();
        server2.awaitTermination();

        int port = 6000;
        JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
        // Register a service
        ServiceInfo serviceInfo = ServiceInfo.create("_printerServiceImpl._tcp.local.", "Printer Service", port, "Will Supply a number of Printing services");
        jmdns.registerService(serviceInfo);
        System.out.println("Starting the Print Server loop");

        ServerSocket listener = new ServerSocket(6000);
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    PrintWriter out
                            = new PrintWriter(socket.getOutputStream(), true);
                    out.println(new PrinterServiceImpl().toString());
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    socket.close();
                    listener.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

