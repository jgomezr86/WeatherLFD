package implement;

/* Commander */
import commander.*;

/* JSON */
import org.json.JSONException;
import org.json.JSONObject;

/* Java */
import java.util.concurrent.TimeUnit;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.TimerTask;
import java.util.Timer;
import java.util.List;
import java.net.URL;

/**
 * @author Anton Stasyuk
 * @author Javier Gómez Ros
 * @author Sergio Abad Martinez
 */
public class TemperatureComponent
   implements IInitManager, IComponent, IEventListener {

   /* Nombre de componente y sus comandos */
   private final String TOKEN = "46946318Y";
   private final String CMD_NAME = "temperature";
   private final String CMD_TIMER = CMD_NAME + ".timer";
   private final String CMD_ENABLE = CMD_NAME + ".enable";
   private final String CMD_DISABLE = CMD_NAME + ".disable";
   private final String STORAGE_KEY = "temperature-component";

   /* Ajustes predeterminados (guardados en `storage` component) */
   private static final JSONObject DEFAULTS = new JSONObject()
      .put("temperature", Float.MIN_VALUE)
      .put("period", 15);

   /* Recursos */
   private IManager manager;
   private Timer timer;

   /* Getters --------------------------------------------------------------------------------------------------------*/

   @Override
   public String getName() {
      return CMD_NAME;
   }

   @Override
   public String[] getCommands() {
      return new String[]{
         CMD_DISABLE,
         CMD_ENABLE,
         CMD_TIMER
      };
   }

   /* Setters --------------------------------------------------------------------------------------------------------*/

   @Override
   public void setManager(IManager m) {
      this.manager = m;
   }

   /* Handlers -------------------------------------------------------------------------------------------------------*/

   @Override
   public void handleEvent(JSONObject jo) {

      if (!"twitter.status".equals(jo.getString("source") + '.' + jo.getString("type"))) return;

      JSONObject status = jo.getJSONObject("status");
      List<Object> hashtags = status
         .getJSONArray("hashtags")
         .toList();

      for (Object hashtag : hashtags)
         if (hashtag.equals("QueTemperaturaLFD")) {
            //TODO: enviar mensaje
            /*manager.execute(new JSONObject()
               .put("token", TOKEN)
               .put("command", "twitter.post")
               .put("status_id", status.getLong("status_id"))
               .put("status", "@" + status.getString("user") + "Que la fuerza te acompañe mi joven padawan!!!")
            );*/
            break;
         }
   }

   @Override
   public JSONObject execute(JSONObject jo) {

      switch (jo.getString("command")) {

         case CMD_DISABLE:
            boolean enabled = stop();
            return response(enabled,
               enabled
                  ? "Temporizador deshabilitado"
                  : "Tarea no esta corriendo"
            );

         case CMD_ENABLE:
            try {
               start();
               return response(true, "Temporizador habilitado");
            }
            catch (Exception e) {
               return response(false, "Ha ocurrido un error al iniciar el temporizador");
            }

         case CMD_TIMER:
            try {
               if (!setStoredData(DEFAULTS.put("period", jo.getInt("time"))))
                  return response(false, "Ha ocurrido un error");

               start();
               return response(true, "Tiempo modificado con éxito");
            }
            catch (JSONException e) {
               return response(false, "Elemento con clave `time` no existe o tiene formato incorrecto");
            }
      }

      return response(false, "Comando no existe");
   }

   /* Helpers --------------------------------------------------------------------------------------------------------*/

   /**
    * Inicializa el temporizador para ejecutar la tarea {@see WeatherTask} periódicamente
    */
   private void start() {
      stop();

      (this.timer = new Timer())
         .schedule(
            new WeatherTask(), // Tarea que se ejecuta
            0, // Tiempo que espera en la primera iteración
            TimeUnit.MINUTES.toMillis(
               getStoredData().getInt("period") // Tiempo a esperar entre las iteraciones
            ));
   }

   /**
    * Para el timer que ejecuta la tarea periódicamente
    * @return {@code false} si estaba parado, {@code true} en caso contrario
    */
   private boolean stop() {

      if (this.timer == null) return false;
      this.timer.cancel();
      return true;
   }

   /**
    * @return JSONObject que tiene la última temperatura y el periodo de ejecución de la tarea. En caso que no exista
    * devuelve los parámetros predeterminados
    */
   private JSONObject getStoredData() {

      return manager.execute(new JSONObject()
         .put("command", "storage.loadorsave")
         .put("key", STORAGE_KEY)
         .put("data", DEFAULTS)
      );
   }

   /**
    * @param data El objeto para guardar en storage
    * @return {@code true} si se ha ejecutado con éxito,{@code false} en caso contrario
    */
   private boolean setStoredData(JSONObject data) {

      return manager.execute(new JSONObject()
         .put("command", "storage.save")
         .put("key", STORAGE_KEY)
         .put("data", data)
      )
         .getBoolean("success");
   }

   /**
    * @return La temperatura actual en las coordenadas especificadas en la URL del API
    * @throws Exception En caso de que no se ha podido consultar la temperatura
    */
   private JSONObject getCurrent() throws Exception {
      int attempt = 0;
      do {
         try {
            ++attempt;
            URL url = new URL("https://api.darksky.net/forecast/" + // URL base del API
               "1a6121be33f01c8c16aee7cfd9cfd50c" + // Token de usuario de prueba
               "/41.548630,2.107440" + // Coordenadas de Sabadell
               // Ajustes
               "?units=si" + // Unidades
               "&lang=ca" + // Idioma
               "&exclude=minutely,hourly,daily,alerts,flags" // Información que no utilizamos
            );

            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {

               StringBuilder response = new StringBuilder();
               String line;

               while (null != (line = br.readLine()))
                  response.append(line);

               return new JSONObject(response.toString())
                  .getJSONObject("currently");
            }
         }
         catch (Exception ignore) {
            Thread.sleep(3000);
         }
      } while (attempt <= 2);
      throw new Exception();
   }

   private JSONObject renderTwitterMessage(JSONObject data) {
      if (data == null) data = getStoredData();
      return null;
   }

   /**
    * Genera una respuesta del componente
    * @param success Tipo de la respuesta (positiva {@code true}, negativa {@code false})
    * @param message Texto descriptivo
    * @return {"success":true, "message": "..."} o {"success":false, "error": "..."}
    */
   private JSONObject response(boolean success, String message) {
      return new JSONObject()
         .put(success ? "data" : "error", message)
         .put("success", success);
   }

   /* Tarea del temporizador -----------------------------------------------------------------------------------------*/
   private class WeatherTask extends TimerTask {

      @Override
      public void run() {
         try {
            JSONObject storage = getStoredData();
            JSONObject current = getCurrent();
         }
         catch (Exception ignore) {
         }

         //TODO: Leer la temperatura guardada en storage
         //TODO: Leer los datos de la página web, si han cambiado ->
         //TODO: Enviar un evento con el twit de que ha cambiado la temperatura
      }
   }
}
