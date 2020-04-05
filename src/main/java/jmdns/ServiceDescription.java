/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmdns;

/**
 * @author dominic
 */
public final class ServiceDescription {

    private final String address;

    public ServiceDescription(String address, int port) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

}
