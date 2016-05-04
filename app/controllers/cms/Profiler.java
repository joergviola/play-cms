package controllers.cms;

import controllers.Secure.Security;
import play.Logger;
import play.Play;
import play.mvc.Scope.Session;
import play.utils.Java;

public class Profiler {
	public static boolean canEdit(String pageName)  {
		if (Session.current().get("username")==null)
			return false;
		String profile = Play.configuration.getProperty("cms.profile", "admin");
		boolean result;
		try {
			result = (Boolean) invoke(Security.class, "check", profile);
			return result;
		} catch (Throwable e) {
			Logger.error(e, "While checking cms profile");
			return false;
		}
	}
	public static boolean canEnter()  {
		if (Session.current().get("username")==null)
			return false;
		String profile = Play.configuration.getProperty("cms.profile", "admin");
		boolean result;
		try {
			result = (Boolean) invoke(Security.class, "check", profile);
			return result;
		} catch (Throwable e) {
			Logger.error(e, "While checking cms profile");
			return false;
		}
	}

  private static Object invoke(Class<?> original, String m, Object... args) throws Throwable {
    return Java.invokeChildOrStatic(original, m, args);
  }
}
