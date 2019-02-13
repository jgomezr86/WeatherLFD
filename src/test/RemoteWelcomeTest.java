
package test;

import commander.IComponent;
import commander.IEventListener;
import commander.IManager;
import commander.ManagerFactory;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import implement.TemperatureComponent;
import implement.WelcomeComponent;
import org.json.JSONObject;

/**
 * @author julian
 */
public class RemoteWelcomeTest implements IEventListener {

   private static final Logger LOGGER = Logger.getLogger(RemoteWelcomeTest.class.getName());

   private IComponent component;

   public RemoteWelcomeTest(IComponent component) {
      this.component = component;
   }

   @Override
   public void handleEvent(JSONObject jo) {
      // send the events coming from 9001 to my component
      ((IEventListener) this.component).handleEvent(jo);
   }

   private static void sleep(long millis) {
      try {
         Thread.sleep(millis);
      }
      catch (InterruptedException ex) {
         LOGGER.log(Level.INFO, "interrupted!");
      }
   }

   public static void main(String[] args) {

      // create component (only for testing)
      TemperatureComponent comp = new TemperatureComponent();

      try (IManager mgr = ManagerFactory.getManager("146.255.96.104")) {

         // init
         comp.setManager(mgr);
         RemoteWelcomeTest test = new RemoteWelcomeTest(comp);
         mgr.registerListener(test);

         comp.execute(new JSONObject()
            .put("command", "storage.remove")
            .put("key", "temperature-component")
         );

         // wait for event, some time
         sleep(TimeUnit.MINUTES.toMillis(2));

         // done
         mgr.unregisterListener(test);

      }
      catch (Exception ex) {
         LOGGER.log(Level.SEVERE, "test failure", ex);
      }
   }
}
