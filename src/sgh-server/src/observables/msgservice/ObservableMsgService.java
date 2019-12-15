package observables.msgservice;

import observables.Observable;

/**
 * 
 * Represents an observable message service, generating an event whenever it
 * gets a message from serial port.
 *
 */
public interface ObservableMsgService extends Observable, MsgService {

}
