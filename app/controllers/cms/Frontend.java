package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import play.i18n.Lang;
import play.libs.MimeTypes;
import play.mvc.Controller;
import play.mvc.Router;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static play.db.jpa.JPA.em;

public class Frontend extends Controller {
  private static final String MAX_AGE_7_DAYS = "max-age=604800";

  public static void show(String template, String pageName) {
    CMSPage page = CMSPage.findByName(pageName, Lang.get());
    if (page == null || !page.active) {
      if (Profiler.canEdit(pageName)) {
        Map<String, Object> args = new HashMap<>();
        args.put("pageName", pageName);
        redirect(Router.reverse("cms.Admin.editPage", args).url);
      }
      else
        notFound();
    }
    if (template == null) {
      template = "cms/default";
    }
    renderArgs.put("page", page);
    renderTemplate("/" + template + ".html");
  }

  public static void image(String name) {
    flash.keep();

    CMSImage image = em().find(CMSImage.class, name);
    if (image == null) {
      notFound();
    }
    else {
      response.contentType = MimeTypes.getContentType(name);
      response.setHeader("Last-Modified", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(image.lastModified));
      response.setHeader("Cache-Control", MAX_AGE_7_DAYS);
      renderBinary(new ByteArrayInputStream(image.data), name);
    }
  }
}
