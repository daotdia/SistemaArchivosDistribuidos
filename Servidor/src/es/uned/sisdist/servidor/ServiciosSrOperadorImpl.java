package es.uned.sisdist.servidor;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import es.uned.sisdist.common.Repositorio;
import es.uned.sisdist.common.ServicioDatosInterface;
import es.uned.sisdist.common.ServicioSrOperadorInterface;
import es.uned.sisdist.common.URL;

public class ServiciosSrOperadorImpl implements ServicioSrOperadorInterface {

	private ServicioDatosInterface bd;
	
	public ServiciosSrOperadorImpl() throws Exception{
		Registry registry = LocateRegistry.getRegistry(7777);
		this.bd = (ServicioDatosInterface) registry.lookup("datos_remotos");
	}
	
	@Override
	public void crearCarpeta(Repositorio repo, String nombre) throws RemoteException {
		//Obtengo el repositorio y creo la carpeta del usuario según el path del mismo.
		File directorio=new File(repo.getPath() + "/" + nombre);
		directorio.mkdir();
		System.out.println("Carpeta creada en path " + directorio);
	}

	@Override
	public void borrarCarpeta(Repositorio repo, String nombre_cliente) throws RemoteException {
		File directorio=new File(repo.getPath() + "/" + nombre_cliente);
		directorio.delete();
		System.out.println("Carpeta eleminada en path " + directorio);
	}

	@Override
	public String bajarArchivo(URL URL) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
