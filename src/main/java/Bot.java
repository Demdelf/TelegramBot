
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        //** We check if the update has a message and the message has text
        //update.hasMessage() && update.getMessage().hasText()
        update.getUpdateId();
        SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(update.getMessage().getChatId());
        message.setText(readMessage(update.getMessage().getText()));

            try {
                execute(message); // Call method to send the message
            }  catch (TelegramApiRequestException e){
                message.setText("Слишком много запросов. Сработала автоблокировка Telegram. Попробуйте через 2 минуты.");
                try {
                    execute(message);
                } catch (TelegramApiException ex) {
                    ex.printStackTrace();
                }
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

    }

    @Override
    public String getBotUsername() {
        return "@CakeOrders_bot";
    }

    @Override
    public String getBotToken() {
        return "1041201168:AAFG2KV5DH8-OFECKghuTY2_to1FC-ZWpAo";
    }

    public String readMessage(String message){
        String answer = "null";
        Boolean isFullQuery = message.equals("!проблема");
        if(message.contains("проблема") && !isFullQuery){
            answer = "Проблемный заказ добавлен в базу";
            String[] m = message.split(" проблема ");
            addOrder(Integer.parseInt(m[0]), m[1]);

        }
        if (isFullQuery){
            answer = "Полный список проблемных заказов: \n" + getAllOrders();

        }
        if (message.contains("решено") || message.contains("решена")){

            String[] m = message.split(" ");
            answer = "Проблемный заказ № " + m[0] + " удален из базы ";
            remove(Integer.parseInt(m[0]));
        }
        return answer;
    }

    public void addOrder(int id, String description){
        String sql = "INSERT INTO orders(id, description) VALUES(?,?)";

        try {
            PreparedStatement statement = Main.connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setString(2, description);
            statement.executeUpdate();
            System.out.println("добавил успешно");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("не добавить!!!");
        }

    }

    public String getAllOrders(){
        ArrayList<Order> list = new ArrayList<>();


        try {
            PreparedStatement statement = null;
            String query = "select * FROM orders";
            statement = Main.connection.prepareStatement(query);
            Main.resultSet = statement.executeQuery();
            while (Main.resultSet.next()){
                list.add(new Order(Main.resultSet.getInt("id"), Main.resultSet.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        for (Order order: list
             ) {
            if(sb.length() != 0){
                sb.append("\n");
            }
            sb.append(order.getId() + " проблема " + order.getDescription());
        }
        return sb.toString();
    }

    public void remove(int id){
        try {
            PreparedStatement statement = null;
                    statement = Main.connection.prepareStatement("DELETE FROM orders WHERE id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("удалил успешно");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ошибка удаления!!!");
        }
    }
}
