/* 
 * Autor:David Otero Díaz.
 * Mail: dotero64@alumno.uned.es
 * 
 * Clase que implementa la información relacionada con cada fichero que debe de quedar reflejada en el servicio de datos
 * del sistema remoto.
 * 
 * */
package es.uned.sisdist.common;

import java.io.Serializable;

public class MetaFichero implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	private String propietario, nombre;
	
	//Guarda por cada fichero su propietario y su nombre.
	public MetaFichero (Fichero fichero) {
		this.propietario = fichero.obtenerPropietario();
		this.nombre = fichero.obtenerNombre();
	}
	
	//Tambiñen se puede crear a partir de un nombre de propietario y de fichero.
	public MetaFichero (String propietario, String nombre) {
		this.propietario = propietario;
		this.nombre = nombre;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getPropietario() {
		return propietario;
	}

}
