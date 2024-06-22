/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.sga.cache_simulator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author salih
 */
public class Bus {
    private PropertyChangeSupport pcs;
    
    public Bus() {
        this.pcs = new PropertyChangeSupport(this);
    }
    
    public void addListener(Cache cache) {
        this.pcs.addPropertyChangeListener(cache);
    }
    
    public void removeListener(Cache cache) {
        this.pcs.removePropertyChangeListener(cache);
    }
    
    public void busRead(String requesterName, int address) {
        firePropertyChangeExceptRequester("busRead", requesterName, address);
    }
    
    public void busReadExclusive(String requesterName, int address) {
        firePropertyChangeExceptRequester("busReadExclusive", requesterName, address);
    }
    
    public void busUpgrade(String requesterName, int address) {
        firePropertyChangeExceptRequester("busUpgrade", requesterName, address);
    }
    
    public void busWriteBack(String requesterName, int address) {
        firePropertyChangeExceptRequester("busWriteBack", requesterName, address);
    }
    
    private void firePropertyChangeExceptRequester(String propertyName, String requesterName, int address) {
        for(PropertyChangeListener listener : pcs.getPropertyChangeListeners()) {
            if (!(listener instanceof Cache)) {
                continue;
            }
            Cache cache = (Cache) listener;
            if (!(cache.getProcessorName().equalsIgnoreCase(requesterName))) {
                cache.propertyChange(new PropertyChangeEvent(this, propertyName, null, address));
            }
        }
    }
}
