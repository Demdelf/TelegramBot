import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;

public class Main {
    static Connection connection;
    static Statement statement;
    static ResultSet resultSet;

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/cakes";

        String username = "root";
        String password = "qwerty";
        System.out.println("Connecting...");

        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        Bot bot = new Bot();
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
