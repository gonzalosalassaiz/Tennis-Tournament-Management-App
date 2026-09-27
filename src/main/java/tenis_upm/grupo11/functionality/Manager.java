package tenis_upm.grupo11.functionality;

public class Manager {
    private static Manager instance;
    private final DBManager db;

    private Manager() {
        db = new DBManager();
    }

    /**
     * Singleton proper function to get the only instance of this class
     *
     * @return the only instance of this class
     */
    public static Manager getInstance() {
        if(instance == null) instance = new Manager();
        return instance;
    }

	public DBManager getDb() {
		return db;
	}
}
