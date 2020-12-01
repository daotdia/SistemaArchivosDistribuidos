package Systema;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
	public static void main (String [] args) throws Exception {
		boolean salir = false;
		boolean servidor_iniciado = true;
		String directorio = System.getProperty("user.dir");
		
		int opcion = -1;
		Scanner in = new Scanner(System.in);
		
		while(!salir) {
			System.out.println("Indique los servicios que quiere ejecutar");
			System.out.println("1. Iniciar el servicio servidor y sus servicios");
			System.out.println("2. Iniciar el servicio repositorio y sus servicios");
			System.out.println("3. Iniciar el servicio usuario y sus servicios");
			System.out.println("4. Salir");
			System.out.println("");
	
			opcion = in.nextInt();
			in.nextLine();
			
			switch(opcion) {
				case 1:
					ProcessBuilder pb = new ProcessBuilder(directorio + "/Servidor.sh");
					Process p = pb.start();
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				    String line = null;
					while ((line = reader.readLine()) != null)
					{
					   System.out.println(line);
					}
					break;
				case 2:
					//AutenticacionRepositorio.main(null);
					break;
				case 3:
					//AutenticacionCliente.main(null);
					break;
				case 4:
					//AutenticacionCliente.main(null);
					break;
				default:
					System.out.println("Argumento no elegido correctamente. Las opciones posibles son:"
							+ "-servidor, -repositorio, -cliente");
			}
		}
	}
}
