package models.cms;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

import play.data.validation.Required;
import play.db.jpa.GenericModel;

@Entity
public class CMSPage extends GenericModel {
	@Required
	@Id
	public String name;
	@Required
	public String title;
	@Required
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(length = 10000)
	public String body;
	public boolean active;

	public static List<CMSPage> findByName(String regex) {
		List<CMSPage> all = CMSPage.find("order by name").fetch();
		List<CMSPage> result = new ArrayList<CMSPage>();
		for (CMSPage page : all) {
			if (page.name.matches(regex))
				result.add(page);
		}
		return result;
	}
}
