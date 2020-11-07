package es.uned.sisdist.common;

import java.rmi.RemoteException;

public interface ServicioDiscoClienteInterface {
	
	//Descarga al pathlocal indicado por el cliente el archivo indicado en la
	//rutaremota que le pasa el Servicio Servidor-Operador
	public boolean bajarFichero (String rutaremota, String pathLocal) throws RemoteException;
}
