package com.ianmiguel;


import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;


public class AppMain {
    private static final Scanner scanString = new Scanner(System.in);
    private static final Scanner scanNum = new Scanner(System.in);

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        int opcion = 1;

        while (opcion>0){
            System.out.println("""
                    1-. Listar todos los videojuegos
                    2-. Insertar videojuegos
                    3-. Modificar los videojuegos
                    4-. Eliminar los videojuegos
                    5-. Busqueda Simple
                    6-. Busqueda Compleja
                    7-. Agregación
                    8-. Proyección
                    Escoge una opción""");
            opcion = scanNum.nextInt();

            switch (opcion){
                case 1 -> listarTodo();
                case 2 -> inserts();
                case 3 -> modificar();
                case 4 -> eliminar();
                case 5 -> busquedaSimple();
                case 6 -> busquedaCompleja();
                case 7 -> agragacion();
                case 8 -> projection();
            }
        }
    }

    private static void listarTodo() {
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");

            FindIterable<Document> fil = mco.find();
            MongoCursor<Document> cursor = fil.cursor();
            while (cursor.hasNext())
                cursor.next();

        }catch (MongoException e){
            e.printStackTrace();
        }
    }

    private static void inserts(){
        System.out.println("Cuantos inserts desea hacer: ");
        int opc = scanNum.nextInt();

        if (opc == 1)
            insert();

        if (opc >1){
            for (int i = 1;opc >= i;i++){
                insert();
            }
        }
        else
            System.out.println("Tiene que ser mayor o igual a uno");
    }
    private static void insert() {
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");

            System.out.println("_Id:");
            int id = scanNum.nextInt();

            System.out.println("Nombre");
            String nombre = scanString.nextLine();

            //Ahora hare un bucle para que escoja tantas categorias como quiera el Usuario
            System.out.println("Categorias: (dejelo vacio cuando no quiera añadir más)");
            List<String> categorias = new ArrayList<>();
            boolean salir = false;
            for(int i=1;!salir;i++)
            {
                System.out.print("\tCategoría "+i+":");
                String categoria = scanString.nextLine();
                if(!categoria.isEmpty())
                    categorias.add(categoria);
                else
                    salir=true;
            }
            System.out.println("Precio");
            double precio = scanNum.nextDouble();

            mco.insertOne(new Document("_id",id)
                    .append("nombre",nombre)
                    .append("categorias",categorias)
                    .append("precio",precio));


        }catch (MongoException e){
            e.printStackTrace();
        }
    }

    private static void modificar() {
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");

            System.out.println("Qué Id es el deseado para modificar: ");
            int id = scanNum.nextInt();
            System.out.println("""
                    Tenga en cuenta que los campos son:
                    Nombre      Categoria       Precio""");

            String opciones = scanString.nextLine();
            if (opciones.equals("Nombre")){
                System.out.println("Qué nombre desea poner a ");
                String nom = scanString.nextLine();
                mco.updateOne(new Document("_id",id), Updates.set("nombre",nom));
            }

            if (opciones.equals("Categoría")){
                System.out.println("Qué categorias desea agragar: ");
                List<String> categorias = new ArrayList<>();
                boolean salir = false;
                for(int i=1;!salir;i++)
                {
                    System.out.print("\tCategoría "+i+":");
                    String categoria = scanString.nextLine();
                    if(!categoria.isEmpty())
                        categorias.add(categoria);
                    else
                        salir=true;
                }

                mco.updateOne(new Document("_id",id),Updates.set("categoria",categorias));
            }

            if (opciones.equals("Precio")){
                System.out.println("Qué precio desea poner: ");
                String nom = scanString.nextLine();
                mco.updateOne(new Document("_id",id), Updates.set("precio",nom));
            }

            else
                System.out.println("Escoja una de las opciones mencionadas anterior mente");
        }catch (MongoException e){
            e.printStackTrace();
        }
    }

    private static void eliminar() {
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");

            System.out.println("Escriba el Id a eliminar:");
            int id = scanNum.nextInt();
            if(id>=0){
                mco.deleteOne(new Document("_Id",id));
            }
            else
                System.out.println("El Id tiene que ser mayor o igual a 0");

        }catch (MongoException e){
            e.printStackTrace();
        }
    }

    private static void busquedaSimple() {
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");

            System.out.println("""
                    Qué desea buscar
                    Id      Precio""");

            String opc = scanString.nextLine();
            if (opc.equals("Id")){
                System.out.println("Qué Id desea buscar: ");
                int id = scanNum.nextInt();
                for (Document document : mco.find(eq("_id", id))) {
                    System.out.println(document.toJson());
                }
            }
            if (opc.equals("Precio")){
                System.out.println("Qué precio desea buscar: ");
                double prec = scanNum.nextInt();
                for (Document document : mco.find(eq("precio", prec))) {
                    System.out.println(document.toJson());
                }
            }

            else
                System.out.println("Introduzca Nombre o Id");
        }catch (MongoException e){
            e.printStackTrace();
        }
    }

    private static void busquedaCompleja() {
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");

            System.out.println("""
                    La busqueda se realizara con dos campos que quiera buscar
                    Nombre igual al que escriba
                    Precio mayor a 4(añadido por defecto en la busqueda)
                    Dos categorias a elegir""");

            System.out.println("Escribe el nombre a buscar: ");
            String nombre = scanString.nextLine();

            System.out.println("Escriba la primera categoria");
            String categoria1 = scanString.nextLine();
            System.out.println("Escriba la segunda categoria");
            String categoria2 = scanString.nextLine();

            List<Document> documents = mco.find(Filters.and(
                    Filters.eq("nombre", nombre),
                    Filters.gt("precio", 4),
                    Filters.in("categoría", Arrays.asList(categoria1, categoria2))
            )).into(new ArrayList<>());

            // Imprimir los documentos encontrados
            for (Document document : documents) {
                System.out.println(document);
            }

        }catch (MongoException e){
            e.printStackTrace();
        }
    }
    private static void agragacion(){
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");
        // Realizar una agregación
        List<Document> result = mco.aggregate(Arrays.asList(
                Aggregates.group("nombre", Accumulators.avg("promedioPrecio", "$precio")),
                Aggregates.sort(new Document("promedioPrecio", 1))
        )).into(new ArrayList<>());

        //Explicación de la agragación
            /*La consulta de agregación utiliza la clase "Aggregates"
            para crear una secuencia de operaciones de agregación.
            En este caso, la consulta primero agrupa los documentos por el campo "nombre",
            y luego calcula el promedio del campo "precio" para cada grupo. Finalmente,
            los resultados son ordenados en orden ascendente por el campo "promedioPrecio".*/

        // Imprimir los resultados
        for (Document document : result) {
            System.out.println(document);
        }
        }catch (MongoException e){
            e.printStackTrace();
        }
    }

    private static void  projection(){
        try(MongoClient mc = new MongoClient("localhost",27017)) {
            MongoDatabase md = mc.getDatabase("ian");
            MongoCollection<Document> mco = md.getCollection("videojuegos");

            // Realizar una proyección
            Document result = mco.find().projection(Projections.fields(
                    Projections.include("nombre"),
                    Projections.exclude("_id","categoria")
            )).first();

            //Explicación proyección
            /*
            * En esta proyección se limitara a mostrarnos el nombre e excluir el id y las categorias*/

            // Imprimir los resultados
            System.out.println(result);

        }catch (MongoException e){
            e.printStackTrace();
        }
    }
}