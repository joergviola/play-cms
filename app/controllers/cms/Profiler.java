package controllers.cms;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
		Class called = null;
		List<Class> classes = Play.classloader
				.getAssignableClasses(original);
		if (classes.size() == 0) {
			called = original;
		} else {
			called = classes.get(0);
		}
		try {
			return Java.invokeStaticOrParent(called, m, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
	
}
