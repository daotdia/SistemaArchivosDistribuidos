package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioClOperadorInterface extends Remote{
	
	//Sube el archivo el mismo a partir de la URL que le da el servidor
	//y el archivo que quiere subir
	public boolean subirArchivo (URL URL, MetaFichero Fichero) throws RemoteException;
	
	public boolean deleteArchivo (URL URL) throws RemoteException;
}
