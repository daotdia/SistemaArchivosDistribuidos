package Cliente;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import es.uned.sisdist.common.Fichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;

public class ServicioDiscoClienteImpl implements ServicioDiscoClienteInterface {

	public void bajarFichero (Repositorio repo, String nombre_fichero, String path_local, String nombre_cliente) throws RemoteException, Exception {
		System.out.println("Creando Fichero");
		Fichero fichero;
		try {
			fichero = new Fichero(repo.getPath()+ "/" + nombre_cliente, nombre_fichero, nombre_cliente);
		} catch (NullPointerException e) {
			throw new NullPointerException("fichero no encontrao en ningún repositorio");
		}
		System.out.println("Fichero creado");
		System.out.println("Tratando de iniciar subida");
		File output = new File(path_local + "/" + nombre_fichero);
		OutputStream os = new FileOutputStream(output);
		fichero.escribirEn(os);
		System.out.println("Fichero "+ nombre_fichero + " de " + nombre_cliente + " subido en " + repo.getPath() + " se ha descargado en " + path_local);
	}
}
