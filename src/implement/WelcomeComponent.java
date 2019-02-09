package implement;

/* Commander */
import commander.*;

/* JSON */
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * @author Anton Stasyuk
 */
public class WelcomeComponent
   implements IInitManager, IComponent, IEventListener {

   /* Component names & constants */
   private static final String TOKEN = "X6214190G";
   private static final String CMD_NAME = "welcome";
   private static final String CMD_CLEAR = CMD_NAME + ".clear";
   private static final String CMD_HASUSER = CMD_NAME + ".hasuser";
   private static final String STORAGE_USERS_KEY = CMD_NAME + "-users";

   /* Default storage data */
   private static final JSONObject DEFAULT_STORED = new JSONObject()
      .put("users", new JSONArray());

   /* Resources */
   private JSONArray usersIds;
   private IManager mgr;

   /* Getters --------------------------------------------------------------------------------------------------------*/

   @Override
   public String getName() {
      return CMD_NAME;
   }

   @Override
   public String[] getCommands() {
      return new String[]{CMD_HASUSER};
   }

   /* Handlers -------------------------------------------------------------------------------------------------------*/

   @Override
   public void handleEvent(JSONObject jo) {

      // Checking event
      if (!"twitter.status".equals(jo.getString("source") + '.' + jo.getString("type"))) return;

      // Retrieving data
      JSONObject status = jo.getJSONObject("status");
      String id = Long.toString(status.getLong("user_id"));

      // Checking if first time user
      if (hasBeenSaluted(id)) return;

      // Registering user
      boolean saved = mgr.execute(new JSONObject()
         .put("command", "storage.save")
         .put("key", STORAGE_USERS_KEY)
         .put("data", new JSONObject()
            .put("users", usersIds.put(id))
         )
      )
         .getBoolean("success");

      // Sending wellcome message
      if (saved)
         mgr.execute(new JSONObject()
            .put("token", TOKEN)
            .put("command", "twitter.post")
            .put("status_id", status.getLong("status_id"))
            .put("status", "@" + status.getString("user") + " Que la fuerza te acompañe mi joven padawan!!!")
         );
   }

   @Override
   public JSONObject execute(JSONObject jo) {

      switch (jo.getString("command")) {

         case CMD_HASUSER:
            return new JSONObject()
               .put("success", Boolean.TRUE)
               .put("data", hasBeenSaluted(Long.toString(jo.getLong("user_id"))));

         case CMD_CLEAR:
            return new JSONObject()
               .put("success", mgr.execute(new JSONObject()
                     .put("command", "storage.delete")
                     .put("key", STORAGE_USERS_KEY)
                  ).getBoolean("success")
               );

         default:
            return new JSONObject()
               .put("success", Boolean.TRUE);
      }
   }

   @Override
   public void setManager(IManager iManager) {
      this.mgr = iManager;
   }

   /* Helpers --------------------------------------------------------------------------------------------------------*/

   private boolean hasBeenSaluted(String id) {

      for (Object uid : usersIds = loadUsersIds())
         if (uid.equals(id))
            return true;

      return false;
   }

   private JSONArray loadUsersIds() {

      return mgr
         .execute(new JSONObject()
            .put("command", "storage.loadorsave")
            .put("data", DEFAULT_STORED)
            .put("key", STORAGE_USERS_KEY)
         )
         .getJSONObject("data")
         .getJSONArray("users");
   }
}
