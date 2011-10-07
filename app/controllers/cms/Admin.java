package controllers.cms;

import java.util.List;

import models.cms.CMSPage;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;
import controllers.Check;
import controllers.Secure;

@With(Secure.class)
@Check("admin")
public class Admin extends Controller {

	public static void index() {
		List<CMSPage> pages = CMSPage.all().fetch();
		render(pages);
	}

	public static void editPage(String tmpl, String pageName) {
		CMSPage page = CMSPage.findById(pageName);
		renderTemplate("@edit", page, tmpl);
	}

	public static void addPage() {
		CMSPage page = new CMSPage();
		renderTemplate("@edit", page);
	}

	public static void savePage(@Valid CMSPage page, String tmpl) {
		page.save();
		if (request.params.get("savePage") != null)
			Frontend.show(tmpl, page.name);
		index();
	}
}
