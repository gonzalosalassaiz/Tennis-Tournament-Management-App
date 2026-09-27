package tenis_upm.grupo11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.util.Pair;
import tenis_upm.grupo11.data.Match;
import tenis_upm.grupo11.data.Tournament;
import tenis_upm.grupo11.functionality.DBManager;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.startsWith;

public class AppTest {
    private DBManager dbManager;
    public volatile int createUserResult;
    @BeforeEach
    public void setUp() {
        // Inicializar DBManager antes de cada test
        dbManager = new DBManager();
        assertNotNull(dbManager.getConnection(), "La conexión con la base de datos no debe ser nula.");
        String[] queries = {
                "DELETE FROM sets",
                "DELETE FROM matches",
                "DELETE FROM registration",
                "DELETE FROM tournamentplayers",
                "DELETE FROM tournaments",
                "DELETE FROM users"
            };

            try (Statement statement = dbManager.getConnection().createStatement()) {
                for (String query : queries) {
                    int rowsAffected = statement.executeUpdate(query);
                    System.out.println("Cleared table: " + query.split(" ")[2] + " | Rows affected: " + rowsAffected);
                }
            } catch (SQLException e) {
                System.err.println("Error clearing the database: " + e.getMessage());
            }
    }

    @Test
    public void testCreateUserWithValidData() {
        // Crear un usuario con parámetros válidos
    	new Thread(() -> { createUserResult = dbManager.createUser(
                "usuarioTest",
                "Usuario Test",
                "123456789",
                "usuario@test.com",
                "password123",
                false  // admin = false
        );}).start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dbManager.verified = true;
        // Comprobamos que el resultado es 0 (usuario creado exitosamente)
        assertEquals(0, createUserResult, "El usuario debería ser creado exitosamente.");
    }

    @Test
    public void testCreateUserWithDuplicateUsername() {
        // Crear un usuario con un nombre de usuario que ya existe
    	new Thread(() -> { dbManager.createUser(
                "usuarioTest",
                "Usuario Test",
                "123456789",
                "usuario@test.com",
                "password123",
                false
        );}).start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dbManager.verified = true;
        // Intentamos crear otro usuario con el mismo nombre de usuario
        new Thread(() -> { createUserResult = dbManager.createUser(
                "usuarioTest",  // Nombre de usuario duplicado
                "Usuario Duplicado",
                "987654321",
                "duplicate@test.com",
                "password456",
                false
        );}).start();
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dbManager.verified = true;
        // Debería devolver 2 porque el nombre de usuario ya está en uso
        assertEquals(2, createUserResult, "El nombre de usuario ya está en uso.");
    }

    @Test
    public void testLoginWithCorrectCredentials() {
        // Crear un usuario con parámetros válidos
    	new Thread(() -> { createUserResult = dbManager.createUser(
                "usuarioLogin",
                "Usuario Login",
                "123456789",
                "login@test.com",
                "password123",
                false
        );}).start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dbManager.verified = true;
        assertEquals(0, createUserResult, "El usuario debería ser creado exitosamente.");

        // Intentar hacer login con las credenciales correctas
        boolean loginSuccess = dbManager.login("usuarioLogin", "password123");
        assertTrue(loginSuccess, "El login debería ser exitoso.");
    }

    @Test
    public void testLoginWithIncorrectCredentials() {
        // Crear un usuario con parámetros válidos
    	new Thread(() -> { createUserResult = dbManager.createUser(
                "usuarioIncorrecto",
                "Usuario Incorrecto",
                "123456789",
                "incorrect@test.com",
                "password123",
                false
        );}).start();
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dbManager.verified = true;
        assertEquals(0, createUserResult, "El usuario debería ser creado exitosamente.");

        // Intentar hacer login con credenciales incorrectas
        boolean loginSuccess = dbManager.login("usuarioIncorrecto", "wrongPassword");
        assertFalse(loginSuccess, "El login debería fallar con la contraseña incorrecta.");
    }

    @Test
    public void testCreateRegistrationWithValidTournament() throws Exception {
        // Crear un usuario con parámetros válidos
    	new Thread(() -> { createUserResult = dbManager.createUser(
                "usuarioRegistro",
                "Usuario Registro",
                "123456789",
                "registro@test.com",
                "password123",
                false
        ); }).start();
    	Thread.sleep(1000);
        dbManager.verified = true;
        assertEquals(0, createUserResult, "El usuario debería ser creado exitosamente.");

        // Loguear al usuario
        boolean loginSuccess = dbManager.login("usuarioRegistro", "password123");
        assertTrue(loginSuccess, "El login debería ser exitoso.");

        // Crear un torneo con fecha válida
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = sdf.parse("2024-12-31");
        Date tournamentDate = new Date(utilDate.getTime());  // Convertir a java.sql.Date
        boolean tournamentId = dbManager.createTournament("Torneo Test", 2024, tournamentDate);
        assertTrue(tournamentId, "No se generó el torneo");

        // Registrar al usuario en el torneo
        boolean registrationSuccess = dbManager.createRegistration(dbManager.getTournamentID("Torneo Test", 2024));
        assertTrue(registrationSuccess, "El registro en el torneo debería ser exitoso.");
    }

    @Test
    public void testCreateRegistrationFailsForPastTournamentDeadline() throws Exception {
        // Crear un usuario con parámetros válidos
        new Thread(() -> {
        	createUserResult = dbManager.createUser(
                    "usuarioDeadline",
                    "Usuario Deadline",
                    "987654321",
                    "deadline@test.com",
                    "password123",
                    false
            );
        }).start();
        Thread.sleep(1000);
        dbManager.verified = true;
        assertEquals(0, createUserResult, "El usuario debería ser creado exitosamente.");

        // Loguear al usuario
        boolean loginSuccess = dbManager.login("usuarioDeadline", "password123");
        assertTrue(loginSuccess, "El login debería ser exitoso.");

        // Crear un torneo con fecha pasada
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = sdf.parse("2022-12-31");
        Date tournamentDate = new Date(utilDate.getTime());  // Convertir a java.sql.Date
        boolean tournamentId = dbManager.createTournament("Torneo Pasado", 2024, tournamentDate);
        assertTrue(tournamentId, "El torneo no se generó correcto.");

        // Intentar registrar al usuario en el torneo cuyo plazo ya pasó
        boolean registrationSuccess = dbManager.createRegistration(dbManager.getTournamentID("Torneo Test", 2024));
        assertFalse(registrationSuccess, "El registro no debería ser posible, la fecha límite ya pasó.");
    }
   
    @Test
    public void testCreateRegistrationFailsIfUserAlreadyRegistered() throws Exception {
        // Crear un usuario con parámetros válidos
        
        new Thread(() -> {
            createUserResult = dbManager.createUser(
                    "usuarioDuplicado",
                    "Usuario Duplicado",
                    "123456789",
                    "duplicado@test.com",
                    "password123",
                    false
            );;
        }).start();
        Thread.sleep(1000);
        dbManager.verified = true;
        assertEquals(0, createUserResult, "El usuario debería ser creado exitosamente.");

        // Loguear al usuario
        boolean loginSuccess = dbManager.login("usuarioDuplicado", "password123");
        assertTrue(loginSuccess, "El login debería ser exitoso.");

        // Crear un torneo con fecha válida
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = sdf.parse("2024-12-31");
        Date tournamentDate = new Date(utilDate.getTime());  // Convertir a java.sql.Date
        boolean tournamentId = dbManager.createTournament("Torneo Unico", 2024, tournamentDate);
        assertTrue(tournamentId, "El torneo no se generó correcto.");

        // Registrar al usuario en el torneo
        boolean registrationSuccess = dbManager.createRegistration(dbManager.getTournamentID("Torneo Unico", 2024));
        assertTrue(registrationSuccess, "El registro en el torneo debería ser exitoso.");

        // Intentar registrar al mismo usuario en el mismo torneo
        registrationSuccess = dbManager.createRegistration(dbManager.getTournamentID("Torneo Unico", 2024));
        assertFalse(registrationSuccess, "El usuario no debería poder registrarse más de una vez en el mismo torneo.");
    }
    @Test
    public void testCreateTournamentWithValidData() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());

        boolean result = dbManager.createTournament("Torneo1", 2024, deadline);
        assertTrue(result, "El torneo debería ser creado exitosamente.");
    }

    @Test
    public void testCreateDuplicateTournament() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());

        dbManager.createTournament("TorneoDuplicado", 2024, deadline);
        boolean result = dbManager.createTournament("TorneoDuplicado", 2024, deadline);

        assertFalse(result, "No debería ser posible crear un torneo duplicado con el mismo nombre y año.");
    }

    @Test
    public void testGetTournamentID() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());

        dbManager.createTournament("TorneoID", 2024, deadline);
        int tournamentId = dbManager.getTournamentID("TorneoID", 2024);

        assertTrue(tournamentId > 0, "El ID del torneo debería ser válido y mayor a 0.");
    }

    @Test
    public void testGetTournamentIDNotFound() {
        int tournamentId = dbManager.getTournamentID("TorneoNoExiste", 2024);
        assertEquals(-1, tournamentId, "El ID debería ser -1 si el torneo no existe.");
    }

    @Test
    public void testShowRegisterTournaments() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());

        dbManager.createTournament("TorneoRegistrable1", 2024, deadline);
        dbManager.createTournament("TorneoRegistrable2", 2024, deadline);

        List<Tournament> tournaments = dbManager.showRegisterTournaments();
        assertFalse(tournaments.isEmpty(), "Debería haber al menos un torneo con registro abierto.");
    }

    @Test
    public void testShowRegisterTournamentsNoTournaments() {
        List<Tournament> tournaments = dbManager.showRegisterTournaments();
        assertTrue(tournaments.isEmpty(), "No debería haber torneos con registro abierto si no se han creado.");
    }

    @Test
    public void testInitiateTournamentWithValidPlayers() throws Exception {
        // Preparar datos
    	new Thread(() -> {dbManager.createUser("Jugador1", "Jugador Uno", "123456789", "jugador1@test.com", "password123", false);}).start();
        Thread.sleep(1000);
    	dbManager.verified=true;
    	new Thread(() -> {dbManager.createUser("Jugador2", "Jugador Dos", "987654321", "jugador2@test.com", "password456", false);}).start();
        Thread.sleep(1000);
    	dbManager.verified=true;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());
        dbManager.createTournament("TorneoIniciable", 2024, deadline);

        int tournamentId = dbManager.getTournamentID("TorneoIniciable", 2024);

        // Registrar jugadores
        dbManager.login("Jugador1", "password123");
        dbManager.createRegistration(tournamentId);
        dbManager.login("Jugador2", "password456");
        dbManager.createRegistration(tournamentId);

        // Iniciar torneo
        dbManager.initiateTournament(tournamentId);
        assertTrue(dbManager.isTournamentActive(tournamentId), "El torneo debería estar activo después de iniciarse.");
    }

    @Test
    public void testInitiateTournamentWithoutPlayers() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());
        dbManager.createTournament("TorneoSinJugadores", 2024, deadline);

        int tournamentId = dbManager.getTournamentID("TorneoSinJugadores", 2024);

        // Intentar iniciar torneo sin jugadores registrados
        dbManager.initiateTournament(tournamentId);

        // Verificar
        assertFalse(dbManager.isTournamentActive(tournamentId), "El torneo no debería estar activo sin jugadores registrados.");
    }

    @Test
    public void testInitiateTournamentNotEnoughPlayers() throws Exception {
        // Preparar datos
    	new Thread(() -> { dbManager.createUser("JugadorSolo", "Jugador Solitario", "123456789", "solitario@test.com", "password123", false);}).start();
    	Thread.sleep(1000);
    	dbManager.verified=true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());
        dbManager.createTournament("TorneoInsuficiente", 2024, deadline);

        int tournamentId = dbManager.getTournamentID("TorneoInsuficiente", 2024);

        // Registrar un solo jugador
        dbManager.login("JugadorSolo", "password123");
        dbManager.createRegistration(tournamentId);

        // Intentar iniciar torneo
        dbManager.initiateTournament(tournamentId);

        // Verificar
        assertTrue(dbManager.isTournamentActive(tournamentId), "El torneo debería estar activo incluso con un solo jugador.");
    }
    @Test
    public void testGenerateMatchesNormal() throws InterruptedException, ParseException {
        // Create users with unique phone numbers
    	new Thread(() -> { dbManager.createUser("Jugador1", "Jugador Uno", "1234567890", "jugador1@test.com", "password123", false);}).start();
        Thread.sleep(1000);  // Ensure user creation is processed
        dbManager.verified = true;  // Set verification flag for created users
        new Thread(() -> {dbManager.createUser("Jugador2", "Jugador Dos", "1234567891", "jugador2@test.com", "password123", false);}).start();
        Thread.sleep(1000);  // Ensure user creation is processed
        dbManager.verified = true;  // Set verification flag for created users
        new Thread(() -> { dbManager.createUser("Jugador3", "Jugador Tres", "1234567892", "jugador3@test.com", "password123", false);}).start();
        Thread.sleep(1000);  // Ensure user creation is processed
        dbManager.verified = true;  // Set verification flag for created users
        new Thread(() -> { dbManager.createUser("Jugador4", "Jugador Cuatro", "1234567893", "jugador4@test.com", "password123", false);}).start();
        Thread.sleep(1000);  // Ensure user creation is processed
        dbManager.verified = true;  // Set verification flag for created users
        Thread.sleep(1000); 
        // Create tournament
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());
        boolean isTournamentCreated = dbManager.createTournament("TorneoUnico", 2024, deadline);
        // Assert tournament creation
        assertTrue(isTournamentCreated, "The tournament should be created successfully.");

        // Get tournament ID
        int tournamentId = dbManager.getTournamentID("TorneoUnico", 2024);

        // Log in users and register each user
        dbManager.login("Jugador1", "password123");
        dbManager.createRegistration(tournamentId );  // Register Jugador1

        dbManager.login("Jugador2", "password123");
        dbManager.createRegistration(tournamentId );   // Register Jugador2

        dbManager.login("Jugador3", "password123");
        dbManager.createRegistration(tournamentId );   // Register Jugador3

        dbManager.login("Jugador4", "password123");
        dbManager.createRegistration(tournamentId );  // Register Jugador4

       
       dbManager.initiateTournament(tournamentId);
        // Call generateMatches to create matches for the current round (e.g., 4 players, 2 matches)
        dbManager.generateMatches(tournamentId);

        // Verify the generated matches (match table should now have 2 matches for the first round)
        String queryMatches = "SELECT COUNT(*) FROM Matches WHERE tournament_id = ?";
        int matchCount = 0;
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(queryMatches)) {
            ps.setInt(1, tournamentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    matchCount = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // There should be 2 matches created in the first round for 4 players
        assertEquals(2, matchCount, "There should be 2 matches created in the first round.");
    }

    @Test
    public void testGenerateMatchesWithOddPlayers() throws InterruptedException, ParseException {
        // Create users with unique phone numbers
    	new Thread(() -> { dbManager.createUser("Jugador1", "Jugador Uno", "1234567890", "jugador1@test.com", "password123", false);}).start();
        Thread.sleep(1000);  // Ensure user creation is processed
        dbManager.verified = true;  // Set verification flag for created users
    	new Thread(() -> { dbManager.createUser("Jugador2", "Jugador Dos", "1234567891", "jugador2@test.com", "password123", false);}).start();
        Thread.sleep(1000);  // Ensure user creation is processed
        dbManager.verified = true;  // Set verification flag for created users
    	new Thread(() -> { dbManager.createUser("Jugador3", "Jugador Tres", "1234567892", "jugador3@test.com", "password123", false);}).start();
        Thread.sleep(1000);  // Ensure user creation is processed
        dbManager.verified = true;  // Set verification flag for created users
        Thread.sleep(1000);
        // Create tournament
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());
        boolean isTournamentCreated = dbManager.createTournament("TorneoUnico", 2024, deadline);
        // Log in users and register each user
        dbManager.login("Jugador1", "password123");
        dbManager.createRegistration(dbManager.getTournamentID("TorneoUnico", 2024));  // Register Jugador1

        dbManager.login("Jugador2", "password123");
        dbManager.createRegistration(dbManager.getTournamentID("TorneoUnico", 2024));  // Register Jugador2

        dbManager.login("Jugador3", "password123");
        dbManager.createRegistration(dbManager.getTournamentID("TorneoUnico", 2024));  // Register Jugador3

       

        // Assert tournament creation
        assertTrue(isTournamentCreated, "The tournament should be created successfully.");

        // Get tournament ID
        int tournamentId = dbManager.getTournamentID("TorneoUnico", 2024);
        dbManager.initiateTournament(tournamentId);
        // Call generateMatches to create matches for the current round
        dbManager.generateMatches(tournamentId);

        // Verify that one player should have been advanced automatically
        String queryPlayers = "SELECT username FROM TournamentPlayers WHERE tournament_id = ? AND round = ?";
        List<String> playersInNextRound = new ArrayList<>();
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(queryPlayers)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, 2);  // Round 2 should have the player advanced
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    playersInNextRound.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // There should be 1 player advanced, so 2 players in the next round
        assertTrue(playersInNextRound.size() > 0, "One player should have been advanced automatically.");
    }

    @Test
    public void testGenerateMatchesWithNoPlayers() throws InterruptedException, ParseException{
        // Create tournament
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());
        boolean isTournamentCreated = dbManager.createTournament("TorneoUnico", 2024, deadline);

        // Assert tournament creation
        assertTrue(isTournamentCreated, "The tournament should be created successfully.");

        // Get tournament ID
        int tournamentId = dbManager.getTournamentID("TorneoUnico", 2024);

        // Call generateMatches when no players are registered
        dbManager.generateMatches(tournamentId);

        // Check if no matches have been created (match count should be 0)
        String queryMatches = "SELECT COUNT(*) FROM Matches WHERE tournament_id = ?";
        int matchCount = 0;
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(queryMatches)) {
            ps.setInt(1, tournamentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    matchCount = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // There should be no matches created when no players are registered
        assertEquals(0, matchCount, "No matches should be created when there are no players.");
    }
    @Test
    public void testGenerateMatchesWithFull16Players() throws InterruptedException, ParseException {
        // Create 16 users with unique phone numbers
        for (int index = 1; index <= 16; index++) {
        	final int i= index;
        	new Thread(() -> {dbManager.createUser("Jugador" + i, "Jugador " + i, "123456789" + i, "jugador" + i + "@test.com", "password123", false);}).start();
        	Thread.sleep(1000);  // Ensure user creation is processed
            dbManager.verified = true;
        }
        Thread.sleep(1000); // Ensure user creation is processed
        // Create tournament
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date deadline = new Date(sdf.parse("2024-12-31").getTime());
        boolean isTournamentCreated = dbManager.createTournament("TorneoUnico", 2024, deadline);

        // Log in users and register each user
        for (int i = 1; i <= 16; i++) {
            dbManager.login("Jugador" + i , "password123");
            dbManager.createRegistration(dbManager.getTournamentID("TorneoUnico", 2024)); // Register each player
        }

       
        // Assert that the tournament was created successfully
        assertTrue(isTournamentCreated, "The tournament should be created successfully.");

        // Get tournament ID
        int tournamentId = dbManager.getTournamentID("TorneoUnico", 2024);
        dbManager.initiateTournament(tournamentId);
        // Call generateMatches to create matches for the first round (8 matches for 16 players)
        dbManager.generateMatches(tournamentId);

        // Verify that 8 matches should be created for 16 players
        String queryMatches = "SELECT COUNT(*) FROM Matches WHERE tournament_id = ?";
        int matchCount = 0;
        try (PreparedStatement ps = dbManager.getConnection().prepareStatement(queryMatches)) {
            ps.setInt(1, tournamentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    matchCount = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // There should be 8 matches created for 16 players
        assertEquals(8, matchCount, "There should be 8 matches created in the first round for 16 players.");
    }
    

    @Test
    public void testVerifyNonexistentUser() {
        dbManager.verifyUser("nonexistentUser");
        // Ensure no exceptions are thrown and no updates occur
        assertFalse(dbManager.login("nonexistentUser", "password123"), "Login should fail for non-existent user.");
    }

    @Test
    public void testForgotPasswordNonexistentUser() {
        boolean result = dbManager.forgotPassword("nonexistentUser");
        assertFalse(result, "Forgot password should fail for non-existent user.");
    }

    @Test
    public void testForgotPasswordSuccess() {
    	new Thread(() -> {dbManager.createUser("userForgot", "User Test", "123456789", "forgot@test.com", "password123", false);}).start();;
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dbManager.verified =true;
    	boolean result = dbManager.forgotPassword("userForgot");
        assertTrue(result, "Forgot password should succeed for an existing user.");

        // Validate that the password is updated
        try {
            PreparedStatement statement = dbManager.getConnection().prepareStatement("SELECT password FROM users WHERE username = ?");
            statement.setString(1, "userForgot");
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String newPassword = rs.getString("password");
                assertNotEquals("password123", newPassword, "The password should be updated to a new value.");
            } else {
                fail("User not found in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("An exception occurred while verifying the updated password.");
        }
    }

    @Test
    public void testShowTournamentsToInitiateEmpty() {
        List<?> tournaments = dbManager.showTournamentsToInitiate();
        assertTrue(tournaments.isEmpty(), "No tournaments should be available to initiate in an empty database.");
    }

    @Test
    public void testCreateSetInvalidMatchId() {
        dbManager.createSet(-1, 1, 6, 4);  // Invalid match ID
        // Verify no sets were created
        String query = "SELECT COUNT(*) FROM Sets WHERE match_id = -1";
        try {
			ResultSet rs = dbManager.getConnection().prepareStatement(query).executeQuery();
			int setCount = rs.getInt(1);
			assertEquals(0, setCount, "No sets should be created for an invalid match ID.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    }

    @Test
    public void testCreateSetInvalidScores() {
        dbManager.createSet(1, 1, -1, 15);  // Invalid scores
        // Verify no sets were created
        String query = "SELECT COUNT(*) FROM Sets WHERE first_player_score = -1";
        try {
			ResultSet rs = dbManager.getConnection().prepareStatement(query).executeQuery();
			int setCount = rs.getInt(1);
			assertEquals(0, setCount, "No sets should be created with invalid scores.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }

    @Test
    public void testIsTournamentActiveInvalidId() {
        boolean isActive = dbManager.isTournamentActive(-1);  // Non-existent tournament ID
        assertFalse(isActive, "Tournament should not be active for an invalid ID.");
    }

    @Test
    public void testChangePasswordNonexistentUser() {
        dbManager.changePassword("nonexistentUser", "newPassword123");
        // Verify the operation does not affect the DB or throw exceptions
        boolean loginSuccess = dbManager.login("nonexistentUser", "newPassword123");
        assertFalse(loginSuccess, "Login should fail for non-existent user even after password change attempt.");
    }

    @Test
    public void testGenerateMatchesEmptyRound() {
        dbManager.generateMatches(1);  // Assuming tournament ID 1 has no players
        // Verify no matches are generated
        String query = "SELECT COUNT(*) FROM Matches WHERE tournament_id = 1";
       
        int matchCount;
		try {
			 ResultSet rs = dbManager.getConnection().prepareStatement(query).executeQuery();
			matchCount = rs.getInt(1);
			assertEquals(0, matchCount, "No matches should be created for an empty round.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    @Test
    public void testNextRoundNoCompletedMatches() {
        dbManager.createTournament("Test Tournament", 2024, Date.valueOf("2024-12-31"));
        int tournamentId = dbManager.getTournamentID("Test Tournament", 2024);
        dbManager.initiateTournament(tournamentId);
        dbManager.nextRound(tournamentId);
        // Validate round does not advance without completed matches
        String query = "SELECT actual_round FROM Tournaments WHERE id = " + tournamentId;
        try {
			ResultSet rs = dbManager.getConnection().prepareStatement(query).executeQuery();
			int actualRound = rs.getInt("actual_round");
			assertEquals(16, actualRound, "Round should not advance without completed matches.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
    }

    @Test
    public void testEndTournamentAlreadyInactive() {
        dbManager.createTournament("Inactive Tournament", 2024, Date.valueOf("2024-12-31"));
        int tournamentId = dbManager.getTournamentID("Inactive Tournament", 2024);
        dbManager.initiateTournament(tournamentId);
        dbManager.endTournament(tournamentId);
        dbManager.endTournament(tournamentId);  // Attempt to end again
        // Verify no issues occur
        boolean isActive = dbManager.isTournamentActive(tournamentId);
        assertFalse(isActive, "Tournament should remain inactive after being ended.");
    }

    @Test
    public void testCreateTournamentDuplicateNameAndYear() {
        dbManager.createTournament("Duplicate Tournament", 2024, Date.valueOf("2024-12-31"));
        boolean result = dbManager.createTournament("Duplicate Tournament", 2024, Date.valueOf("2024-12-31"));
        assertFalse(result, "Duplicate tournaments with the same name and year should not be allowed.");
    }
    
    @Test
    public void testTournamentEndPointsAssignmentSetsDifferent() {
        try {
            // Setup players and tournament
            new Thread(() -> dbManager.createUser("Player1", "Player One", "1234567890", "player1@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player2", "Player Two", "1234567891", "player2@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player3", "Player Three", "1234567892", "player3@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player4", "Player Four", "1234567893", "player4@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            dbManager.createTournament("Point Tournament", 2024, Date.valueOf("2024-12-31"));
            int tournamentId = dbManager.getTournamentID("Point Tournament", 2024);

            // Register players
            dbManager.login("Player1", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player2", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player3", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player4", "password");
            dbManager.createRegistration(tournamentId);

            // Start tournament and play through all rounds
            dbManager.initiateTournament(tournamentId);
            Map<String, Pair<Integer, Integer>> table = new HashMap<>();
            table.put("Player1", new Pair<Integer, Integer>(0,0));
            table.put("Player2", new Pair<Integer, Integer>(0,0));
            table.put("Player3", new Pair<Integer, Integer>(0,0));
            table.put("Player4", new Pair<Integer, Integer>(0,0));
            while (true) {
                dbManager.generateMatches(tournamentId);

                // Simulate matches being completed (assuming all matches are played)
                String matchQuery = "SELECT id, first_player_username, second_player_username, round FROM matches WHERE tournament_id = ? AND match_status = FALSE";
                List<Integer> matchIds = new ArrayList<>();
                List<String> firstUser = new ArrayList<>();
                List<String> secondUser = new ArrayList<>();
                List<Integer> rounds = new ArrayList<>();
                try (PreparedStatement ps = dbManager.getConnection().prepareStatement(matchQuery)) {
                    ps.setInt(1, tournamentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            matchIds.add(rs.getInt("id"));
                            firstUser.add(rs.getString("first_player_username"));
                            secondUser.add(rs.getString("second_player_username"));
                            rounds.add(rs.getInt("round"));
                        }
                    }
                }
                int i = 0;
                Map<Integer, Pair<Integer,Integer>> points = new HashMap<>();
               
                
                
                
                for (int matchId : matchIds) {
                	Match match = new Match(matchId, tournamentId, firstUser.get(i), secondUser.get(i), rounds.get(i));
                	points.put(1, new Pair<Integer, Integer>(6,4- matchId%3));
                	points.put(2, new Pair<Integer, Integer>(6,4));
                	points.put(4, new Pair<Integer, Integer>(6,4));
                	points.put(3, new Pair<Integer, Integer>(4,4+ matchId%2));
                	match.setPoints(points);
                    dbManager.updateResults(match);  // Update results after each match
                    
                	Pair<Integer, Integer> pair = table.get(firstUser.get(i));
                	int add = 4<=4+matchId%2 ? 0 : 1;
                	Pair<Integer,Integer> newPair = new Pair<Integer, Integer>(pair.getKey() + 3+ add, pair.getValue() + 22);
                	table.put(firstUser.get(i), newPair);
                	add = 4+matchId%2>4 ? 1 : 0;
                	pair = table.get(secondUser.get(i));
                	newPair = new Pair<Integer, Integer>(pair.getKey() + add, pair.getValue() + 16 - matchId%3 + matchId%2);
                	table.put(secondUser.get(i), newPair);
                	System.out.println("***************TABLA: "+table.toString());
                    i++;
                }

                dbManager.nextRound(tournamentId);

                String roundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
                int currentRound;
                try (PreparedStatement ps = dbManager.getConnection().prepareStatement(roundQuery)) {
                    ps.setInt(1, tournamentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        currentRound = rs.getInt("actual_round");
                    }
                }
                if (currentRound == 0) {
                    break;
                }
            }

            String query = "SELECT username, finalPoints FROM TournamentPlayers WHERE tournament_id = ? ORDER BY finalPoints DESC";
            List<String> results = new ArrayList<>();
            try (PreparedStatement ps = dbManager.getConnection().prepareStatement(query)) {
                ps.setInt(1, tournamentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString("username") + ":" + rs.getInt("finalPoints"));
                    }
                }
            }
            // Validate points assignment
            
            Map<String, Pair<Integer, Integer>> sortedTable = new TreeMap<>((player1, player2) -> {
                Pair<Integer, Integer> pair1 = table.get(player1);
                Pair<Integer, Integer> pair2 = table.get(player2);
                
                // Comparar por la primera clave del Pair (descendente)
                int compareKeys = Integer.compare(pair2.getKey(), pair1.getKey());
                if (compareKeys != 0) {
                    return compareKeys;
                }
                
                // Comparar por la segunda clave del Pair (descendente)
                return Integer.compare(pair2.getValue(), pair1.getValue());
            });

            // Transferir los valores del mapa original al mapa ordenado
            sortedTable.putAll(table);

            // Imprimir el mapa ordenado para verificar
            System.out.println("Tabla ordenada: " + sortedTable.toString());
            
            List<String> expectedResults = new ArrayList<>(sortedTable.keySet());
            // Assuming the highest-scoring player gets 2000 points, verify point assignments
            assertEquals(expectedResults.get(0)+":2000", results.get(0),expectedResults.get(0)+ " should have the highest points.");
            assertEquals(expectedResults.get(1)+":1500", results.get(1),expectedResults.get(1)+ " should have the second-highest points.");
            assertEquals(expectedResults.get(2)+":1000", results.get(2),expectedResults.get(2)+ " should have the third-highest points.");
            assertEquals(expectedResults.get(3)+":500", results.get(3),expectedResults.get(3)+ " should have the fourth-highest points.");

        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred during the test: " + e.getMessage());
        }
    }
    
    @Test
    public void testTournamentEndPointsAssignmentSetsSame() {
        try {
            // Setup players and tournament
            new Thread(() -> dbManager.createUser("Player1", "Player One", "1234567890", "player1@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player2", "Player Two", "1234567891", "player2@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player3", "Player Three", "1234567892", "player3@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player4", "Player Four", "1234567893", "player4@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            dbManager.createTournament("Point Tournament", 2024, Date.valueOf("2024-12-31"));
            int tournamentId = dbManager.getTournamentID("Point Tournament", 2024);

            // Register players
            dbManager.login("Player1", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player2", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player3", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player4", "password");
            dbManager.createRegistration(tournamentId);

            // Start tournament and play through all rounds
            dbManager.initiateTournament(tournamentId);
            Map<String, Pair<Integer, Integer>> table = new HashMap<>();
            table.put("Player1", new Pair<Integer, Integer>(0,0));
            table.put("Player2", new Pair<Integer, Integer>(0,0));
            table.put("Player3", new Pair<Integer, Integer>(0,0));
            table.put("Player4", new Pair<Integer, Integer>(0,0));
            while (true) {
                dbManager.generateMatches(tournamentId);

                // Simulate matches being completed (assuming all matches are played)
                String matchQuery = "SELECT id, first_player_username, second_player_username, round FROM matches WHERE tournament_id = ? AND match_status = FALSE";
                List<Integer> matchIds = new ArrayList<>();
                List<String> firstUser = new ArrayList<>();
                List<String> secondUser = new ArrayList<>();
                List<Integer> rounds = new ArrayList<>();
                try (PreparedStatement ps = dbManager.getConnection().prepareStatement(matchQuery)) {
                    ps.setInt(1, tournamentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            matchIds.add(rs.getInt("id"));
                            firstUser.add(rs.getString("first_player_username"));
                            secondUser.add(rs.getString("second_player_username"));
                            rounds.add(rs.getInt("round"));
                        }
                    }
                }
                int i = 0;
                Map<Integer, Pair<Integer,Integer>> points = new HashMap<>();
               
                
                
                
                for (int matchId : matchIds) {
                	Match match = new Match(matchId, tournamentId, firstUser.get(i), secondUser.get(i), rounds.get(i));
                	points.put(1, new Pair<Integer, Integer>(6,4- matchId%3));
                	points.put(2, new Pair<Integer, Integer>(6,4));
                	points.put(3, new Pair<Integer, Integer>(6,4));
                	match.setPoints(points);
                    dbManager.updateResults(match);  // Update results after each match
                    
                	Pair<Integer, Integer> pair = table.get(firstUser.get(i));
                	Pair<Integer,Integer> newPair = new Pair<Integer, Integer>(pair.getKey() + 3, pair.getValue() + 18);
                	table.put(firstUser.get(i), newPair);

                	pair = table.get(secondUser.get(i));
                	newPair = new Pair<Integer, Integer>(pair.getKey(), pair.getValue() + 12 - matchId%3);
                	table.put(secondUser.get(i), newPair);
                	System.out.println("***************TABLA: "+table.toString());
                    i++;
                }

                dbManager.nextRound(tournamentId);

                String roundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
                int currentRound;
                try (PreparedStatement ps = dbManager.getConnection().prepareStatement(roundQuery)) {
                    ps.setInt(1, tournamentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        currentRound = rs.getInt("actual_round");
                    }
                }
                if (currentRound == 0) {
                    break;
                }
            }

            String query = "SELECT username, finalPoints FROM TournamentPlayers WHERE tournament_id = ? ORDER BY finalPoints DESC";
            List<String> results = new ArrayList<>();
            try (PreparedStatement ps = dbManager.getConnection().prepareStatement(query)) {
                ps.setInt(1, tournamentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString("username") + ":" + rs.getInt("finalPoints"));
                    }
                }
            }
            // Validate points assignment
            
            Map<String, Pair<Integer, Integer>> sortedTable = new TreeMap<>((player1, player2) -> {
                Pair<Integer, Integer> pair1 = table.get(player1);
                Pair<Integer, Integer> pair2 = table.get(player2);
                
                // Comparar por la primera clave del Pair (descendente)
                int compareKeys = Integer.compare(pair2.getKey(), pair1.getKey());
                if (compareKeys != 0) {
                    return compareKeys;
                }
                
                // Comparar por la segunda clave del Pair (descendente)
                return Integer.compare(pair2.getValue(), pair1.getValue());
            });

            // Transferir los valores del mapa original al mapa ordenado
            sortedTable.putAll(table);

            // Imprimir el mapa ordenado para verificar
            System.out.println("Tabla ordenada: " + sortedTable.toString());
            
            List<String> expectedResults = new ArrayList<>(sortedTable.keySet());
            // Assuming the highest-scoring player gets 2000 points, verify point assignments
            assertEquals(expectedResults.get(0)+":2000", results.get(0),expectedResults.get(0)+ " should have the highest points.");
            assertEquals(expectedResults.get(1)+":1500", results.get(1),expectedResults.get(1)+ " should have the second-highest points.");
            assertEquals(expectedResults.get(2)+":1000", results.get(2),expectedResults.get(2)+ " should have the third-highest points.");
            assertEquals(expectedResults.get(3)+":500", results.get(3),expectedResults.get(3)+ " should have the fourth-highest points.");

        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred during the test: " + e.getMessage());
        }
    }
    
    @Test
    public void testTournamentEndPointsAssignmentSetsSameGamesSame() {
        try {
            // Setup players and tournament
            new Thread(() -> dbManager.createUser("Player1", "Player One", "1234567890", "player1@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player2", "Player Two", "1234567891", "player2@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player3", "Player Three", "1234567892", "player3@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            new Thread(() -> dbManager.createUser("Player4", "Player Four", "1234567893", "player4@test.com", "password", false)).start();
            Thread.sleep(1000);
            dbManager.verified = true;

            dbManager.createTournament("Point Tournament", 2024, Date.valueOf("2024-12-31"));
            int tournamentId = dbManager.getTournamentID("Point Tournament", 2024);

            // Register players
            dbManager.login("Player1", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player2", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player3", "password");
            dbManager.createRegistration(tournamentId);

            dbManager.login("Player4", "password");
            dbManager.createRegistration(tournamentId);

            // Start tournament and play through all rounds
            dbManager.initiateTournament(tournamentId);
            Map<String, Pair<Integer, Integer>> table = new HashMap<>();
            table.put("Player1", new Pair<Integer, Integer>(0,0));
            table.put("Player2", new Pair<Integer, Integer>(0,0));
            table.put("Player3", new Pair<Integer, Integer>(0,0));
            table.put("Player4", new Pair<Integer, Integer>(0,0));
            while (true) {
                dbManager.generateMatches(tournamentId);

                // Simulate matches being completed (assuming all matches are played)
                String matchQuery = "SELECT id, first_player_username, second_player_username, round FROM matches WHERE tournament_id = ? AND match_status = FALSE";
                List<Integer> matchIds = new ArrayList<>();
                List<String> firstUser = new ArrayList<>();
                List<String> secondUser = new ArrayList<>();
                List<Integer> rounds = new ArrayList<>();
                try (PreparedStatement ps = dbManager.getConnection().prepareStatement(matchQuery)) {
                    ps.setInt(1, tournamentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            matchIds.add(rs.getInt("id"));
                            firstUser.add(rs.getString("first_player_username"));
                            secondUser.add(rs.getString("second_player_username"));
                            rounds.add(rs.getInt("round"));
                        }
                    }
                }
                int i = 0;
                Map<Integer, Pair<Integer,Integer>> points = new HashMap<>();
               
                
                
                
                for (int matchId : matchIds) {
                	Match match = new Match(matchId, tournamentId, firstUser.get(i), secondUser.get(i), rounds.get(i));
                	points.put(1, new Pair<Integer, Integer>(6,4));
                	points.put(2, new Pair<Integer, Integer>(6,4));
                	points.put(3, new Pair<Integer, Integer>(6,4));
                	match.setPoints(points);
                    dbManager.updateResults(match);  // Update results after each match
                    
                	Pair<Integer, Integer> pair = table.get(firstUser.get(i));
                	Pair<Integer,Integer> newPair = new Pair<Integer, Integer>(pair.getKey() + 3, pair.getValue() + 18);
                	table.put(firstUser.get(i), newPair);

                	pair = table.get(secondUser.get(i));
                	newPair = new Pair<Integer, Integer>(pair.getKey(), pair.getValue() + 12);
                	table.put(secondUser.get(i), newPair);
                	System.out.println("***************TABLA: "+table.toString());
                    i++;
                }

                dbManager.nextRound(tournamentId);

                String roundQuery = "SELECT actual_round FROM Tournaments WHERE id = ?";
                int currentRound;
                try (PreparedStatement ps = dbManager.getConnection().prepareStatement(roundQuery)) {
                    ps.setInt(1, tournamentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        currentRound = rs.getInt("actual_round");
                    }
                }
                if (currentRound == 0) {
                    break;
                }
            }

            String query = "SELECT username, finalPoints FROM TournamentPlayers WHERE tournament_id = ? ORDER BY finalPoints DESC";
            List<String> results = new ArrayList<>();
            try (PreparedStatement ps = dbManager.getConnection().prepareStatement(query)) {
                ps.setInt(1, tournamentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        results.add(rs.getString("username") + ":" + rs.getInt("finalPoints"));
                    }
                }
            }
            // Validate points assignment
            
            Map<String, Pair<Integer, Integer>> sortedTable = new TreeMap<>((player1, player2) -> {
                Pair<Integer, Integer> pair1 = table.get(player1);
                Pair<Integer, Integer> pair2 = table.get(player2);
                
                // Comparar por la primera clave del Pair (descendente)
                int compareKeys = Integer.compare(pair2.getKey(), pair1.getKey());
                if (compareKeys != 0) {
                    return compareKeys;
                }
                
                // Comparar por la segunda clave del Pair (descendente)
                return Integer.compare(pair2.getValue(), pair1.getValue());
            });

            // Transferir los valores del mapa original al mapa ordenado
            sortedTable.putAll(table);

            // Imprimir el mapa ordenado para verificar
            System.out.println("Tabla ordenada: " + sortedTable.toString());
            
            List<String> expectedResults = new ArrayList<>(sortedTable.keySet());
            // Assuming the highest-scoring player gets 2000 points, verify point assignments
            assertEquals(expectedResults.get(0)+":2000", results.get(0),expectedResults.get(0)+ " should have the highest points.");
            assertEquals(expectedResults.get(1)+":1500", results.get(1),expectedResults.get(1)+ " should have the second-highest points.");
           
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred during the test: " + e.getMessage());
        }
    }


}

    