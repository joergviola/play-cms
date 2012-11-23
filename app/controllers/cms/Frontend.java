package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import play.i18n.Lang;
import play.libs.MimeTypes;
import play.mvc.Controller;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;

public class Frontend extends Controller {
	public static void show(String template, String pageName) throws Throwable {
		CMSPage page = CMSPage.findByName(pageName, Lang.get());
		if (page == null || !page.active) {
			if (Profiler.canEdit(pageName))
				Admin.editPage(null, pageName);
			else
				notFound();
		}
		if (template == null) {
			template = "cms/default";
		}
		renderTemplate("/" + template + ".html", page);
	}

	public static void image(String name) {
    CMSImage image = CMSImage.findById(name);
    if (image == null) notFound();
    response.contentType = MimeTypes.getContentType(name);
    response.setHeader("Last-Modified", new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(image.lastModified));
    response.setHeader("Cache-Control", "max-age=604800"); // 7 days
    renderBinary(new ByteArrayInputStream(image.data), name);
  }
}
