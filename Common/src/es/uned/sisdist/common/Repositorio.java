package es.uned.sisdist.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Repositorio implements Remote{
	
	private String nombre;
	private int identificador; 
	
	public Repositorio (String nombre) throws RemoteException {
		this.nombre = nombre;
	}
	
	public int getId() {
		return identificador;
	}
	
	public void setId(int id) {
		identificador = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	//Necesito utilizar método equals para poder determinar si dentro de una lista de repositorios
	//existe un repositorio con dicho nombre (ya que el método contains de ArrayList utiliza este método)
	@Override
    public boolean equals(Object o){
        if(o instanceof Repositorio){
             Repositorio p = (Repositorio) o;
             return this.nombre.equals(p.getNombre());
        } else
             return false;
    }
}
