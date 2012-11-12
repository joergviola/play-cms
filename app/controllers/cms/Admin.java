package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import org.apache.commons.io.IOUtils;
import play.data.validation.Valid;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Router;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.StringUtils.defaultString;

public class Admin extends Controller {

	public static void index() {
		if (!Profiler.canEnter())
			forbidden();
		List<CMSPage> pages = CMSPage.find("order by time desc").fetch();
		render(pages);
	}

	public static void editPage(String tmpl, String pageName) {
		if (!Profiler.canEdit(pageName))
			forbidden();
		CMSPage page = CMSPage.findByName(pageName, Lang.get());
		if (page==null) {
			page = new CMSPage();
			page.name = pageName;
			page.active = true;
      page.locale = Lang.get();
		}
		renderTemplate("@edit", page, tmpl);
	}

  public static void editPageById(Long id) {
    CMSPage page = CMSPage.findById(id);
    if (!Profiler.canEdit(page.name)) forbidden();
    renderTemplate("@edit", page);
  }

	public static void addPage(String tags, String name) {
		if (!Profiler.canEnter())
			forbidden();
		CMSPage page = new CMSPage();
		page.active = true;
    page.tags = tags;
    page.locale = Lang.get();
    page.name = name;
		renderTemplate("@edit", page);
	}

	public static void savePage(@Valid CMSPage page, boolean active) throws Throwable {
		if (!Profiler.canEdit(page.name))
			forbidden();

    page.locale = defaultString(page.locale, Lang.get());
		page.active = active;
    page.time = new Date();

    if (request.params.get("delete") != null) {
      page.delete();
      index();
    }

    if (validation.hasErrors())
      renderTemplate("@edit", page);

		page.save();
		if (request.params.get("savePage") != null)
			Frontend.show(null, page.name);
		index();
	}

	public static void upload(File data) throws IOException {
		if (!Profiler.canEnter())
			forbidden();
    checkAuthenticity();

		CMSImage image = CMSImage.findById(data.getName());
		if (image == null) {
			image = new CMSImage();
			image.name = data.getName();
		}
    image.lastModified = new Date();
		image.data = IOUtils.toByteArray(new FileInputStream(data));
		image.save();
		redirect(Router.reverse("cms.Admin.imagelist").url + "?" + request.querystring);
	}

  public static void delete(String name) {
    if (!Profiler.canEnter())
      forbidden();
    checkAuthenticity();

    CMSImage image = CMSImage.findById(name);
    image.delete();
    redirect(Router.reverse("cms.Admin.imagelist").url + "?" + request.querystring);
  }

	public static void imagelist() {
		if (!Profiler.canEnter())
			forbidden();
		List<CMSImage> images = CMSImage.findAll();
		render(images);
	}
}
