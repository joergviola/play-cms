package controllers.cms;

import play.Play;
import play.classloading.enhancers.ControllersEnhancer.ControllerSupport;
import play.mvc.Scope.Session;
import play.security.AuthorizationService;

import javax.inject.Inject;

public class Profiler implements ControllerSupport {
  @Inject static AuthorizationService authorizationService;

  public static boolean canEdit(String pageName) {
    if (Session.current().get("username") == null)
      return false;

    String profile = Play.configuration.getProperty("cms.profile", "admin");
    return authorizationService.check(profile);
  }

  public static boolean canEnter() {
    if (Session.current().get("username") == null)
      return false;

    String profile = Play.configuration.getProperty("cms.profile", "admin");
    return authorizationService.check(profile);
  }
}
