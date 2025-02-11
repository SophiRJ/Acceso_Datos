package ConexionBiblioteca;
import java.sql.*;
import java.util.Scanner;

public class InsercionLibro {

    public static final String URL = "jdbc:mysql://localhost:3306/biblioteca";

    public static void main(String[] args) {
        String user = "user_biblioteca";
        String password = "1234";
        try(Connection connection= DriverManager.getConnection(URL,user,password);
            Scanner scn= new Scanner(System.in);
            Statement statement= connection.createStatement()){
            System.out.println("Conexion exitosa");
            //visualizar(statement);
            //Insertar
            //hablamos con el uuario y pedimos datos
            //Debemos gestionar
            String isbn;
            String titulo;
            int numeroEjemplares;
            String autor;
            String editorial;
            String tema;

            int idEditorial;
            int idTema;
            int idAutor;

            System.out.println("Registro de libros.\nIngrese los datos:");
            System.out.println("ISBN: ");
            isbn=scn.nextLine();



            //si existe o no el isbn, si no existe lo creamos y nos quedamos con id y si existe nos quedamos con su id
            //Comprobación de datos: Libro
            String selectLibro = "SELECT * FROM libro WHERE isbn='"+isbn+"'";
            ResultSet sentenciaSelect = statement.executeQuery(selectLibro);
            if(sentenciaSelect.next()){
                System.out.println("El libro ya existe");
                //visualizar(statement);
            }else {
                //Damos de alta
                //empezamos con el autor
                System.out.println("Titulo: ");
                titulo=scn.nextLine();
                System.out.println("Numero Ejemplares: ");
                numeroEjemplares=scn.nextInt();
                scn.nextLine();
                System.out.println("Autor: ");
                autor=scn.nextLine();
                System.out.println("Editorial: ");
                editorial=scn.nextLine();
                System.out.println("Tema: ");
                tema=scn.nextLine();
                if(!existeAutor(autor,connection)){
                    altaAutor(autor,connection);
                }
                idAutor=buscarIdAutor(autor, connection);

                //editorial
                if(!existeEditorial(editorial,connection)){
                    System.out.println("El editorial no esta registrado");
                    System.out.println("Direccion editorial: ");
                    String direccionEditorial=scn.nextLine();
                    System.out.println("Telefono Editorial: ");
                    String telefonoEditorial=scn.next();
                    altaEditorial(editorial,direccionEditorial,telefonoEditorial,connection);
                }
                idEditorial=buscarIdEditorial(editorial,connection);


                //tema
                if(!existeTema(tema,connection)){
                    altaTema(tema,connection);
                }
                idTema=buscarIdTema(tema,connection);
                insercionLibro(connection,isbn,titulo,numeroEjemplares,idAutor,idEditorial,idTema);

            }
            /*insercion falsa en el mejor de los casos
            String isbn="fal_789456123";
            String titulo="Victoria_falsa";
            int numeroEjemplares=50;
            int autor=2;
            int editorial=4;
            int tema=4;
            String sentenciaInsert= "INSERT INTO libro "
                    +"VALUES(null,'" + isbn + "','" + titulo + "',"+numeroEjemplares+","+autor+","+editorial+","+tema+")";
            statement.executeUpdate(sentenciaInsert);
            System.out.println("guay");*/
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void visualizar(Statement statement) throws SQLException {
        //String selectT="SELECT * FROM `libro` NATURAL JOIN autor,editorial,tema;";
        String selectT =
                "SELECT libro.ISBN, libro.Titulo, libro.NumeroEjemplares, " +
                        "autor.nombreAutor, editorial.NombreEditorial, tema.NombreTema " +
                        "FROM libro " +
                        "INNER JOIN autor ON libro.idAutor = autor.idAutor " +
                        "INNER JOIN editorial ON libro.idEditorial = editorial.idEditorial " +
                        "INNER JOIN tema ON libro.idTema = tema.idTema;";
        ResultSet cursor= statement.executeQuery(selectT);
        while (cursor.next()) {
            String isbn = cursor.getString("ISBN");
            String titulo = cursor.getString("Titulo");
            int numeroEjemplares = cursor.getInt("NumeroEjemplares");
            String nombreAutor = cursor.getString("nombreAutor");
            String nombreEditorial = cursor.getString("NombreEditorial");
            String nombreTema = cursor.getString("NombreTema");

            System.out.printf("ISBN: %s, Título: %s, Ejemplares: %d, Autor: %s, Editorial: %s, Tema: %s%n",
                    isbn, titulo, numeroEjemplares, nombreAutor, nombreEditorial, nombreTema);
        }
    }
    private static boolean existeAutor(String autor, Connection con) throws SQLException {
        String select="SELECT * FROM autor WHERE nombreAutor= ?";
        try(PreparedStatement statement= con.prepareStatement(select)){
            statement.setString(1,autor);
            try(ResultSet rs=statement.executeQuery()){
                return rs.next();
            }
        }
    }
    private static Integer buscarIdAutor(String autor, Connection con) throws SQLException {
        String selectId = "SELECT idAutor FROM autor WHERE nombreAutor = ?";
        int idAutor;
        try (PreparedStatement ps = con.prepareStatement(selectId)) {
            ps.setString(1, autor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idAutor= rs.getInt("idAutor"); // Retorna el ID si se encuentra el autor.
                } else {
                    idAutor= -1;
                }
            }
        }
        return idAutor;
    }
    private static void altaAutor(String autor, Connection con) {
        String dropProcedure = "DROP PROCEDURE IF EXISTS altaAutor;";
        String createProcedure =
                "CREATE PROCEDURE altaAutor(IN p_nombre VARCHAR(50)) " +
                        "BEGIN " +
                        "    INSERT INTO autor(nombreAutor) VALUES (p_nombre); " +
                        "END;";
        try (Statement st = con.createStatement()) {
            // Primero eliminamos el procedimiento si existe
            st.execute(dropProcedure);
            // Luego creamos el nuevo procedimiento
            st.execute(createProcedure);

            String callProcedure="CALL altaAutor(?)";
            try(CallableStatement cst=con.prepareCall(callProcedure)){
                cst.setString(1,autor);
                cst.execute();
                System.out.println("Autor dado de alta");
            }
        } catch (SQLException e) {
            System.out.println("Error altaAutor");
            throw new RuntimeException(e);
        }
    }
    private static boolean existeEditorial(String editorial, Connection con)  {
        String select="SELECT * FROM editorial WHERE NombreEditorial = ?";
        try(PreparedStatement ps= con.prepareStatement(select)){
            ps.setString(1,editorial);
            try(ResultSet rs=ps.executeQuery()){
                return  rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error existeEditorial");
            throw new RuntimeException(e);
        }
    }
    private static void altaEditorial(String editorial,String direccion, String telefono, Connection con) {
        // Declaración para eliminar el procedimiento si ya existe
        String dropProcedure = "DROP PROCEDURE IF EXISTS altaEditorial;";
        // Declaración para crear el nuevo procedimiento
        String createProcedure =
                "CREATE PROCEDURE altaEditorial(" +
                        "    IN p_nombre VARCHAR(30), " +
                        "    IN p_direccion VARCHAR(100), " +
                        "    IN p_telefono VARCHAR(15)) " +
                        "BEGIN " +
                        "    INSERT INTO editorial(NombreEditorial, Direccion, Telefono) " +
                        "    VALUES (p_nombre, p_direccion, p_telefono); " +
                        "END;";
        try (Statement st = con.createStatement()) {
            // Primero eliminamos el procedimiento si existe
            st.execute(dropProcedure);
            // Luego creamos el procedimiento
            st.execute(createProcedure);
            String callProcedure="CALL altaEditorial(?,?,?)";
            try(CallableStatement cst=con.prepareCall(callProcedure)){
                cst.setString(1,editorial);
                cst.setString(2,direccion);
                cst.setString(3,telefono);
                cst.execute();
            }
        } catch (SQLException e) {
            System.out.println("Error altaEditorial");
            throw new RuntimeException(e);
        }
    }
    private static int buscarIdEditorial(String editorial, Connection con){
        String select="SELECT idEditorial FROM editorial WHERE NombreEditorial= ?";
        int idEditorial;
        try(PreparedStatement ps =con.prepareStatement(select)){
            ps.setString(1,editorial);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    idEditorial=rs.getInt("idEditorial");
                }else {
                    idEditorial=-1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return idEditorial;
    }
    private static boolean existeTema(String tema, Connection con){
        String select="SELECT * FROM tema WHERE NombreTema = ?";
        try(PreparedStatement ps=con.prepareStatement(select)){
            ps.setString(1,tema);
            try(ResultSet rs= ps.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error existeTema");
            throw new RuntimeException(e);
        }
    }
    private static void altaTema(String tema, Connection con) {
        // Declaración para eliminar el procedimiento si ya existe
        String dropProcedure = "DROP PROCEDURE IF EXISTS altaTema;";
        // Declaración para crear el nuevo procedimiento
        String createProcedure =
                "CREATE PROCEDURE altaTema(IN p_nombreTema VARCHAR(30)) " +
                        "BEGIN " +
                        "    INSERT INTO tema (NombreTema) VALUES (p_nombreTema); " +
                        "END;";
        try (Statement st = con.createStatement()) {
            // Primero eliminamos el procedimiento si existe
            st.execute(dropProcedure);
            // Luego creamos el procedimiento
            st.execute(createProcedure);
            String callProcedure="CALL altaTema(?)";
            try(CallableStatement cst=con.prepareCall(callProcedure)){
                cst.setString(1,tema);
                cst.execute();
            }
        } catch (SQLException e) {
            System.out.println("Error altaTema");
            throw new RuntimeException(e);
        }
    }
    private static int buscarIdTema(String tema, Connection con){
        String select="SELECT idTema FROM tema WHERE NombreTema= ?";
        int idTema;
        try(PreparedStatement ps=con.prepareStatement(select)){
            ps.setString(1,tema);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    idTema=rs.getInt("idTema");
                }else{
                    idTema=-1;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error buscarIdTema");
            throw new RuntimeException(e);
        }
        return idTema;
    }
    private static void insercionLibro(Connection con,String isbn,String tituloLibro, int numeroEjemplares,int idAutor,
                                       int idEditorial,int idTema) {
        // Declaración para eliminar el procedimiento si ya existe
        String dropProcedure = "DROP PROCEDURE IF EXISTS altaLibro;";
        // Declaración para crear el nuevo procedimiento
        String createProcedure =
                "CREATE PROCEDURE altaLibro(" +
                        "   IN p_isbn VARCHAR(20), " +
                        "   IN p_titulo VARCHAR(65), " +
                        "   IN p_numeroEjemplares TINYINT(4), " +
                        "   IN p_idAutor INT(11), " +
                        "   IN p_idEditorial INT(11), " +
                        "   IN p_idTema INT(11)) " +
                        "BEGIN " +
                        "   INSERT INTO libro (ISBN, Titulo, NumeroEjemplares, idAutor, idEditorial, idTema) " +
                        "   VALUES (p_isbn, p_titulo, p_numeroEjemplares, p_idAutor, p_idEditorial, p_idTema); " +
                        "END;";
        try (Statement st = con.createStatement()) {
            // Primero eliminamos el procedimiento si existe
            st.execute(dropProcedure);
            // Luego creamos el procedimiento
            st.execute(createProcedure);

            String callProcedure="CALL altaLibro(?,?,?,?,?,?)";
            try(CallableStatement cst=con.prepareCall(callProcedure)){
                cst.setString(1,isbn);
                cst.setString(2,tituloLibro);
                cst.setInt(3,numeroEjemplares);
                cst.setInt(4,idAutor);
                cst.setInt(5,idEditorial);
                cst.setInt(6,idTema);
                cst.execute();
                System.out.println("Libro insertado correctamente");
            }
        } catch (SQLException e) {
            System.out.println("Error altaLibro");
            throw new RuntimeException(e);
        }

    }
}
