package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import play.mvc.Controller;

public class Frontend extends Controller {
	public static void show(String template, String pageName) {
		CMSPage page = CMSPage.findById(pageName);
		if (page == null || !page.active)
			notFound();
		if (template == null) {
			template = "default";
		}
		renderTemplate("/" + template + ".html", page);
	}

	public static void image(String name) {
		CMSImage image = CMSImage.findById(name);
		renderBinary(image.data.get());
	}
}
