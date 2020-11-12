package es.uned.sisdist.common;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioClOperadorInterface extends Remote{
	
	//Sube el archivo el mismo a partir de la URL que le da el servidor
	//y el archivo que quiere subir
	public void subirArchivo (String path, String nombre_fichero, String nombre_cliente) throws RemoteException, IOException;
	
	public void deleteArchivo (MetaFichero metafichero, String nombre_cliente) throws RemoteException, IOException;
}
