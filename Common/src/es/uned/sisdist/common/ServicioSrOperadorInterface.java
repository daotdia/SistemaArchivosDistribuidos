package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioSrOperadorInterface extends Remote{
	//Crea la carpeta del cliente donde se gestionaran sus archivos.
	public boolean crearCarpeta (int identifciador, int id_repositorio) throws RemoteException;
	
	public boolean borrarCarpeta (int identifciador) throws RemoteException;
	
	//Le pasa a DiscoCliente el pathremoto donde esta el archivo remoto que quiere bajar el cliente,
	//para ello utiliza la URL que le da el servidor
	public String bajarArchivo (URL URL) throws RemoteException;
}
