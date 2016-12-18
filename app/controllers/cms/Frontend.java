package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import play.i18n.Lang;
import play.libs.MimeTypes;
import play.mvc.Controller;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Frontend extends Controller {

  private static final String MAX_AGE_7_DAYS = "max-age=604800";

  public static void show(String template, String pageName) throws UnsupportedEncodingException {
    CMSPage page = CMSPage.findByName(pageName, Lang.get());
    if (page == null || !page.active) {
      if (Profiler.canEdit(pageName))
        redirect("/cms/admin/edit?pageName=" + URLEncoder.encode(pageName, UTF_8.name()));
      else
        notFound();
    }
    if (template == null) {
      template = "cms/default";
    }
    renderTemplate("/" + template + ".html", page);
  }

  public static void image(String name) {
    flash.keep();

    CMSImage image = CMSImage.findById(name);
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
