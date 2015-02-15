package views.tags.cms;

import controllers.cms.Profiler;
import groovy.lang.Closure;
import models.cms.CMSPage;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Router;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@FastTags.Namespace("cms")
public class Tags extends FastTags {
	public static void _display(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Throwable {
		String pageName = (String) args.get("arg");
		String className = (String) args.get("class");
    String locale = (String) args.get("locale");
    if (isNotEmpty(locale) && !Lang.get().matches(locale)) return;

    if (isEmpty(locale)) locale = Lang.get();
		CMSPage page = CMSPage.findByName(pageName, locale);

		if (page == null) {
			page = new CMSPage();
			page.name = pageName;
      page.locale = locale;
			page.title = Messages.get("cms.fragment") + " " + pageName;
			page.body = body != null ? JavaExtensions.toString(body) : null;

      Boolean defaultActive = (Boolean) args.get("defaultActive");
			page.active = defaultActive == null || defaultActive;
			if (isNotEmpty(page.body))
        page.save();
		}

    out.print("<div class=\"cms-content" + (Profiler.canEdit(page.name) ? " editable" : "") + "\">");
    if (page.active && page.body != null)
      out.print(page.body);
    editLink(out, page.name, className);
    out.print("</div>");
	}
	
	public static void _edit(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Throwable {
		String pageName = (String) args.get("arg");
		editLink(out, pageName, "");
	}

	private static void editLink(PrintWriter out, String name, String className) throws Throwable {
		if (!Profiler.canEdit(name))
			return;
		HashMap<String, Object> args = new HashMap<>();
		args.put("pageName", name);
		out.print("<a class=\"cms-edit " + className + "\" href=\"" + Router.reverse("cms.Admin.editPage", args) + "\">" + Messages.get("cms.edit") + "</a>");
	}
}
