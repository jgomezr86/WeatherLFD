package implement;

/* Commander */
import commander.IEventListener;
import commander.IInitManager;
import commander.IComponent;
import commander.IManager;

/* JSON */
import org.json.JSONObject;

/* Java */
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
   private static final String TOKEN = "46946318Y";
   private static final String CMD_NAME = "temperature";
   private static final String CMD_TIMER = CMD_NAME + ".timer";
   private static final String CMD_ENABLE = CMD_NAME + ".enable";
   private static final String CMD_DISABLE = CMD_NAME + ".disable";
   private static final String STORAGE_KEY = "temperature-component";

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

   private JSONObject getData() {

      //TODO: Javier – leer, si no existe guardar los datos predeterminados (DEFAULTS), y devolver leído/guardado
      return null;
   }

   /* Setters --------------------------------------------------------------------------------------------------------*/

   @Override
   public void setManager(IManager m) {
      this.manager = m;
   }

   /* Handlers -------------------------------------------------------------------------------------------------------*/

   @Override
   public void handleEvent(JSONObject jo) {
      //TODO:
   }

   @Override
   public JSONObject execute(JSONObject jo) {

      switch (jo.getString("command")) {

         case CMD_DISABLE:
            boolean wasEnabled = stop();
            return response(wasEnabled,
               wasEnabled
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
