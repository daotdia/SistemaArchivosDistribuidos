package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioSrOperadorInterface extends Remote{
	//Crea la carpeta del cliente donde se gestionaran sus archivos.
	public void crearCarpeta (String path, String nombre_carpeta) throws RemoteException;
	
	public void borrarCarpeta (Repositorio repo, String nombre_cliente) throws RemoteException;
	
	//Le pasa a DiscoCliente el pathremoto donde esta el archivo remoto que quiere bajar el cliente,
	//para ello utiliza la URL que le da el servidor
	public void bajarFichero(Repositorio repo, String nombre_fichero, String path_local, String nombre_cliente) throws RemoteException, Exception;
}
