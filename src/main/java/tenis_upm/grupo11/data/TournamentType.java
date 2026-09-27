package tenis_upm.grupo11.data;

/**
 * Enum representing the types of tournaments.
 */
public enum TournamentType {
    TORNEO_DE_PRIMAVERA, TORNEO_DE_VERANO, TORNEO_DE_OTOÑO, TORNEO_DE_INVIERNO;

    /**
     * Converts the enum value to a formatted string.
     *
     * @return the formatted string representation of the enum.
     */
    public String toFormattedString() {
        switch (this) {
            case TORNEO_DE_PRIMAVERA:
                return "Torneo de Primavera";
            case TORNEO_DE_VERANO:
                return "Torneo de Verano";
            case TORNEO_DE_OTOÑO:
                return "Torneo de Otoño";
            case TORNEO_DE_INVIERNO:
                return "Torneo de Invierno";
            default:
                throw new IllegalArgumentException("Unknown tournament type: " + this);
        }
    }
}
