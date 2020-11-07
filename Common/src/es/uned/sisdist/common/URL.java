package es.uned.sisdist.common;

public class URL {
	private String ip;
	private int puerto;
	private String nombre_servicio;
	private int identificador;
		
	public URL (String ip, int puerto, String nombre_servicio, int identificador) {
		this.ip = ip;
		this.identificador = identificador;
		this.nombre_servicio = nombre_servicio;
		this.puerto = puerto;
	}	
	
	public String getNombreServicio() {
		return nombre_servicio;
	}
	
	public int getIdentificador() {
		return identificador;
	}
	
	public String getUrl () {
		return "rmi://" + ip + ":" + puerto + "/" + nombre_servicio + "/" + identificador;
	}
}
