package jmdns;

/**
 * @author dominic
 */
public interface ServiceObserver {

    boolean interested(String type);

    void serviceAdded(ServiceDescription service);

    String getName();

}
