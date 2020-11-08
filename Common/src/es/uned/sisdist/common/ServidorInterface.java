package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServidorInterface extends Remote{
		public void menu_inicial(String nombre, int tipo, int opcion)
				throws RemoteException;
		
}
