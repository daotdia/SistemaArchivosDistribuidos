package es.uned.sisdist.common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioClOperadorInterface extends Remote{
	
	//Sube el archivo el mismo a partir de la URL que le da el servidor
	//y el archivo que quiere subir
	public void subirArchivo(Fichero fichero, Repositorio repo, String nombre_cliente, String nombre_fichero) throws RemoteException, IOException;
	
	public void deleteArchivo(Repositorio repo, String nombre_fichero, String nombre_cliente) throws RemoteException, Exception;
}
