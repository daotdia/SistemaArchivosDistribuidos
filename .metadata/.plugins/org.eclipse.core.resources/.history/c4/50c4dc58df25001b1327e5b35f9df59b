package es.uned.sisdist.servidor;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioAutenticacionInterface;
import es.uned.sisdist.common.ServicioClOperadorInterface;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioDiscoClienteInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;
import es.uned.sisdist.common.ServidorInterface;
import es.uned.sisdist.common.SourcePath;

public class Servidor implements ServidorInterface {
	
	private static ServicioDatosInterface bd;
	private static ServicioAutenticacionInterface au;
	private static List<Repositorio> repositorios;
	private static ServicioSrOperadorInterface sso;
	private static ServicioClOperadorInterface sco;
	
	private static final int NUMERO_REPOSITORIOS = 100;
	
	public Servidor(ServicioDatosInterface bd, ServicioAutenticacionInterface au,
			ServicioSrOperadorInterface sso, ServicioClOperadorInterface sco) throws RemoteException {
		this.bd = bd;
		this.au = au;
		this.sso = sso;
		this.sco = sco;
	}
	
	public static void main (String[] Args) throws Exception{
		SourcePath.setCodebase(ServicioAutenticacionInterface.class);
		
		Registry registry = LocateRegistry.createRegistry(7777);
		
		
		ServicioDatosInterface datos = new ServicioDatosImpl();
		Remote datos_remotos = UnicastRemoteObject.exportObject(datos,8888);
		registry.rebind("datos_remotos", datos_remotos);
		
		ServicioSrOperadorInterface srOp = new ServiciosSrOperadorImpl();
		Remote srOp_remoto = UnicastRemoteObject.exportObject(srOp, 5555);
		registry.rebind("sso_remoto", srOp_remoto);
		
		ServicioClOperadorInterface srCp = new ServicioClOperadorImpl();
		Remote srCp_remoto = UnicastRemoteObject.exportObject(srCp, 2222);
		registry.rebind("srCp_remoto", srCp_remoto);
		
		ServicioDatosInterface datos_rmi = (ServicioDatosInterface) registry.lookup("datos_remotos");
		ServicioAutenticacionInterface autenticacion = new ServicioAutenticacionImpl();
		Remote autenticacion_remota = UnicastRemoteObject.exportObject(autenticacion, 6666);
		registry.rebind("autenticacion_remota", autenticacion_remota);
		
		ServicioSrOperadorInterface sso_rmi = (ServicioSrOperadorInterface) registry.lookup("sso_remoto");
		ServicioAutenticacionInterface autenticacion_rmi = (ServicioAutenticacionInterface) registry.lookup("autenticacion_remota");
		ServicioClOperadorInterface scp_rmi = (ServicioClOperadorInterface) registry.lookup("srCp_remoto");
		ServidorInterface servidor = new Servidor(datos_rmi,autenticacion_rmi,sso_rmi,scp_rmi);
		Remote servidor_remoto = UnicastRemoteObject.exportObject(servidor,9999);
		registry.rebind("servidor_remoto",servidor_remoto);
		
		System.out.println("Servidor remoto listo, con los servicios remotos listos");
		System.in.read();
		
		registry.unbind("datos_remotos");
		UnicastRemoteObject.unexportObject(datos, true);
		registry.unbind("autenticacion_remota");
		UnicastRemoteObject.unexportObject(autenticacion, true);
		registry.unbind("sso_remoto");
		UnicastRemoteObject.unexportObject(srOp, true);
		registry.unbind("servidor_remoto");
		UnicastRemoteObject.unexportObject(servidor, true);
	}
	
	public void menu_inicial (String nombre, int tipo, int opcion) 
			throws RemoteException {
		int id_cliente;
		int id_repositorio;
		
		switch(opcion) {
		case 0:
			au.registrarObjeto(nombre, tipo);
			break;
		case 1:
			id_cliente = au.iniciarSesion(nombre, tipo);
			//crearCarpeta(id_cliente, id_repositorio);
			break;
		case 2:
			id_cliente = au.cerrarSesion(nombre, tipo);
			//deleteCarpeta(id_cliente, id_repositorio);
			break;
		case 3:
			au.deleteObjeto(nombre, tipo);
			break;
		}
	}
	
	public void gestion_archivos (String nombre_cliente, String nombre_fichero, String path, int opcion) 
		throws RemoteException, IOException{
		switch(opcion) {
			case 0:
				sco.subirArchivo(path, nombre_fichero, nombre_cliente);
				break;
		}
	}
	
	public boolean comprobarCliente (String nombre) throws RemoteException{
		return bd.getListaClientesActivos().containsKey(nombre);
	}
}
