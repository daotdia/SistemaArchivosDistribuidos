package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioDiscoClienteInterface extends Remote{
	
	//Descarga al pathlocal indicado por el cliente el archivo indicado en la
	//rutaremota que le pasa el Servicio Servidor-Operador
	public void bajarFichero (Repositorio repo, String nombre_fichero, String path_local, String nombre_cliente) throws RemoteException, Exception;
}
