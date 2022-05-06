package com.example.exchangeratestelegrambot.botapi;

import com.aspose.pdf.HtmlLoadOptions;
import com.example.exchangeratestelegrambot.botconfig.TelegramBotConfiguration;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class ExchangeRatesTelegramBot extends TelegramWebhookBot {

    private static final String URL = "https://select.by/minsk/kurs";
    private static final String OUTPUT_PDF_FILE = "currency.pdf";
    private static final String OUTPUT_PNG_FILE = "currency_image.png";

    private final TelegramBotConfiguration telegramBotConfiguration;
    private final ArrayList<KeyboardRow> keyboardButton = new ArrayList<>();

    public ExchangeRatesTelegramBot(TelegramBotConfiguration telegramBotConfiguration) {
        this.telegramBotConfiguration = telegramBotConfiguration;

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("/get_text");
        keyboardRow.add("/get_image");
        keyboardRow.add("/get_document");

        this.keyboardButton.add(keyboardRow);
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfiguration.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfiguration.getBotToken();
    }

    @Override
    public String getBotPath() {
        return telegramBotConfiguration.getWebHookPath();
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        System.out.println(telegramBotConfiguration.getWebHookPath());
        System.out.println(telegramBotConfiguration.getBotToken());
        System.out.println(telegramBotConfiguration.getBotUserName());

        Message message = update.getMessage();

        try {
            messageHandler(message);
        } catch (TelegramApiException e) {
            System.out.println("Exception in message handler!");
            //e.printStackTrace();
        }
        return null;
    }

    private synchronized void messageHandler(Message message) throws TelegramApiException {
        if (message != null && message.hasText()) {

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardButton);
            replyKeyboardMarkup.setOneTimeKeyboard(false);
            replyKeyboardMarkup.setResizeKeyboard(true);

            // handle regular text message
            if (!message.hasEntities()) {

                execute(SendMessage.builder()
                        .chatId(String.valueOf(message.getChatId()))
                        .text("You enter this: \n" + message.getText())
                        .replyMarkup(replyKeyboardMarkup)
                        .build());
            }
            // handle command
            else {
                Optional<MessageEntity> commandEntity =
                        message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
                if (commandEntity.isPresent()) {
                    String command =
                            message
                                    .getText()
                                    .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                    File sendingFile;

                    switch (command) {
                        case "/start" -> execute(SendMessage.builder()
                                .chatId(String.valueOf(message.getChatId()))
                                .text("Use the commands below to use the functionality of the bot")
                                .replyMarkup(replyKeyboardMarkup)
                                .build());
                        case "/get_text" -> {

                            // status
                            execute(SendChatAction.builder()
                                    .chatId(String.valueOf(message.getChatId()))
                                    .action("typing")
                                    .build());
                            execute(SendMessage.builder()
                                    .chatId(String.valueOf(message.getChatId()))
                                    .text(getCurrencyText())
                                    .replyMarkup(replyKeyboardMarkup)
                                    .build());
                        }
                        case "/get_document" -> {
                            // send document

                            // status
                            execute(SendChatAction.builder()
                                    .chatId(String.valueOf(message.getChatId()))
                                    .action("upload_document")
                                    .build());
                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setChatId(message.getChatId().toString());
                            sendDocument.setCaption("caption");
                            sendingFile = getCurrencyPdf()/*getCurrencyPDF()*/;
                            if (sendingFile != null) {
                                sendDocument.setDocument(new InputFile(sendingFile));
                                execute(sendDocument);
                            }
                        }
                        case "/get_image" -> {
                            // send photo

                            // status
                            execute(SendChatAction.builder()
                                    .chatId(String.valueOf(message.getChatId()))
                                    .action("upload_photo")
                                    .build());
                            sendingFile = getCurrencyPNG();
                            if (sendingFile != null) {
                                SendPhoto sendPhoto = new SendPhoto();
                                sendPhoto.setPhoto(new InputFile(sendingFile));
                                sendPhoto.setChatId(message.getChatId().toString());
                                execute(sendPhoto);
                            }
                        }
                    }
                }
            }
        }
    }


    private String getCurrencyText() {
        try {
            Document doc = getDocument();

            StringBuilder result = new StringBuilder();
            // name
            result.append(doc.getElementsByClass("py-3 pl-3 m-0").get(1).text());
            result.append("\n\n");
            // get table body
            Element currencyTable = doc.getElementsByClass("table table-sm table-borderless").get(1);
            // table column names
            Elements ths = currencyTable.select("th");

            ArrayList<String> columnsName = new ArrayList<>();
            for (int i = 0; i < ths.size(); i++)
                if (!ths.eq(i).text().isBlank())
                    columnsName.add(ths.eq(i).text());

            // table elements
            Elements tds = currencyTable.select("td");
            for (int i = 0; i < 25; i += 5) {
                for (int j = i; j < i + 5; j++) {
                    if (j == i) {
                        result.append(tds.eq(j).text())
                                .append(" ");
                    } else if (j == i + 1) {
                        result.append(tds.eq(j).text())
                                .append("\n");
                    } else {
                        result.append(columnsName.get((j % 5) - 2))
                                .append(": ");
                        result.append(tds.eq(j).text())
                                .append(" ");
                    }
                }
                result.append("\n\n");
            }

            return result.toString();

        } catch (Exception e) {
            System.out.println("Exception in creation text answer! " + e.getCause());
            e.printStackTrace();
            return "failed connection with " + URL;
        }
    }

    private Document getDocument() throws Exception {

        Document doc = null;
        Connection.Response response;
        response = Jsoup.connect(URL)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .referrer("https://www.google.com")
                .ignoreHttpErrors(true)
                .execute();
        System.out.println("response status code: " + response.statusCode());


        int statusCode = response.statusCode();
        if (statusCode == 200) {
            doc = Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .referrer("https://www.google.com")
                    .get();


        }

        System.out.println("site successfully parsed");

        return doc;
    }

    private String createCurrencyHtmlTable() throws Exception {
        Document doc = getDocument();

        StringBuilder buf = new StringBuilder();

        // get table body
        Element currencyTable = doc.getElementsByClass("table table-sm table-borderless").get(1);

        // table column names
        Elements ths = currencyTable.select("th");

        // name
        buf.append(doc.getElementsByClass("py-3 pl-3 m-0").get(1).text());

        buf.append("\n<html>" +
                "<body>" +
                "<table style='color:black;font-family: \"Comic Sans MS\", bold, sans-serif; font-size: 20px;'>" +
                "<tr>");
        buf.append("<th>" + "Валюта" + "</th>");

        for (Element el : ths)
            if (!el.text().isBlank())
                buf.append("<th>")
                        .append(el.text())
                        .append("</th>");
        buf.append("</tr>");

        // table elements
        Elements tds = currencyTable.select("td");
        for (int i = 0; i < 25; i += 5) {
            buf.append("<tr>");
            for (int j = i; j < i + 5; j++) {
                if (j == i) {
                    buf.append("<td>")
                            .append(tds.eq(j)
                                    .text()).append(" ");

                } else if (j == i + 1) {
                    buf.append(tds.eq(j).text());
                    buf.append("</td>");
                } else {
                    buf.append("<td>");
                    buf.append(tds.eq(j).text());
                    buf.append("</td>");
                }
            }
            buf.append("</tr>");
        }

        buf.append("</table>" +
                "</body>" +
                "</html>");

        return buf.toString();
    }

    private File getCurrencyPNG() {
        try {
            HtmlImageGenerator hig = new HtmlImageGenerator();

            File savingFile = new File(OUTPUT_PNG_FILE);

            hig.loadHtml(createCurrencyHtmlTable());
            hig.saveAsImage(savingFile);

            System.out.println("PNG successfully created");

            return savingFile;
        } catch (Exception e) {
            System.out.println("Exception in creation PNG file!");
            e.printStackTrace();
            return null;
        }
    }

    private File getCurrencyPdf() {
        try {
            // Create HTML load options
            HtmlLoadOptions htmloptions = new HtmlLoadOptions();
            // Get HTML
            String html = createCurrencyHtmlTable();
            // Input stream from String
            InputStream inputStream = new ByteArrayInputStream(html.getBytes());
            // Load HTML file
            com.aspose.pdf.Document doc = new com.aspose.pdf.Document(inputStream, htmloptions);
            // Close input stream
            inputStream.close();
            // Convert HTML file to PDF
            doc.save(OUTPUT_PDF_FILE);

            return new File(OUTPUT_PDF_FILE);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
