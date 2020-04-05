
package jmdns;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

// This code is adapted from https://github.com/jmdns/jmdns
public class ServiceRegistration {

    public static class ExampleServiceRegistration {

        public static void main(String[] args) {

            try {
                // Create a JmDNS instance
                JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

                // Register a service
                ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "example", 8000, "path=index.html");
                jmdns.registerService(serviceInfo);

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
