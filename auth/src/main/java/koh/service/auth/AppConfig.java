package koh.service.auth;

public class AppConfig {
    public static final String MARIADB_USER = System.getenv("MARIADB_USER");
    public static final String MARIADB_PASSWORD = System.getenv("MARIADB_PASSWORD");
    public static final String MARIADB_HOST = System.getenv("MARIADB_HOST");
    public static final String MARIADB_DATABASE = System.getenv("MARIADB_DATABASE");
    public static final String MARIADB_PORT = System.getenv("MARIADB_PORT");

    public static final String KAFKA_HOST = System.getenv("KAFKA_HOST");
    public static final String KAFKA_PORT = System.getenv("KAFKA_PORT");
    public static final String KAFKA_GROUP = System.getenv("KAFKA_GROUP");

    public static final String APP_PRIVATE_KEY_PATH = System.getenv("APP_PRIVATE_KEY_PATH");
    public static final String APP_PUBLIC_KEY_PATH = System.getenv("APP_PUBLIC_KEY_PATH");
}
