package es.uned.sisdist.common;

import java.io.Serializable;

public class MetaFichero implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	private String propietario, nombre;
	
	
	public MetaFichero (Fichero fichero) {
		this.propietario = fichero.obtenerPropietario();
		this.nombre = fichero.obtenerNombre();
	}
	
	public String getNombre() {
		return nombre;
	}

}
