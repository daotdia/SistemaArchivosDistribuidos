package es.uned.sisdist.common;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Repositorio implements Remote{
	
	private String nombre;
	private int identificador; 
	private String path;
	
	public Repositorio (String nombre) throws RemoteException {
		this.nombre = nombre;
		//El path del repositorio va a ser su dirección de trabajo actua, en un directorio con su nombre.
		path = System.getProperty("user.dir")+ "/Repositorio_" + nombre;
		File directorio=new File(path);
		directorio.mkdir();
	}
	
	public int getId() {
		return identificador;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setId(int id) {
		identificador = id;
	}
	
	public String getNombre() {
		return nombre;
	}
}
