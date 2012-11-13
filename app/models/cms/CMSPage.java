package models.cms;

import org.hibernate.annotations.Type;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
public class CMSPage extends Model {
	@Required public String name;
  public String locale;
  @Required public String title;
  public String author;
  @Temporal(TIMESTAMP) public Date time = new Date();

  @Required @Lob
	@Type(type = "org.hibernate.type.TextType") @Column(length=10000)
	public String body;

	public boolean active;
  public boolean editSource;
	public Integer sort;
	public String tags;

	public static List<CMSPage> findByTag(String tag) {
		return find("tags like ? order by sort", "%" + tag + "%").fetch();
	}

  public static CMSPage findByName(String name, String locale) {
    return find("byNameAndLocale", name, locale).first();
  }
}
