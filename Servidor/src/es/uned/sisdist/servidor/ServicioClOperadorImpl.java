package es.uned.sisdist.servidor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.uned.sisdist.common.Fichero;
import es.uned.sisdist.common.MetaFichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioClOperadorInterface;
import es.uned.sisdist.common.ServicioDatosInterface;

public class ServicioClOperadorImpl implements ServicioClOperadorInterface{

	private ServicioDatosInterface bd;
	
	public ServicioClOperadorImpl() throws RemoteException, Exception{
		Registry registry = LocateRegistry.getRegistry(7777);
		this.bd = (ServicioDatosInterface) registry.lookup("datos_remotos");
	}
	
	@Override
	public void subirArchivo(Fichero fichero,Repositorio repo, String nombre_cliente, String nombre_fichero) throws RemoteException, IOException {
		File output = new File(repo.getPath()+ "/" + nombre_cliente + "/" + nombre_fichero);
		OutputStream os = new FileOutputStream(output);
		fichero.escribirEn(os);
		System.out.println("Fichero "+ nombre_fichero + " de " + nombre_cliente + " subido en " + repo.getPath());
	}

	//Elimina todos los archivos que se llamas igual en los dsitintos repositorios del usuario.
	@Override
	public void deleteArchivo(Repositorio repo, String nombre_fichero, String nombre_cliente) throws RemoteException {
		File file = new File(repo.getPath() + "/" + nombre_cliente + "/" + nombre_fichero);
		file.delete();
	}

}
