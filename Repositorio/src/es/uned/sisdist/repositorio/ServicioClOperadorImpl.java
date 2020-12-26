/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa el servicio cliente operador que se encarga de subir y eliminar los archivos según lo indicado
 * por el servicio gestor del servidor.
 * 
 * */
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
	
	//Método para subir un fichero deun cliente en un repositorio concreto.
	public void subirArchivo(Fichero fichero,Repositorio repo, String nombre_cliente, String nombre_fichero) throws RemoteException, IOException {
		File output = new File(repo.getPath()+ File.separator + nombre_cliente + File.separator + nombre_fichero);
		OutputStream os = new FileOutputStream(output);
		fichero.escribirEn(os);
		System.out.println("Fichero "+ nombre_fichero + " de " + nombre_cliente + " subido en " + repo.getPath());
	}

	//Método para eliminar un fichero de cliente del repositorio indicado.
	public void deleteArchivo(Repositorio repo, String nombre_fichero, String nombre_cliente) throws Exception {
		try{
			File file = new File(repo.getPath() + File.separator + nombre_cliente + File.separator + nombre_fichero);
			file.delete();
		} catch(Exception e) {
			throw new Exception();
		}
		
	}

}
