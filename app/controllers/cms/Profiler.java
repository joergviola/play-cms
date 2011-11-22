package controllers.cms;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import controllers.Secure.Security;

import play.Play;
import play.mvc.Scope.Session;
import play.utils.Java;

public class Profiler {
	public static boolean canEdit(String pageName) throws Throwable {
		if (Session.current().get("username")==null)
			return false;
		boolean result = (Boolean) invoke(Security.class, "check", "admin");
		return result;
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
