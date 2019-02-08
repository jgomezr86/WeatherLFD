package implement;

/* Commander */
import commander.IEventListener;
import commander.IInitManager;
import commander.IComponent;
import commander.IManager;

/* JSON */
import org.json.JSONObject;

/* Java */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.TimerTask;
import java.util.Timer;

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
   private final String API_URL = "https://api.darksky.net/forecast/1a6121be33f01c8c16aee7cfd9cfd50c/41.548630,2.107440?units=si&lang=es&exclude=minutely,hourly,daily,alerts,flags";

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

      throw new RuntimeException("Not supported yet");
      //TODO: Detectar si hashtag del comando
      // – Consultar temeperatura del API
      // – Responser con twit
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
            //TODO guardar el número de la durada de iteración
            //
            return null;
      }

      return response(false, "Comando no existe");
   }

   /* Helpers --------------------------------------------------------------------------------------------------------*/

   private void start() {
      stop();
      int period = 0;

      //TODO: Sergio ->
      // – Cargar ajustes (storage), asignar a period
      // – Temperature = Float.MIN_VALUE (storage)
      // (tener en cuenta los posibles errores)

      (this.timer = new Timer())
         .schedule(new WeatherTask(), 0, TimeUnit.MINUTES.toMillis(period));
   }

   private boolean stop() {

      if (this.timer == null) return false;
      this.timer.cancel();
      return true;
   }

   private JSONObject getStoredData() {

      //TODO: Javier – leer, si no existe guardar los datos predeterminados (DEFAULTS), y devolver leído/guardado
      return null;
   }

   /**
    * @return La temperatura actual
    * @throws InterruptedException En caso de que no se ha podido consultar la temperatura
    */
   private int getCurrentTemperature() throws InterruptedException {
      try {
         URL url = new URL(API_URL);

         try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {

            StringBuilder response = new StringBuilder();
            String line;

            while (null != (line = br.readLine()))
               response.append(line);

            return new JSONObject(response.toString())
               .getJSONObject("currently")
               .getInt("temperature");
         }
      }
      catch (Exception ignore) {
      }
      throw new InterruptedException();
   }

   private JSONObject response(boolean success, String message) {
      return new JSONObject()
         .put(success ? "data" : "error", message)
         .put("success", success);
   }

   /* Tarea del temporizador -----------------------------------------------------------------------------------------*/
   private class WeatherTask extends TimerTask {

      @Override
      public void run() {
         //TODO: Sergio – Leer la temperatura guardada en storage
         //TODO: Anton – Leer los datos de la página web, si han cambiado ->
         //TODO: Javier – Enviar un evento con el twit de que ha cambiado la temperatura
      }
   }
}
