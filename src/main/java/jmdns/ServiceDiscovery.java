package jmdns;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;


// This code is adapted from https://github.com/jmdns/jmdns

public class ServiceDiscovery {


    private static class SampleListener implements ServiceListener {

        ServiceObserver observer;

        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
            event.getDNS().requestServiceInfo(event.getType(), event.getName(), 0);
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent arg0) {

            System.out.println(arg0);
            String address = arg0.getInfo().getHostAddress();
            int port = arg0.getInfo().getPort();
            String type = arg0.getInfo().getType();

            if (observer != null && observer.interested(type)) {
                observer.serviceAdded(new ServiceDescription(address, port));
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

            // Add a service listener
            jmdns.addServiceListener("_printerServiceImpl._tcp.local.", new SampleListener());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private JmDNS jmdns;
    private ServiceInfo info;

    public void JmDNSRegistrationHelper(String name, String type, String location, int port) {
        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            info = ServiceInfo.create(type, name, port,
                    "params=" + location);
            jmdns.registerService(info);
            System.out.println("ServiceInfo: " + info.toString() + "\nJMDNS: " + jmdns.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int findPort() throws IOException {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }

    public void deRegister() {
        jmdns.unregisterService(info);

    }

    public ServiceInfo getInfo() {
        return info;
    }
}