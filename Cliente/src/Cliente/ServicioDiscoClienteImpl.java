/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa el servicio disco cliente encargado de ejecutar la bajada de archivos indicada por el SSO del repositorio
 * (gestionado a su vez por el servicio gestor el cuál sigue las indicaciones del cliente).
 * 
 * */
package Cliente;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import es.uned.sisdist.common.Fichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;

public class ServicioDiscoClienteImpl implements ServicioDiscoClienteInterface {

	//Método que se encarga de bajar un fichero de un repositorio concreto al path indicado por el cliente.
	public void bajarFichero (Repositorio repo, String nombre_fichero, String path_local, String nombre_cliente) throws RemoteException, Exception {
		Fichero fichero;
		try {
			fichero = new Fichero(repo.getPath()+ File.separator + nombre_cliente, nombre_fichero, nombre_cliente);
		} catch (NullPointerException e) {
			throw new NullPointerException("fichero no encontrao en ningún repositorio");
		}
		File output = new File(path_local + File.separator + nombre_fichero);
		OutputStream os = new FileOutputStream(output);
		fichero.escribirEn(os);
		System.out.println("Fichero "+ nombre_fichero + " de " + nombre_cliente + " subido en " + repo.getPath() + " se ha descargado en " + path_local);
	}
}
