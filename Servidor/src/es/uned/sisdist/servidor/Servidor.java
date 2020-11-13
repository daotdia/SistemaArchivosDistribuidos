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
import es.uned.sisdist.common.ServicioGestorInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;
import es.uned.sisdist.common.SourcePath;

public class Servidor{
	
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
		registry.rebind("sco_remoto", srCp_remoto);
		
		ServicioGestorInterface sg = new ServicioGestorImpl();
		Remote sg_remoto = UnicastRemoteObject.exportObject(sg, 2323);
		registry.rebind("sg_remoto", sg_remoto);
		
		ServicioDatosInterface datos_rmi = (ServicioDatosInterface) registry.lookup("datos_remotos");
		ServicioAutenticacionInterface autenticacion = new ServicioAutenticacionImpl();
		Remote autenticacion_remota = UnicastRemoteObject.exportObject(autenticacion, 6666);
		registry.rebind("autenticacion_remota", autenticacion_remota);
		
		ServicioSrOperadorInterface sso_rmi = (ServicioSrOperadorInterface) registry.lookup("sso_remoto");
		ServicioAutenticacionInterface autenticacion_rmi = (ServicioAutenticacionInterface) registry.lookup("autenticacion_remota");
		ServicioClOperadorInterface scp_rmi = (ServicioClOperadorInterface) registry.lookup("srCp_remoto");
		
		
		System.out.println("Servidor remoto listo, con los servicios remotos listos");
		System.in.read();
		
		registry.unbind("datos_remotos");
		UnicastRemoteObject.unexportObject(datos, true);
		registry.unbind("autenticacion_remota");
		UnicastRemoteObject.unexportObject(autenticacion, true);
		registry.unbind("sso_remoto");
		UnicastRemoteObject.unexportObject(srOp, true);
	}
}
