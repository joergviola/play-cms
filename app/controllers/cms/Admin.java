package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import models.cms.CMSPageRepository;
import org.apache.commons.io.IOUtils;
import play.data.validation.Valid;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.results.Forbidden;
import play.mvc.results.Redirect;
import play.mvc.results.Result;
import play.rebel.RebelController;
import play.rebel.RedirectToAction;
import play.utils.Java;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.defaultString;
import static play.db.jpa.JPA.em;

public class Admin extends RebelController {
  @Inject static CMSPageRepository pages;
  
  public Result index() {
    if (!Profiler.canEnter()) {
      return new Forbidden();
    }

    return viewResult().with("pages", pages.all());
  }

  public Result editPage(String tmpl, String pageName, Long id) {
    CMSPage page = id != null ? pages.byId(id) : CMSPage.findByName(pageName, Lang.get());
    if (page == null) {
      page = new CMSPage();
      page.name = pageName;
      page.active = true;
      page.locale = Lang.get();
    }

    if (!Profiler.canEdit(page.name)) {
      return new Forbidden();
    }

    renderArgs.put("page", page);
    renderArgs.put("tmpl", tmpl);
    return viewResult("@edit");
  }

  public Result addPage(String tags, String name) {
    if (!Profiler.canEnter()) {
      return new Forbidden();
    }
    CMSPage page = new CMSPage();
    page.active = true;
    page.tags = tags;
    page.locale = Lang.get();
    page.name = name;
    renderArgs.put("page", page);
    return viewResult("@edit");
  }

  public Result savePage(@Valid CMSPage page, boolean active) throws Throwable {
    if (!Profiler.canEdit(page.name)) {
      return new Forbidden();
    }

    checkAuthenticity();

    page.locale = defaultString(page.locale, Lang.get());
    page.active = active;

    if (request.params.get("delete") != null) {
      page.delete();
      Extension.invoke("afterDelete", page);
      return redirectToIndex();
    }

    if (validation.hasErrors()) {
      renderArgs.put("page", page);
      return viewResult("@edit");
    }

    page.save();
    Extension.invoke("afterSave", page);
    
    if (request.params.get("savePage") != null) {
      Map<String, Object> args = new HashMap<>();
      args.put("pageName", page.name);
      return new RedirectToAction("cms.Frontend.show", args);
    }
    else {
      return redirectToIndex();
    }
  }

  private RedirectToAction redirectToIndex() {
    return new RedirectToAction("cms.Admin.index");
  }

  public Result upload(File data) throws Throwable {
    if (!Profiler.canEnter()) {
      return new Forbidden();
    }
    checkAuthenticity();

    CMSImage image = em().find(CMSImage.class, data.getName());
    if (image == null) {
      image = new CMSImage();
      image.name = data.getName();
    }
    image.lastModified = new Date();
    image.data = IOUtils.toByteArray(new FileInputStream(data));
    image.save();
    Extension.invoke("afterSave", image);
    return new Redirect(Router.reverse("cms.Admin.imagelist").url + "?" + request.querystring);
  }

  public Result delete(String name) throws Throwable {
    if (!Profiler.canEnter()) {
      return new Forbidden();
    }
    checkAuthenticity();

    CMSImage image = em().find(CMSImage.class, name);
    image.delete();
    Extension.invoke("afterDelete", image);
    return new Redirect(Router.reverse("cms.Admin.imagelist").url + "?" + request.querystring);
  }

  public Result imagelist() {
    if (!Profiler.canEnter()) {
      return new Forbidden();
    }

    List<CMSImage> images = em().createQuery("select i from CMSImage i", CMSImage.class).getResultList();
    return viewResult().with("images", images);
  }

  public static class Extension extends Controller {
    static void afterSave(CMSPage page) {}

    static void afterDelete(CMSPage page) {}

    static void afterSave(CMSImage image) {}

    static void afterDelete(CMSImage image) {}

    private static Object invoke(String m, Object... args) throws Throwable {
      try {
        return Java.invokeChildOrStatic(Extension.class, m, args);
      }
      catch (InvocationTargetException e) {
        throw e.getTargetException();
      }
    }
  }
}
