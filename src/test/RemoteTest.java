
package test;

import commander.IComponent;
import commander.IEventListener;
import commander.IManager;
import commander.ManagerFactory;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import implement.TemperatureComponent;
import org.json.JSONObject;

/**
 * @author julian
 */
public class RemoteTest implements IEventListener {

   private static final Logger LOGGER = Logger.getLogger(RemoteTest.class.getName());

   private IComponent component;

   public RemoteTest(IComponent component) {
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
         RemoteTest test = new RemoteTest(comp);
         mgr.registerListener(test);

         comp.start();

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
