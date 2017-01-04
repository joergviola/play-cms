package controllers.cms;

import models.cms.CMSImage;
import models.cms.CMSPage;
import models.cms.CMSPageRepository;
import org.apache.commons.io.IOUtils;
import play.data.validation.Valid;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Router;
import play.utils.Java;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.defaultString;
import static play.db.jpa.JPA.em;

public class Admin extends Controller {
  @Inject static CMSPageRepository pages;
  
  public static void index() {
    if (!Profiler.canEnter())
      forbidden();

    renderArgs.put("pages", pages.all());
    render();
  }

  public static void editPage(String tmpl, String pageName, Long id) {
    CMSPage page = id != null ? pages.byId(id) : CMSPage.findByName(pageName, Lang.get());
    if (page == null) {
      page = new CMSPage();
      page.name = pageName;
      page.active = true;
      page.locale = Lang.get();
    }

    if (!Profiler.canEdit(page.name))
      forbidden();

    renderArgs.put("page", page);
    renderArgs.put("tmpl", tmpl);
    renderTemplate("@edit");
  }

  public static void addPage(String tags, String name) {
    if (!Profiler.canEnter())
      forbidden();
    CMSPage page = new CMSPage();
    page.active = true;
    page.tags = tags;
    page.locale = Lang.get();
    page.name = name;
    renderArgs.put("page", page);
    renderTemplate("@edit");
  }

  public static void savePage(@Valid CMSPage page, boolean active) throws Throwable {
    if (!Profiler.canEdit(page.name))
      forbidden();

    checkAuthenticity();

    page.locale = defaultString(page.locale, Lang.get());
    page.active = active;

    if (request.params.get("delete") != null) {
      page.delete();
      Extension.invoke("afterDelete", page);
      redirectToIndex();
    }

    if (validation.hasErrors()) {
      renderArgs.put("page", page);
      renderTemplate("@edit");
    }

    page.save();
    Extension.invoke("afterSave", page);
    
    if (request.params.get("savePage") != null) {
      Map<String, Object> args = new HashMap<>();
      args.put("pageName", page.name);
      redirect(Router.reverse("cms.Frontend.show", args).url);
    }
    else {
      redirectToIndex();
    }
  }

  private static void redirectToIndex() {
    redirect(Router.reverse("cms.Admin.index").url);
  }

  public static void upload(File data) throws Throwable {
    if (!Profiler.canEnter())
      forbidden();
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
    redirect(Router.reverse("cms.Admin.imagelist").url + "?" + request.querystring);
  }

  public static void delete(String name) throws Throwable {
    if (!Profiler.canEnter())
      forbidden();
    checkAuthenticity();

    CMSImage image = em().find(CMSImage.class, name);
    image.delete();
    Extension.invoke("afterDelete", image);
    redirect(Router.reverse("cms.Admin.imagelist").url + "?" + request.querystring);
  }

  public static void imagelist() {
    if (!Profiler.canEnter())
      forbidden();
    renderArgs.put("images", em().createQuery("select i from CMSImage i", CMSImage.class).getResultList());
    render();
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
