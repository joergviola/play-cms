package views.tags.cms;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import models.cms.CMSPage;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;
import play.templates.JavaExtensions;

@FastTags.Namespace("cms")
public class Tags extends FastTags {
	public static void _display(Map<?, ?> args, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {

		String pageName = (String) args.get("arg");
		CMSPage page = CMSPage.findById(pageName);
		if (page == null) {
			page = new CMSPage();
			page.name = pageName;
			page.title = "Fragment on "+template.template.name;
			page.body = JavaExtensions.toString(body);
			page.active = false;
			page.save();
			out.print(JavaExtensions.toString(body));
		} else if (!page.active) {
			out.print(JavaExtensions.toString(body));
		} else {
			out.print(page.body);
		}
	}
}
