package es.uned.sisdist.common;

import java.io.File;
import java.io.Serializable;

public class Repositorio implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String nombre;
	private int identificador; 
	private String path;
	private int port_sso, port_sco;
	
	public Repositorio (String nombre, int port) {
		this.nombre = nombre;
		//El path del repositorio va a ser su dirección de trabajo actua, en un directorio con su nombre.
		path = System.getProperty("user.dir")+ "/Repositorio_" + nombre;
		File directorio=new File(path);
		directorio.mkdir();
		this.port_sso = port;
		this.port_sco = port + 1;
	}
	
	public int getId(){
		return identificador;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setId(int id){
		identificador = id;
	}
	
	public String getNombre(){
		return nombre;
	}
	
	public int getPortSso() {
		return port_sso;
	}
	
	public int getPortSco() {
		return port_sco;
	}
}
