

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Titulo titulo = new Titulo("Heroes");
        String heroe;
        String nombreH;
        String link;
        String img;
        int tamanio;


        JSONObject esquema = new JSONObject();
        esquema.put("hero", titulo.getTitulo());
        JSONArray arrayJs = new JSONArray();


        Scanner scn = new Scanner(System.in);
        System.out.println("Heroe (o escribe * para salir): ");
        heroe = scn.nextLine();

        while (!heroe.equals("*")) {
            System.out.println("Nombre: ");
            nombreH = scn.nextLine();
            System.out.println("Link: ");
            link = scn.nextLine();
            System.out.println("URL de la Imagen: ");
            img = scn.nextLine();
            System.out.println("Tamaño: ");
            tamanio = scn.nextInt();
            scn.nextLine(); // Consumir el salto de línea residual

            // Crear y agregar héroe al array JSON
            Heroe heroe1 = new Heroe(heroe, nombreH, link, img, tamanio);
            titulo.add(heroe1);

            JSONObject heroeJs = new JSONObject();
            heroeJs.put("hero", heroe1.getHeroe());
            heroeJs.put("name", heroe1.getNombre());
            heroeJs.put("link", heroe1.getLink());
            heroeJs.put("img", heroe1.getImg());
            heroeJs.put("size", heroe1.getTamanio());

            arrayJs.put(heroeJs);

            System.out.println("Heroe (o escribe * para salir): ");
            heroe = scn.nextLine();

        }
        // Agregamos el array de heroes al objeto json principal
        esquema.put("heroes", arrayJs);

        // Escribir el archivo JSON
        Files.write(Paths.get("heroes.json"), esquema.toString(4)
                .getBytes());
        System.out.println("Los datos se han guardado en heroes.json");
    }
}

