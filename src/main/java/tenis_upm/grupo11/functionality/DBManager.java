package tenis_upm.grupo11.functionality;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.util.Pair;
import tenis_upm.grupo11.data.Match;
import tenis_upm.grupo11.data.PlayerTournamentStats;
import tenis_upm.grupo11.data.Tournament;
import tenis_upm.grupo11.data.User;
import tenis_upm.grupo11.view.insidepanels.*;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/tenis";
    private static final String USER = "tenista";
    private static final String PASSWORD = "tenista";
    private User actualUser;
    public volatile boolean verified = false;
    private Connection connection;

    public DBManager() {
        try {
            // CONECTAMOS DATABASE
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /// //////////////// ACCIONES USUARIO ///////////////////

    // AÑADIR USUARIO
    public int createUser(String username, String name, String phone, String email, String password, Boolean admin) {
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(regex)) {
            return 1;
        }
        // Consulta para verificar si el nombre de usuario ya existe
        String checkUsernameQuery = "SELECT COUNT(*) FROM Users WHERE username = ?";

        // Si el nombre de usuario ya existe, retornar un mensaje indicando el error
        try (PreparedStatement ps = connection.prepareStatement(checkUsernameQuery)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Si ya existe el usuario, retornamos un mensaje
                    System.out.println("Error: El nombre de usuario ya está en uso.");
                    return 2;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 3; // Error al consultar la base de datos
        }
        String token = UUID.randomUUID().toString();
        //actualUser.setVerificationToken(token);
        //users.put(username, actualUser);
        //Sentencias sql
        String htmlContent = "<html><body>"
                + "<p>Hola " + name + ",</p>"
                + "<p>Gracias por registrarte en nuestro sistema. Haz clic en el siguiente enlace para activar tu cuenta:</p>"
                + "<p><a href='http://localhost:8080/mail/verify?token=" + token + "'>Activar mi cuenta</a></p>"
                + "</body></html>";

        // Si el nombre de usuario no existe, podemos proceder a crear el usuario
        String insertUserQuery = "INSERT INTO Users (username, name, phone, email, password, isAdmin) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insertUserQuery)) {
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, password);  // En una implementación real, deberías encriptar la contraseña
            ps.setBoolean(6, admin);  // Si el usuario es administrador, lo establecemos como true
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Usuario creado exitosamente.");
                if (!sendVerificationEmail("tenisupm@gmail.com", email, "Tenis UPM: Verificación de la cuenta", htmlContent, token, username)) {
                    return 4;
                }
                return 0;
            } else {
                System.out.println("Error al crear el usuario.");
                return 5;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 6;
        }
    }
    public boolean isTournamentActive(int tournamentID) {
        String query = "SELECT active_status FROM Tournaments WHERE id = ?";
        boolean isActive = false;

        try (Connection connection = getConnection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, tournamentID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isActive = rs.getBoolean("active_status");
                } else {
                    System.out.println("No tournament found with ID: " + tournamentID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isActive;
    }
    public Connection getConnection() {
    	return connection;
    }
    // VERIFICAR USUARIO (pone verificationStatus a true)//HAY QUE VER COMO CON LO DEL CORREO
    public void verifyUser(String username) {
        System.out.println(username);
        String query = "UPDATE Users SET verificationStatus = 1 WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario verificado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean sendVerificationEmail(String from, String to, String subject, String content, String token, String username) {
        final String user = "mazx2369";
        final String password = "iafliiivbpgwhmac";

        // provide Mailtrap's host address
        String host = "smtp.gmail.com";

        // configure Mailtrap's SMTP details
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // create the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        //compose the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");


            // Send message
            Transport.send(message);
            System.out.println("Verification-Email Sent");
            verified = false;
            
            Thread serverThr =  new Thread(() -> runVerificationServer("/mail/verify?token=" + token, username));
            serverThr.start();
            int secCount = 0;
            while (!verified && secCount <= 120) { // Check verification status periodically
                try {
                	if(verified) {
                		serverThr.interrupt();
                	}
                    Thread.sleep(500);
                    secCount++;
                } catch (InterruptedException e) {

                    e.printStackTrace();
                }
            }
            
            if (secCount > 120) {
                System.out.println("Time limit for answer exceeded");
                return false;
            }
            verified = false;

        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
        return true;
    }

    private void runVerificationServer(String token, String username) {
    	boolean tobeverified = false;
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080. Waiting for token...");

            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

                // Receive token from the client
                String receivedToken = in.readLine();
                System.out.println("Server received token: " + receivedToken.split(" ")[1]);
                String responseMessage;
                String background;
                String HTTPStatus;

                // Verify the token
                if (token != null && token.equals(receivedToken.split(" ")[1])) {
                    responseMessage = "Email verificado correctamente.";
                    background = "#0000ff";
                    HTTPStatus = "HTTP/1.1 200 OK\r\n";
                    verifyUser(username);
                    tobeverified = true;
                    
                    System.out.println("Token verified successfully.\n");
                } else {
                    responseMessage = "El email no ha sido verificado. El token introducido es incorrecto.";
                    background = "#ff0000";
                    HTTPStatus = "HTTP/1.1 406 Not Acceptable\r\n";
                    System.out.println(token + " Invalid token.\n");
                }

                // Send a valid HTTP response to the client
                // HTML content with basic styling

                String htmlResponse = "<html>\n"
                        + "<head>\n"
                        + "<title>Tenis UPM</title>\n"
                        + "<style>\n"
                        + "body { font-family: Arial, sans-serif; background-color: " + background + "; color: " + "#fff" + "; text-align: center; padding: 50px; }\n"
                        + "h1 { font-size: 50px; }\n"
                        + "p { font-size: 20px; }\n"
                        + "</style>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "<h1>" + "Tenis UPM" + "</h1>\n"
                        + "<p>" + responseMessage + "</p>\n"
                        + "</body>\n"
                        + "</html>";

                out.write(HTTPStatus); // HTTP status line
                out.write("Content-Type: text/html\r\n"); // Content type header
                out.write("Content-Length: " + htmlResponse.length() + "\r\n"); // Content length header
                out.write("\r\n"); // End of headers
                out.write(htmlResponse); // The actual response message

                out.flush();
                verified = tobeverified;
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    //Comprobar el login
    // Comprobar el login y actualizar el actualUser
    public boolean login(String username, String pass) {
        String query = "SELECT name, phone, email, username, password, verificationStatus, isAdmin FROM Users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // El usuario existe, comprobamos la contraseña
                    String correctPass = rs.getString("password");
                    if (pass.equals(correctPass)) {
                        // Crear un objeto User con la información del usuario
                        String name = rs.getString("name");
                        String phone = rs.getString("phone");
                        String email = rs.getString("email");
                        String userUsername = rs.getString("username");
                        boolean verificationStatus = rs.getBoolean("verificationStatus");
                        boolean isAdmin = rs.getBoolean("isAdmin");

                        // Crear el objeto User y asignarlo a actualUser
                        actualUser = new User(name, phone, email, userUsername, correctPass, verificationStatus, isAdmin);
                        FunctionalitiesPanel.getInstance().refresh();
                        System.out.println("Login exitoso.");
                        return true;
                    } else {
                        System.out.println("Error: Contraseña incorrecta.");
                        return false;
                    }
                } else {
                    // El usuario no existe
                    System.out.println("Error: Usuario no encontrado.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Se produjo un problema en la base de datos.");
            return false;
        }
    }


    //INSCRIBE A UN USUARIO EN UN TORNEO
    public boolean createRegistration(int tournamentId) {
        Date today = new Date(System.currentTimeMillis());
 
        // Paso 1: Verificar si la fecha actual es posterior a la fecha límite del torneo y que no esté inciado
        String queryDeadline = "SELECT deadline, active_status FROM Tournaments WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(queryDeadline)) {
            ps.setInt(1, tournamentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date deadline = rs.getDate("deadline");
                    if (today.after(deadline)) {
                        System.out.println("Error: La fecha actual es posterior a la fecha límite.");
                        return false;
                    }
                    if(rs.getInt("active_status") == 1) {
                    	System.out.println("Error: El torneo ya está iniciado.");
                    	return false;
                    	
                    }
                } else {
                    System.out.println("Error: Torneo no encontrado.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar la fecha límite del torneo.");
            return false;
        }

        // Paso 2: Verificar si el usuario ya está inscrito
        String queryCheckInscription = "SELECT COUNT(*) FROM Registration WHERE tournament_id = ? AND username = ?";
        try (PreparedStatement ps = connection.prepareStatement(queryCheckInscription)) {
            ps.setInt(1, tournamentId);
            ps.setString(2, actualUser.getUsername());  // actualUser es el nombre de usuario del que está logueado
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: El usuario ya está inscrito en este torneo.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar la inscripción del usuario.");
            return false;
        }

        // Paso 3: Insertar al usuario en la tabla Registration.
        String queryInsertRegistration = "INSERT INTO Registration (tournament_id, username, creation_date) VALUES (?, ?, CURRENT_DATE)";
        try (PreparedStatement ps = connection.prepareStatement(queryInsertRegistration)) {
            ps.setInt(1, tournamentId);
            ps.setString(2, actualUser.getUsername());
            //ps.setInt(3, userPoints);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Usuario inscrito con éxito en el torneo.");
                return true;
            } else {
                System.out.println("Error: No se pudo inscribir al usuario en el torneo.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al inscribir al usuario en la base de datos.");
            return false;
        }
    }

    // PASSWORD OLVIDADO, NUEVO SE ENVÍA POR EMAIL
    public boolean forgotPassword(String username) {
        String token = UUID.randomUUID().toString();
        // Verificar si el usuario existe en la base de datos
        String query = "SELECT * FROM Users WHERE username = ?";
        String email = null;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                    System.out.println("Usuario encontrado: " + username); // Mensaje en consola si se encuentra el usuario
                } else {
                    System.out.println("No se encontró el usuario.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar el usuario.");
            return false;
        }

        // Generar una nueva contraseña aleatoria
        String newPassword = generateRandomPassword(12); // Longitud de la contraseña aleatoria
        System.out.println("Nueva contraseña generada: " + newPassword); // Mensaje en consola con la nueva contraseña
        // Crear el contenido del correo
        String htmlContent = "<html><body>"
                + "<p>Hola " + username + ",</p>"
                + "<p>Hemos recibido una solicitud para restablecer tu contraseña.</p>"
                + "<p>Tu nueva contraseña es: <strong>" + newPassword + "</strong></p>"
                + "<p>Por favor, cámbiala tan pronto como sea posible después de iniciar sesión.</p>"
                + "</body></html>";
        if (sendForgottenEmail("tenisupm@gmail.com", email,
                "Tenis UPM: Recuperación de Contraseña", htmlContent, token, username)) {
            System.out.println("Correo de recuperación de contraseña enviado con éxito.");
        } else {
            System.out.println("Error al enviar el correo de recuperación de contraseña.");
        }
        // Actualizar la contraseña en la base de datos
        String updatePasswordQuery = "UPDATE Users SET password = ? WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(updatePasswordQuery)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("La contraseña ha sido cambiada exitosamente.");
                return true; // También devuelve la nueva contraseña al usuario
            } else {
                System.out.println("No se pudo actualizar la contraseña.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cambiar la contraseña.");
            return false;
        }
    }

    public boolean sendForgottenEmail(String from, String to, String subject, String content, String token, String username) {
        final String user = "mazx2369";
        final String password = "iafliiivbpgwhmac";

        // provide Mailtrap's host address
        String host = "smtp.gmail.com";

        // configure Mailtrap's SMTP details
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // create the Session object
        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        //compose the message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            // Send message
            Transport.send(message);
            System.out.println("Forgotten-Email Sent");
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
        return true;
    }

    // Método para generar una contraseña aleatoria de longitud 'length'
    private String generateRandomPassword(int length) {
        // Conjunto de caracteres válidos para la contraseña
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(validChars.length());
            password.append(validChars.charAt(index));
        }

        return password.toString();
    }

    /*// USUARIO ES ADMIN (pone isAdmin a true)
    public void userAdmin(String username) {
        String query = "UPDATE Users SET isAdmin = true WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Permisos de administrador asignados exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */

    // ACTUALIZAR PUNTOS DE USUARIO !!!!!!CICLO 2!!!!!!
/*    public void userPoints(String username, int points) {//
        String query = "UPDATE Users SET totalPoints = ? WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, points);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Puntos de usuario actualizados exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    // CAMBIAR CONTRASEÑA !!!!CICLO 2!!!!!
    public void changePassword(String username, String newPassword) {
        String query = "UPDATE Users SET password = ? WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contraseña cambiada exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /// //////////////// ACCIONES TORNEO ///////////////////

    // CREAR TORNEO
    public boolean createTournament(String name, int year, Date deadline) {
        String checkQuery = "SELECT COUNT(*) FROM Tournaments WHERE name = ? AND year = ?";
        String insertQuery = "INSERT INTO Tournaments (name, year, deadline) VALUES (?, ?, ?)";

        try (PreparedStatement checkPs = connection.prepareStatement(checkQuery)) {
            // Comprobar si el torneo ya existe
            checkPs.setString(1, name);
            checkPs.setInt(2, year);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("El torneo ya existe para el año especificado.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Salir si ocurre un error al comprobar
        }

        // Insertar el nuevo torneo si no existe
        try (PreparedStatement insertPs = connection.prepareStatement(insertQuery)) {
            insertPs.setString(1, name);
            insertPs.setInt(2, year);
            insertPs.setDate(3, deadline);
            int rowsAffected = insertPs.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Torneo creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    // OBTENER ID DEL TORNEO PARA UTILIZARLO EN OPERACIONES SIGUIENTES
    public int getTournamentID(String name, int year) {
        String query = "SELECT id FROM Tournaments WHERE name = ? AND year = ?";
        int tournamentId = -1; // Valor por defecto para identificar errores o no encontrado
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tournamentId = rs.getInt("id");
                    System.out.println("ID de torneo encontrado: " + tournamentId);
                } else {
                    System.out.println("No se encontró un torneo con el nombre '" + name + "' y año " + year);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al buscar el ID del torneo.");
        }
        return tournamentId;
    }

    
    private List<String> getTopPlayers(int tournamentId) {
        String query = "SELECT u.username FROM Users u INNER JOIN Registration r ON u.username = r.username WHERE r.tournament_id = ? ORDER BY u.totalPoints DESC LIMIT 16";
        List<String> players = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, tournamentId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                players.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return players;
    }

    public List<Tournament> showRegisterTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Consulta para obtener torneos con deadline posterior a hoy
            String query = "SELECT id, name, year, deadline, number_of_players, active_status, actual_round " +
                    "FROM Tournaments " +
                    "WHERE deadline > CURRENT_DATE";
            statement = connection.prepareStatement(query);

            // Ejecuta la consulta
            resultSet = statement.executeQuery();

            // Recorre los resultados y los mapea a objetos Tournament
            while (resultSet.next()) {
                Tournament tournament = new Tournament(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("year"),
                        resultSet.getDate("deadline"),
                        resultSet.getBoolean("active_status"),
                        resultSet.getInt("actual_round")
                );
                tournaments.add(tournament);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo básico de excepciones, podrías mejorar esto según tu lógica
        } finally {
            // Cierra los recursos para evitar fugas de memoria
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        RegisterTournamentPanel.getInstance().addTournaments(tournaments);
        return tournaments;
    }


    public List<Tournament> showTournamentsToInitiate() {
        List<Tournament> tournaments = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Consulta para obtener torneos con deadline posterior a hoy
            String query = "SELECT id, name, year, deadline, number_of_players, active_status, actual_round " +
                    "FROM Tournaments " +
                    "WHERE deadline > CURRENT_DATE";
            statement = connection.prepareStatement(query);

            // Ejecuta la consulta
            resultSet = statement.executeQuery();

            // Recorre los resultados y los mapea a objetos Tournament
            while (resultSet.next()) {
                Tournament tournament = new Tournament(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("year"),
                        resultSet.getDate("deadline"),
                        resultSet.getBoolean("active_status"),
                        resultSet.getInt("actual_round")
                );
                tournaments.add(tournament);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Manejo básico de excepciones, podrías mejorar esto según tu lógica
        } finally {
            // Cierra los recursos para evitar fugas de memoria
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ManageTournamentsPanel.getInstance().addTournaments(tournaments);
        return tournaments;
    }

    
    // INICIAR TORNEO
    public void initiateTournament(int idTournament) {
        // Obtener el torneo desde la base de datos
        String queryTournament = "SELECT * FROM Tournaments WHERE id = ?";
        Tournament actualTournament = null;
        try (PreparedStatement ps = connection.prepareStatement(queryTournament)) {
            ps.setInt(1, idTournament);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    actualTournament = new Tournament(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("year"),
                            rs.getDate("deadline"),
                            rs.getBoolean("active_status"),
                            rs.getInt("actual_round")
                    );
                    if(actualTournament.isActive()) {
                    	System.out.println("El torneo ya está inciado");
                    	return;
                    	
                    }
                } else {
                    System.out.println("Torneo no encontrado.");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Verificar si el torneo tiene inscripciones
        String queryCheckInscriptions = "SELECT COUNT(*) FROM Registration WHERE tournament_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(queryCheckInscriptions)) {
            ps.setInt(1, idTournament);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("No hay inscripciones en el torneo con ID " + idTournament + ".");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }


        // Seleccionar e insertar 16 jugadores (o menos) en el torneo

        List<String> players = getTopPlayers(idTournament);

        String query = "INSERT INTO TournamentPlayers (tournament_id, username) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (String username : players) {
                statement.setInt(1, idTournament);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // Actualizar el torneo con los jugadores seleccionados y poniendo estado a activo
        String updateTournamentPlayers = "UPDATE Tournaments SET number_of_players = ?, active_status = TRUE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(updateTournamentPlayers)) {
            ps.setInt(1, players.size());
            ps.setInt(2, idTournament);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }


        // Ajustar el actual_round del torneo según el número de jugadores
        int numberOfPlayers = players.size();
        if (numberOfPlayers < 9) {
            String updateActualRound = "UPDATE Tournaments SET actual_round = ? WHERE id = ?";
            int newRound = 16; // Valor por defecto si hay 16 jugadores

            if (numberOfPlayers <= 8 && numberOfPlayers > 4) {
                newRound = 8;
            } else if (numberOfPlayers <= 4 && numberOfPlayers > 2) {
                newRound = 4;
            } else if (numberOfPlayers == 2) {
                newRound = 2;
            } else if (numberOfPlayers < 2) {
            	System.out.println("Error: No hay suficientes jugadores para iniciar el torneo.");
                return;
            }

            try (PreparedStatement ps = connection.prepareStatement(updateActualRound)) {
                ps.setInt(1, newRound);
                ps.setInt(2, idTournament);
                ps.executeUpdate();
                System.out.println("Torneo actualizado a la ronda " + newRound + " con " + numberOfPlayers + " jugadores.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al actualizar la ronda del torneo.");
            }


            // Actualizar el round de TournamentPlayers según la ronda del torneo
            String updateTournamentPlayersRound = "UPDATE TournamentPlayers SET round = ? WHERE tournament_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateTournamentPlayersRound)) {
                ps.setInt(1, newRound);
                ps.setInt(2, idTournament);
                ps.executeUpdate();
                System.out.println("Rounds de los jugadores actualizados a " + newRound + ".");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al actualizar el round de los jugadores.");
            }

        }


        System.out.println("Torneo iniciado con éxito.");
    }


    // ACTUALIZAR RONDA DEL TORNEO//CICLO 2
 /*   public void updateRound(int id, int round) {
        String query = "UPDATE Tournaments SET actual_round = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, round);
            ps.setInt(2, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ronda del torneo actualizada exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/


/////////////////// ACCIONES REGISTRO ///////////////////    
    
    /*// CREAR REGISTRO EN TORNEO ESTE ESTA HECHO ARRIBA BIEN
    public void createRegistration(String username, int idTournament) {
    	String date = "CURRENT_DATE";
        String query = "INSERT INTO Registration (tournament_id, username, creation_date) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idTournament);
            ps.setString(2, username);
            ps.setString(3, date);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Registro creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/


    /// //////////////// ACCIONES PARTIDO ///////////////////


    //REALIZA EL MATCHMAKING DE LA RONDA ACTUAL
    public void generateMatches(int tournamentId) {

        // Obtener la ronda actual del torneo
        String queryCurrentRound = "SELECT actual_round FROM Tournaments WHERE id = ?";
        int currentRound = 0;

        try (PreparedStatement ps = connection.prepareStatement(queryCurrentRound)) {
            ps.setInt(1, tournamentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentRound = rs.getInt("actual_round");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al recuperar la ronda actual del torneo con ID " + tournamentId);
            return;
        }

        if (currentRound == 0) {
            System.out.println("No se encontró información de rondas para el torneo con ID " + tournamentId);
            return;
        }

        System.out.println("Ronda actual del torneo: " + currentRound);

        // coger jugadores que están en la ronda actual del torneo

        String queryPlayers = "SELECT username FROM TournamentPlayers WHERE round = ? AND tournament_id = ?";
        List<String> players = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(queryPlayers)) {
            ps.setInt(1, currentRound);
            ps.setInt(2, tournamentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    players.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al recuperar jugadores de la ronda " + currentRound);
            return;
        }

        System.out.println("Jugadores en la ronda " + currentRound + ": " + players);

        // Barajar los jugadores para aleatoriedad
        Collections.shuffle(players);

        int nextRound = currentRound / 2; // Calcular la próxima ronda
        int numMatches = players.size() / 2;

        if (currentRound == 16) {
            numMatches = Math.min(numMatches, 8); // Hasta 8 partidos
        } else if (currentRound == 8) {
            numMatches = Math.min(numMatches, 4); // Hasta 4 partidos
        } else if (currentRound == 4) {
            numMatches = Math.min(numMatches, 2); // Hasta 2 partidos
        }

        System.out.println("Número de partidos a generar: " + numMatches);

        String insertMatchQuery = "INSERT INTO Matches (tournament_id, first_player_username, second_player_username, round) VALUES (?, ?, ?, ?)";
        String updatePlayerRoundQuery = "UPDATE TournamentPlayers SET round = ? WHERE username = ? AND tournament_id = ?";

        int i = 0;
       
        while (players.size() >= 2 && i < numMatches) {
            String firstPlayer = players.remove(0);
            String secondPlayer = players.remove(0);
         
            try (PreparedStatement ps = connection.prepareStatement(insertMatchQuery)) {
                ps.setInt(1, tournamentId);
                ps.setString(2, firstPlayer);
                ps.setString(3, secondPlayer);
                ps.setInt(4, currentRound);
                ps.executeUpdate();
                int matchID = getMatchID(tournamentId, firstPlayer, secondPlayer);
                if(matchID==-1) {
                	throw new SQLException();
                }
              
                System.out.println("Partido creado: " + firstPlayer + " vs " + secondPlayer);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al crear partido entre " + firstPlayer + " y " + secondPlayer);
            }
          
            
            i++;
        }
     
        // Si queda un jugador impar, avanzarlo directamente a la siguiente ronda
        if (!players.isEmpty()) {
            String remainingPlayer = players.remove(0);
            try (PreparedStatement ps = connection.prepareStatement(updatePlayerRoundQuery)) {
                ps.setInt(1, nextRound);
                ps.setString(2, remainingPlayer);
                ps.setInt(3, tournamentId);
                ps.executeUpdate();
                System.out.println("Jugador avanzado automáticamente a la ronda " + nextRound + ": " + remainingPlayer);
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error al avanzar jugador " + remainingPlayer + " a la ronda " + nextRound);
            }
        }
    }

    // CREAR PARTIDO// ESTO HAY QUE HACERLO CON EL MATCHMAKING
 /*   public void createMatch(int tournamentId, String username1, String username2, int round) {
        String query = "INSERT INTO Matches (tournament_id, first_player_username, second_player_username, round) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, tournamentId);
            ps.setString(2, username1);
            ps.setString(3, username2);
            ps.setInt(4, round);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Partido creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/


    // OBTENER ID DEL PARTIDO PARA UTILIZARLO EN OPERACIONES SIGUIENTES
    public int getMatchID(int tournamentId, String username1, String username2) {
        String query = "SELECT id FROM Matches WHERE tournament_id = ? AND first_player_username = ? AND second_player_username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, tournamentId);
            ps.setString(2, username1);
            ps.setString(3, username2);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
            	return rs.getInt("id");
            } else {
            	return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        
    }

    /// //////////////// ACCIONES SET ///////////////////

    //CREAR SET
    public void createSet(int matchId, int setNumber, int score1, int score2) {
        String query = "INSERT INTO Sets (match_id, set_number, first_player_score, second_player_score) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, matchId);
            ps.setInt(2, setNumber);
            ps.setInt(3, score1);
            ps.setInt(4, score2);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Set creado exitosamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    
    
    //////////////////////////////////////////////// CICLO 2 ////////////////////////////////////////////////

    
    // AVANZA TORNEO Y JUGADORES GANADORES DE SUS PARTIDOS A LA SIGUIENTE RONDA
    
    public void nextRound(int tournamentID) {
        try {
            // Obtener el actual_round del torneo
            String getRoundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
            int currentRound = 0;

            try (PreparedStatement ps = connection.prepareStatement(getRoundQuery)) {
                ps.setInt(1, tournamentID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentRound = rs.getInt("actual_round");
                    }
                }
            }
            
            // Determinar el número de partidos terminados requeridos para avanzar
            String getNumJugadoresQuery = "SELECT COUNT(*) AS players FROM tournamentplayers WHERE tournament_id = ? AND round = ?";
            int numJugadores = 0;
            try (PreparedStatement ps = connection.prepareStatement(getNumJugadoresQuery)) {
                ps.setInt(1, tournamentID);
                ps.setInt(2, currentRound);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        numJugadores = rs.getInt("players");
                    }
                }
            }
            int requiredMatches = numJugadores / 2;
            System.out.println("REQUIRED MATCHEES: " + requiredMatches);
            // Verificar si hay suficientes partidos completados en la ronda actual
            String checkMatchesQuery = "SELECT COUNT(*) AS completed_matches FROM Matches WHERE tournament_id = ? AND round = ? AND match_status = TRUE";
            int completedMatches = 0;

            try (PreparedStatement ps = connection.prepareStatement(checkMatchesQuery)) {
                ps.setInt(1, tournamentID);
                ps.setInt(2, currentRound);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        completedMatches = rs.getInt("completed_matches");
                    }
                }
            }

            // Validar si se cumplen las condiciones para avanzar de ronda
            if (completedMatches < requiredMatches) {
                System.out.println("Error: No hay suficientes partidos terminados para avanzar de ronda.");
                return; 
                
                // INTERFAZ GRÁFICA: SALTAR ERROR O DETERMINAR QUE EL BOTÓN NO PUEDE APARECER
                
            }

            
            // Determinar los ganadores de los partidos terminados y avanzar la ronda
            String getWinnersQuery = "SELECT id, first_player_username, second_player_username FROM Matches WHERE tournament_id = ? AND round = ? AND match_status = TRUE";
            String getSetScoresQuery = "SELECT first_player_score, second_player_score FROM Sets WHERE match_id = ?";
            
            
            String updatePlayerRoundQuery = "UPDATE TournamentPlayers SET round = ? WHERE tournament_id = ? AND username = ?";
            String updateTournamentRoundQuery = "UPDATE Tournaments SET actual_round = ? WHERE id = ?";

            int nextRound = currentRound / 2;

            try (PreparedStatement psWinners = connection.prepareStatement(getWinnersQuery);
                 PreparedStatement psScores = connection.prepareStatement(getSetScoresQuery);
                 PreparedStatement psUpdatePlayer = connection.prepareStatement(updatePlayerRoundQuery);
                 PreparedStatement psUpdateTournament = connection.prepareStatement(updateTournamentRoundQuery)) {

                psWinners.setInt(1, tournamentID);
                psWinners.setInt(2, currentRound);
                try (ResultSet rsWinners = psWinners.executeQuery()) {
                    while (rsWinners.next()) {
                        int matchID = rsWinners.getInt("id");
                        String firstPlayer = rsWinners.getString("first_player_username");
                        String secondPlayer = rsWinners.getString("second_player_username");

                        // Contadores para sets ganados
                        int firstPlayerWins = 0;
                        int secondPlayerWins = 0;

                        // Obtener sets y determinar el ganador
                        psScores.setInt(1, matchID);
                        try (ResultSet rsScores = psScores.executeQuery()) {
                            while (rsScores.next()) {
                                int firstScore = rsScores.getInt("first_player_score");
                                int secondScore = rsScores.getInt("second_player_score");
                                if (firstScore > secondScore) {
                                    firstPlayerWins++;
                                } else if (secondScore > firstScore) {
                                    secondPlayerWins++;
                                }
                            }
                        }

                        
                        // Avanzar el ganador a la siguiente ronda
                        String winner = firstPlayerWins >= 3 ? firstPlayer : secondPlayer;
                        psUpdatePlayer.setInt(1, nextRound);
                        psUpdatePlayer.setInt(2, tournamentID);
                        psUpdatePlayer.setString(3, winner);
                        psUpdatePlayer.executeUpdate();
                    }
                }

                // Actualizar el actual_round del torneo
                psUpdateTournament.setInt(1, nextRound);
                psUpdateTournament.setInt(2, tournamentID);
                psUpdateTournament.executeUpdate();
                System.out.println("El torneo ha avanzado a la ronda " + nextRound);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al avanzar de ronda.");
        }
    }
    
    // FINALIZACIÓN DE TORNEO
    public void endTournament(int tournamentID) {
        try {
            // Verificar si el torneo está en la ronda final (actual_round = 1)
            String getRoundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
            int currentRound = 0;

            try (PreparedStatement ps = connection.prepareStatement(getRoundQuery)) {
                ps.setInt(1, tournamentID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentRound = rs.getInt("actual_round");
                    }
                }
            }

            if (currentRound != 1) {
                System.out.println("Error: El torneo no está en la ronda final.");
                return;
            }

            // Poner el torneo como inactivo (active_status = false)
            String deactivateTournamentQuery = "UPDATE Tournaments SET active_status = FALSE WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(deactivateTournamentQuery)) {
                ps.setInt(1, tournamentID);
                ps.executeUpdate();
            }

            // Mapas para almacenar datos de los jugadores
            Map<String, Integer> playerSetWins = new HashMap<>();
            Map<String, Integer> playerGamesWon = new HashMap<>();
            Map<String, Integer> playerGamesLost = new HashMap<>();
            Map<String, Integer> playerRounds = new HashMap<>();
            Map<String, Integer> playerPoints = new HashMap<>();

            // Obtener jugadores y sus rondas
            String getPlayersQuery = "SELECT username, round FROM TournamentPlayers WHERE tournament_id = ?";
            List<String> players = new ArrayList<>();

            try (PreparedStatement ps = connection.prepareStatement(getPlayersQuery)) {
                ps.setInt(1, tournamentID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        int round = rs.getInt("round");
                        players.add(username);
                        playerRounds.put(username, round);
                    }
                }
            }

            // Calcular sets ganados, juegos ganados y juegos perdidos
            for (String player : players) {
                int setWins = 0;
                int gamesWon = 0;
                int gamesLost = 0;

                String getSetsQuery = "SELECT first_player_score, second_player_score, CASE WHEN first_player_username = ? THEN 'first' ELSE 'second' END AS player_position " +
                                      "FROM Sets s JOIN Matches m ON s.match_id = m.id WHERE m.tournament_id = ? AND (m.first_player_username = ? OR m.second_player_username = ?)";
                try (PreparedStatement ps = connection.prepareStatement(getSetsQuery)) {
                    ps.setString(1, player);
                    ps.setInt(2, tournamentID);
                    ps.setString(3, player);
                    ps.setString(4, player);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int firstPlayerScore = rs.getInt("first_player_score");
                            int secondPlayerScore = rs.getInt("second_player_score");
                            String playerPosition = rs.getString("player_position");

                            if ("first".equals(playerPosition)) {
                                if (firstPlayerScore > secondPlayerScore) setWins++;
                                gamesWon += firstPlayerScore;
                                gamesLost += secondPlayerScore;
                            } else {
                                if (secondPlayerScore > firstPlayerScore) setWins++;
                                gamesWon += secondPlayerScore;
                                gamesLost += firstPlayerScore;
                            }
                        }
                    }
                }

                playerSetWins.put(player, setWins);
                playerGamesWon.put(player, gamesWon);
                playerGamesLost.put(player, gamesLost);
            }

            // Clasificar jugadores
            List<String> rankedPlayers = players.stream()
                .sorted((p1, p2) -> {
                	System.out.println("Comprando ronda: " + p2 + ":" + playerRounds.get(p2) + ":" + p1 + ":" + playerRounds.get(p1) );
                    int roundComparison = Integer.compare(playerRounds.get(p2), playerRounds.get(p1)); // Descendente
                    if (roundComparison != 0) return roundComparison;

                	System.out.println("Comprando sets: " + p2 + ":" + playerSetWins.get(p2) + ":" + p1 + ":" + playerSetWins.get(p1) );
                    int setWinsComparison = Integer.compare(playerSetWins.get(p1), playerSetWins.get(p2)); // Descendente
                    if (setWinsComparison != 0) return setWinsComparison;

                	System.out.println("Comprando juegos ganados: " + p2 + ":" + playerGamesWon.get(p2) + ":" + p1 + ":" + playerGamesWon.get(p1) );
                    int gamesWonComparison = Integer.compare(playerGamesWon.get(p1), playerGamesWon.get(p2)); // Descendente
                    if (gamesWonComparison != 0) return gamesWonComparison;

                	System.out.println("Comprando juegos perdidos: " + p1 + ":" + playerGamesLost.get(p1) + ":" + p2 + ":" + playerGamesLost.get(p2) );
                    int gamesLostComparison = Integer.compare(playerGamesLost.get(p1), playerGamesLost.get(p2)); // Ascendente
                    if (gamesLostComparison != 0) return gamesLostComparison;

                    return 0; // Sin desempate aleatorio
                })
                .toList();
         // Imprimir los jugadores ordenados
            System.out.println("Final Rankings:");
            rankedPlayers.forEach(player -> {
                int setsWon = playerSetWins.get(player);
                int gamesWon = playerGamesWon.get(player);
                int gamesLost = playerGamesLost.get(player);

                System.out.println(player + ":" + setsWon + ":" + gamesWon + ":" + gamesLost);
            });

            // Asignar puntos a la inversa
            int[] points = {2000, 1500, 1000, 500, 475, 450, 425, 400, 375, 350, 325, 300, 275, 250, 225, 200};
            int pointIndex = 0; // Índice de puntos
            for (int i = rankedPlayers.size() - 1; i >= 0 && pointIndex < points.length; i--) {
                playerPoints.put(rankedPlayers.get(i), points[pointIndex]);
                pointIndex++;
            }

            // Verificar puntos asignados
            System.out.println("Puntos asignados (orden inverso):");
            for (Map.Entry<String, Integer> entry : playerPoints.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " puntos");
            }

            // Actualizar puntos en la base de datos
            String updatePointsQuery = "UPDATE Users SET totalPoints = totalPoints + ? WHERE username = ?";
            try (PreparedStatement ps = connection.prepareStatement(updatePointsQuery)) {
                for (Map.Entry<String, Integer> entry : playerPoints.entrySet()) {
                    ps.setInt(1, entry.getValue());
                    ps.setString(2, entry.getKey());
                    ps.executeUpdate();
                }
            }

            String updateFinalPointsQuery = "UPDATE TournamentPlayers SET finalPoints = ? WHERE tournament_id = ? AND username = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateFinalPointsQuery)) {
                for (Map.Entry<String, Integer> entry : playerPoints.entrySet()) {
                    ps.setInt(1, entry.getValue());
                    ps.setInt(2, tournamentID);
                    ps.setString(3, entry.getKey());
                    ps.executeUpdate();
                }
            }

            System.out.println("Torneo finalizado y puntos asignados en orden inverso correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al finalizar el torneo.");
        }
    }
    //CHECK IF YOU CAN ADVANCE ROUND
    public boolean canAdvanceRound(int tournamentID) {
        String getRoundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
        String checkMatchesQuery = "SELECT COUNT(*) AS completed_matches FROM Matches WHERE tournament_id = ? AND round = ? AND match_status = TRUE";

        try {
            int currentRound = 0;

            // Obtener el actual_round del torneo
            try (PreparedStatement ps = connection.prepareStatement(getRoundQuery)) {
                ps.setInt(1, tournamentID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentRound = rs.getInt("actual_round");
                        if(currentRound == 2) {
                        	return false;
                        }
                    } else {
                        System.out.println("Error: Torneo no encontrado.");
                        return false;
                    }
                }
            }
            if(currentRound==1)return false;//si torneo acabado then false
            // Calcular el número de partidos completados en la ronda actual
            int completedMatches = 0;
            try (PreparedStatement ps = connection.prepareStatement(checkMatchesQuery)) {
                ps.setInt(1, tournamentID);
                ps.setInt(2, currentRound);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        completedMatches = rs.getInt("completed_matches");
                    }
                }
            }
            
            // Determinar el número de partidos terminados requeridos para avanzar
            String getNumJugadoresQuery = "SELECT COUNT(*) AS players FROM tournamentplayers WHERE tournament_id = ? AND round = ?";
            int numJugadores = 0;
            try (PreparedStatement ps = connection.prepareStatement(getNumJugadoresQuery)) {
                ps.setInt(1, tournamentID);
                ps.setInt(2, currentRound);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        numJugadores = rs.getInt("players");
                    }
                }
            }
            int requiredMatches = numJugadores / 2;

            // Validar si se cumplen las condiciones para avanzar de ronda
            if (completedMatches == requiredMatches) {
                return true;
            } else {
                System.out.println("No hay suficientes partidos terminados para avanzar de ronda.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar si se puede avanzar de ronda.");
            return false;
        }
    }
    
  //VERIFICA SI SE PUEDE HACER EL MATCHMAKING

    public boolean canMatchmaking(int tournamentID) {
        String getRoundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
        String checkMatchesQuery = "SELECT COUNT(*) AS match_count FROM Matches WHERE tournament_id = ? AND round = ?";

        try {
            int currentRound = 0;

            // Obtener el actual_round del torneo
            try (PreparedStatement ps = connection.prepareStatement(getRoundQuery)) {
                ps.setInt(1, tournamentID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentRound = rs.getInt("actual_round");
                    } else {
                        System.out.println("Error: Torneo no encontrado.");
                        return false; // O lanza una excepción si lo prefieres
                    }
                }
            }
            if(currentRound==1)return false;// si torneo acabado then false
            // Verificar si existen partidos creados en el actual_round
            int matchCount = 0;
            try (PreparedStatement ps = connection.prepareStatement(checkMatchesQuery)) {
                ps.setInt(1, tournamentID);
                ps.setInt(2, currentRound);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        matchCount = rs.getInt("match_count");
                    }
                }
            }

            // Retorna true si no hay partidos creados, false en caso contrario
            return matchCount == 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar si se puede realizar matchmaking.");
            return false;
        }
    }

    
    
    //UPDATE RESULTS OF A MATCH
    public void updateResults(Match match) {
        String insertSetsQuery = "INSERT INTO Sets (match_id, set_number, first_player_score, second_player_score) VALUES (?, ?, ?, ?)";
        String updateMatchStatusQuery = "UPDATE Matches SET match_status = TRUE WHERE id = ?";
        try {
            // Insertar resultados de los sets
            try (PreparedStatement ps = connection.prepareStatement(insertSetsQuery)) {
                for (Map.Entry<Integer, Pair<Integer, Integer>> entry : match.getPoints().entrySet()) {
                    int setNumber = entry.getKey();
                    int firstPlayerScore = entry.getValue().getKey();
                    int secondPlayerScore = entry.getValue().getValue();

                    ps.setInt(1, match.getId());
                    ps.setInt(2, setNumber);
                    ps.setInt(3, firstPlayerScore);
                    ps.setInt(4, secondPlayerScore);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

         // Actualizar el estado del partido
            try (PreparedStatement ps = connection.prepareStatement(updateMatchStatusQuery)) {
                ps.setInt(1, match.getId());
                ps.executeUpdate();
           
            }

            System.out.println("Resultados de los sets añadidos correctamente.");
            if (match.getRound() ==2) {
            	nextRound(match.getTournamentId());
            	endTournament(match.getTournamentId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al insertar los resultados de los sets.");
        }
       
    }

    
    
    // RÁNKING GLOBAL
    public void computeRanking() {
        List<Pair<String, Integer>> ranking = new ArrayList<>();

        try {
            // Obtener todos los usuarios no administradores y sus puntos totales
            String getUsersQuery = "SELECT username, totalPoints FROM Users WHERE isAdmin = false";
            Map<String, Integer> userPoints = new HashMap<>();
            Map<String, Integer> userSetWins = new HashMap<>();
            Map<String, Integer> userGamesWon = new HashMap<>();
            Map<String, Integer> userGamesLost = new HashMap<>();
            List<String> users = new ArrayList<>();

            try (PreparedStatement ps = connection.prepareStatement(getUsersQuery);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    int totalPoints = rs.getInt("totalPoints");
                    users.add(username);
                    userPoints.put(username, totalPoints);
                }
            }

            // Calcular sets ganados, juegos ganados y juegos perdidos para cada usuario
            for (String user : users) {
                int setWins = 0;
                int gamesWon = 0;
                int gamesLost = 0;

                String getSetsQuery = "SELECT first_player_score, second_player_score, CASE WHEN first_player_username = ? THEN 'first' ELSE 'second' END AS player_position " +
                                      "FROM Sets s JOIN Matches m ON s.match_id = m.id WHERE m.first_player_username = ? OR m.second_player_username = ?";

                try (PreparedStatement ps = connection.prepareStatement(getSetsQuery)) {
                    ps.setString(1, user);
                    ps.setString(2, user);
                    ps.setString(3, user);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int firstPlayerScore = rs.getInt("first_player_score");
                            int secondPlayerScore = rs.getInt("second_player_score");
                            String playerPosition = rs.getString("player_position");

                            if ("first".equals(playerPosition)) {
                                if (firstPlayerScore > secondPlayerScore) setWins++;
                                gamesWon += firstPlayerScore;
                                gamesLost += secondPlayerScore;
                            } else {
                                if (secondPlayerScore > firstPlayerScore) setWins++;
                                gamesWon += secondPlayerScore;
                                gamesLost += firstPlayerScore;
                            }
                        }
                    }
                }

                userSetWins.put(user, setWins);
                userGamesWon.put(user, gamesWon);
                userGamesLost.put(user, gamesLost);
            }

            // Ordenar usuarios según puntos totales y criterios de desempate
            ranking = users.stream()
                    .sorted((u1, u2) -> {
                        int pointsComparison = Integer.compare(userPoints.get(u2), userPoints.get(u1));
                        if (pointsComparison != 0) return pointsComparison;

                        int setWinsComparison = Integer.compare(userSetWins.get(u2), userSetWins.get(u1));
                        if (setWinsComparison != 0) return setWinsComparison;

                        int gamesWonComparison = Integer.compare(userGamesWon.get(u2), userGamesWon.get(u1));
                        if (gamesWonComparison != 0) return gamesWonComparison;

                        int gamesLostComparison = Integer.compare(userGamesLost.get(u1), userGamesLost.get(u2));
                        if (gamesLostComparison != 0) return gamesLostComparison;

                        return new Random().nextInt(2) * 2 - 1; // Desempate aleatorio
                    })
                    .map(user -> new Pair<>(user, userPoints.get(user)))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener el ranking global.");
        }
        System.out.println(ranking);

        RankingPanel.getInstance().addUsers(ranking);
    }
    
    // VISUALIZACIÓN DE PUNTOS Y POSICIÓN FINAL EN TORNEO
    public String getTournamentResults(int tournamentID) {
        try {
            // Crear el mapa de puntos a posiciones manualmente
            Map<Integer, Integer> pointsToPosition = new HashMap<>();
            pointsToPosition.put(2000, 1);
            pointsToPosition.put(1500, 2);
            pointsToPosition.put(1000, 3);
            pointsToPosition.put(500, 4);
            pointsToPosition.put(475, 5);
            pointsToPosition.put(450, 6);
            pointsToPosition.put(425, 7);
            pointsToPosition.put(400, 8);
            pointsToPosition.put(375, 9);
            pointsToPosition.put(350, 10);
            pointsToPosition.put(325, 11);
            pointsToPosition.put(300, 12);
            pointsToPosition.put(275, 13);
            pointsToPosition.put(250, 14);
            pointsToPosition.put(225, 15);
            pointsToPosition.put(200, 16);

            // Consulta para obtener los puntos finales de todos los jugadores en el torneo
            String query = "SELECT username, finalPoints FROM TournamentPlayers WHERE tournament_id = ?";
            StringBuilder results = new StringBuilder();

            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, tournamentID);

                System.out.println("Ejecutando consulta para torneo ID: " + tournamentID); // Depuración

                try (ResultSet rs = ps.executeQuery()) {
                    boolean foundResults = false;

                    while (rs.next()) {
                        foundResults = true;
                        String username = rs.getString("username");
                        int finalPoints = rs.getInt("finalPoints");

                        // Log para depuración
                        System.out.println("Procesando usuario: " + username + ", Puntos: " + finalPoints);

                        // Determinar la posición final a partir de los puntos finales
                        Integer position = pointsToPosition.get(finalPoints);
                        if (position == null) {
                            results.append("Usuario: ").append(username)
                                   .append(" - Error: Los puntos finales no coinciden con ninguna posición conocida.\n");
                        } else {
                            // Formatear el resultado para este usuario
                            results.append("Usuario: ").append(username).append("\n")
                                   .append("Puntos obtenidos: ").append(finalPoints).append("\n")
                                   .append("Posición final: ").append(position).append("\n\n");
                        }
                    }

                    if (!foundResults) {
                        System.out.println("No se encontraron jugadores para el torneo."); // Depuración
                        return "Error: No se encontraron jugadores para este torneo.";
                    }
                }
            }

            // Mostrar resultados en consola para depuración
            System.out.println("Resultados generados:\n" + results);

            // Devolver los resultados formateados
            return results.toString();

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Ocurrió un problema al obtener los resultados del torneo.";
        }
    }

    // CERRAMOS CONEXION A BASE DE DATOS
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter, returns the logged user in the application
     *
     * @return the logged user
     */
    public User getActualUser() {
        return actualUser;
    }

    /**
     * Handles the logic to log out the actual user
     */
    public void logout() {
        actualUser = null;
    }

    /**
     * Modifies the user data
     *
     * @param user the user with the new data
     */
    public int modifyUser(User user) {
        // Consulta para verificar si el nombre de usuario ya existe
        String checkUsernameQuery = "SELECT COUNT(*) FROM Users WHERE username = ? AND password = ? AND phone = ?";

        // Si el nombre de usuario ya existe, retornar un mensaje indicando el error
        try (PreparedStatement ps = connection.prepareStatement(checkUsernameQuery)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getPhone());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Si ya existe el usuario, retornamos un mensaje
                    System.out.println("Error: El nombre de usuario ya está en uso.");
                    return 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 2; // Error al consultar la base de datos
        }

        // Si el nombre de usuario no existe, podemos proceder a crear el usuario
        String insertUserQuery = "UPDATE Users SET username = ?, password = ?, phone = ? WHERE username = ?;";

        try (PreparedStatement ps = connection.prepareStatement(insertUserQuery)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getPhone());
            ps.setString(4, actualUser.getUsername());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Usuario actualizado exitosamente.");
                actualUser = user;
                return 0;
            } else {
                System.out.println("Error al actualizar el usuario.");
                return 3;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Loads stats of the current user for each tournament
     */
    public void loadTournamentStats() {
        List<PlayerTournamentStats> playerStatsList = new ArrayList<>();
        String currentUser = getActualUser().getUsername(); // Obtén el usuario actual

        // Consulta para obtener los torneos en los que ha participado el usuario
        String getTournamentsQuery = 
            "SELECT tp.tournament_id, t.name, tp.finalPoints " +
            "FROM TournamentPlayers tp " +
            "JOIN Tournaments t ON tp.tournament_id = t.id " +
            "WHERE tp.username = ?";

        // Consulta para obtener estadísticas de sets y juegos
        String getMatchStatsQuery = 
            "SELECT " +
            "  SUM(CASE " +
            "      WHEN (s.first_player_score > s.second_player_score AND m.first_player_username = ?) OR " +
            "           (s.second_player_score > s.first_player_score AND m.second_player_username = ?) THEN 1 " +
            "      ELSE 0 " +
            "  END) AS set_wins, " +
            "  SUM(CASE " +
            "      WHEN m.first_player_username = ? THEN s.first_player_score " +
            "      WHEN m.second_player_username = ? THEN s.second_player_score " +
            "      ELSE 0 " +
            "  END) AS game_wins, " +
            "  SUM(CASE " +
            "      WHEN m.first_player_username = ? THEN s.second_player_score " +
            "      WHEN m.second_player_username = ? THEN s.first_player_score " +
            "      ELSE 0 " +
            "  END) AS game_losses " +
            "FROM Matches m " +
            "JOIN Sets s ON m.id = s.match_id " +
            "WHERE m.tournament_id = ?";

        try (PreparedStatement tournamentPs = connection.prepareStatement(getTournamentsQuery)) {
            tournamentPs.setString(1, currentUser);

            try (ResultSet tournamentsRs = tournamentPs.executeQuery()) {
                while (tournamentsRs.next()) {
                    int tournamentId = tournamentsRs.getInt("tournament_id");
                    String tournamentFullName = tournamentsRs.getString("name");
                    int finalPoints = tournamentsRs.getInt("finalPoints");

                    int position = determinePosition(finalPoints);
                    int setWins = 0, gameWins = 0, gameLosses = 0;

                    // Obtener estadísticas de sets y juegos
                    try (PreparedStatement matchStatsPs = connection.prepareStatement(getMatchStatsQuery)) {
                        matchStatsPs.setString(1, currentUser); // Primer parámetro: jugador actual en first_player_username
                        matchStatsPs.setString(2, currentUser); // Segundo parámetro: jugador actual en second_player_username
                        matchStatsPs.setString(3, currentUser); // Tercer parámetro: jugador actual para game_wins
                        matchStatsPs.setString(4, currentUser); // Cuarto parámetro: jugador actual para game_wins
                        matchStatsPs.setString(5, currentUser); // Quinto parámetro: jugador actual para game_losses
                        matchStatsPs.setString(6, currentUser); // Sexto parámetro: jugador actual para game_losses
                        matchStatsPs.setInt(7, tournamentId);   // Séptimo parámetro: ID del torneo

                        try (ResultSet statsRs = matchStatsPs.executeQuery()) {
                            if (statsRs.next()) {
                                setWins = statsRs.getInt("set_wins");
                                gameWins = statsRs.getInt("game_wins");
                                gameLosses = statsRs.getInt("game_losses");
                            }
                        }
                    }

                    // Crear el objeto PlayerTournamentStats y añadirlo a la lista
                    PlayerTournamentStats stats = new PlayerTournamentStats(currentUser, tournamentFullName, setWins, gameWins, gameLosses, position);
                    playerStatsList.add(stats);

                    // Cargar las estadísticas en el panel
                    MyTournamentsStatsPanel.getInstance().loadStats(playerStatsList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cargar las estadísticas del torneo.");
        }
    }

    
    //DETERMINAR POSICIÓN EN EL TORNEO
    private int determinePosition(int finalPoints) {
        Map<Integer, Integer> pointsToPosition = new HashMap<>();
        pointsToPosition.put(2000, 1);
        pointsToPosition.put(1500, 2);
        pointsToPosition.put(1000, 3);
        pointsToPosition.put(500, 4);
        pointsToPosition.put(475, 5);
        pointsToPosition.put(450, 6);
        pointsToPosition.put(425, 7);
        pointsToPosition.put(400, 8);
        pointsToPosition.put(375, 9);
        pointsToPosition.put(350, 10);
        pointsToPosition.put(325, 11);
        pointsToPosition.put(300, 12);
        pointsToPosition.put(275, 13);
        pointsToPosition.put(250, 14);
        pointsToPosition.put(225, 15);
        pointsToPosition.put(200, 16);

        return pointsToPosition.getOrDefault(finalPoints, -1); // Devuelve -1 si no se encuentra una posición
    }

    //LISTA DE PARTIDOS DE UN TORNEO
    public List<Match> getTournamentMatches(int tournamentID) {
        List<Match> matches = new ArrayList<>();

        try {
            String query = "SELECT id, first_player_username, second_player_username, match_status FROM Matches WHERE tournament_id = ? AND round = ?";
            String getRoundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
            int currentRound = 0;

         // Obtener el actual_round del torneo
            try (PreparedStatement ps = connection.prepareStatement(getRoundQuery)) {
                ps.setInt(1, tournamentID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentRound = rs.getInt("actual_round");
                    } 
                }
            }


            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, tournamentID);
                ps.setInt(2, currentRound);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int matchID = rs.getInt("id");
                        String firstPlayer = rs.getString("first_player_username");
                        String secondPlayer = rs.getString("second_player_username");
                        String status = rs.getString("match_status");
                        int round = currentRound;
                        System.out.println("metodo nuevo");
                        Match match = new Match(matchID, tournamentID, firstPlayer, secondPlayer, round);


                        matches.add(match);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener los partidos del torneo.");
        }
        ResultsMatchPanel.getInstance().addMatches(matches);
        return matches;
    }
}
