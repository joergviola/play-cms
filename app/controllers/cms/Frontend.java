package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import play.i18n.Lang;
import play.mvc.Controller;

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
		renderBinary(image.data.get());
	}
}
