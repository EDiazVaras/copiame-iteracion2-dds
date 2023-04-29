package ar.utn.dds.copiame;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class CopiameBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message.hasDocument()) {
            Document document = message.getDocument();
            if (document.getMimeType().equals("application/zip")) {
                try {
                    // Obtiene el archivo
                    GetFile getFile = new GetFile();
                    getFile.setFileId(message.getDocument().getFileId());
                    org.telegram.telegrambots.meta.api.objects.File file =
                            execute(getFile);
                    java.io.File downloadedFile = downloadFile(file);
                    // Descomprime los archivos en un directorio
                    String destDirectory = "C:\\Users\\Ezequiel\\Desktop\\copiame2\\directorioTemporal\\" +
                            message.getDocument().getFileId();
                    UnzipUtility.unzip(downloadedFile, destDirectory );
                    // Procesa al lote
                    Lote lote = new Lote(destDirectory);
                    lote.validar();
                    lote.cargar();
                    float umbral = 0.5f;
                    AnalsisDeCopia analisis = new AnalsisDeCopia(umbral, lote);
                    ResultadoLote resultado = analisis.procesar();

                    // Genera la salida y manda el mensaje
                    String se_copiaron = "";
                    for (ParDocumentos par : resultado.getPosiblesCopias()) {
                        se_copiaron += par.getDocumento1().getAutor() + " " +
                                par.getDocumento2().getAutor() + "\n";
                    }
                    // Envia el mensaje al usuario
                    SendMessage responseMsg = new SendMessage();
                    responseMsg.setChatId(message.getChatId());
                    if(se_copiaron.isBlank()) {
                        responseMsg.setText("No se copió nadie");
                    } else {
                        responseMsg.setText("Se copiaron: \n" + se_copiaron);
                    }
                    execute(responseMsg);
                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
// Se devuelve el nombre que dimos al bot al crearlo con el BotFather
        return System.getenv("NOMBRE_BOT");

    }
    @Override
    public String getBotToken() {
// Se devuelve el token que nos generó el BotFather de nuestro bot
        return System.getenv("TOKEN_BOT");


    }
    public static void main(String[] args)
            throws TelegramApiException {
// Se crea un nuevo Bot API
        final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
// Se registra el bot
            telegramBotsApi.registerBot(new CopiameBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
