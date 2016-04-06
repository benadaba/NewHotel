/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package suncertify.server;

import java.io.FileNotFoundException;
import java.rmi.*;

/**
 * The remote interface for obtaining the remote object reference.
 * Uses Factory pattern to let the subclass to implement the method that is 
 * needed.
 * 
 * @author Bernard
 */
public interface RemoteDatabaseFactory extends Remote {

    /**
      * Called to obtain reference (stub) to the remote object, RemoteDatabase
      * @return RemoteDatabase reference (stub) to the remote object
      * @throws java.rmi.RemoteException if registry could not be contacted
      * @throws FileNotFoundException if the specified file cannot be found
      */
    public RemoteDatabase getClient() 
            throws RemoteException, FileNotFoundException;
}
