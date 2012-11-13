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

import static org.apache.commons.lang.StringUtils.isNotEmpty;

@FastTags.Namespace("cms")
public class Tags extends FastTags {
	public static void _display(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Throwable {
		String pageName = (String) args.get("arg");
		CMSPage page = CMSPage.findByName(pageName, Lang.get());

    String safeBody = body != null ? JavaExtensions.toString(body) : "";

		if (page == null) {
			page = new CMSPage();
			page.name = pageName;
      page.locale = Lang.get();
			page.title = Messages.get("cms.fragment") + template.template.name;
			page.body = safeBody;
			page.active = false;
			if (isNotEmpty(page.body))
        page.save();
		}
    else if (page.active && page.body != null) {
      safeBody = page.body;
		}

    out.print("<div class=\"cms-content" + (Profiler.canEdit(page.name) ? " editable" : "") + "\">");
    out.print(safeBody);
    editLink(out, page.name);
    out.print("</div>");
	}
	
	public static void _edit(Map<?, ?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine) throws Throwable {
		String pageName = (String) args.get("arg");
		editLink(out, pageName);
	}

	private static void editLink(PrintWriter out, String name) throws Throwable {
		if (!Profiler.canEdit(name))
			return;
		HashMap<String, Object> args = new HashMap<String, Object>();
		args.put("pageName", name);
		out.print("<a class=\"cms-edit\" href=\"" + Router.reverse("cms.Admin.editPage", args) + "\">" + Messages.get("cms.edit") + "</a>");
	}
}
