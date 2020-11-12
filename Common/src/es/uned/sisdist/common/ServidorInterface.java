package es.uned.sisdist.common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServidorInterface extends Remote{
		public void menu_inicial(String nombre, int tipo, int opcion)
				throws RemoteException;
		
		public boolean comprobarCliente (String nombre) throws RemoteException;
		
		public void gestion_archivos (String nombre_cliente, String nombre_fichero, String path, int opcion) 
				throws RemoteException, IOException;
}
