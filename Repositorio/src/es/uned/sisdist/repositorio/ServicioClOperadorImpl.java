package es.uned.sisdist.repositorio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;

import es.uned.sisdist.common.Fichero;
import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioClOperadorInterface;

public class ServicioClOperadorImpl implements ServicioClOperadorInterface{
	
	@Override
	public void subirArchivo(Fichero fichero,Repositorio repo, String nombre_cliente, String nombre_fichero) throws RemoteException, IOException {
		File output = new File(repo.getPath()+ "/" + nombre_cliente + "/" + nombre_fichero);
		OutputStream os = new FileOutputStream(output);
		fichero.escribirEn(os);
		System.out.println("Fichero "+ nombre_fichero + " de " + nombre_cliente + " subido en " + repo.getPath());
	}

	//Elimina todos los archivos que se llamas igual en los dsitintos repositorios del usuario.
	@Override
	public void deleteArchivo(Repositorio repo, String nombre_fichero, String nombre_cliente) throws Exception {
		try{
			File file = new File(repo.getPath() + "/" + nombre_cliente + "/" + nombre_fichero);
			file.delete();
		} catch(Exception e) {
			throw new Exception();
		}
		
	}

}
