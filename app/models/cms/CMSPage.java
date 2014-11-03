package models.cms;

import org.hibernate.annotations.Type;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.JPABase;
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
  public static final CMSPage NULL_PAGE = new CMSPage();
  public static final String CACHE_EXPIRATION = Play.configuration.getProperty("cms.cache.expiration", "30mn");

	@Required public String name;
  public String locale;
  @Required public String title;
  public String author;
  @Temporal(TIMESTAMP) public Date time = new Date();
  @Temporal(TIMESTAMP) public Date lastEditTime = time;

  @Required @Lob
	@Type(type = "org.hibernate.type.TextType") @Column(length=10000)
	public String body;

	public boolean active;
  public boolean editSource;
	public Integer sort;
	public String tags;

  boolean isNullPage() {
    return id == null && name == null && body == null;
  }

  static String cacheKey(String name, String locale) {
    return "cms:" + name + ":" + locale;
  }

  String cacheKey() {
    return cacheKey(name, locale);
  }

  @Override public <T extends JPABase> T save() {
    if (id != null) {
      lastEditTime = new Date();
    }
    T page = super.save();
    Cache.set(cacheKey(), page, CACHE_EXPIRATION);
    return page;
  }

  @Override public <T extends JPABase> T delete() {
    T page = super.delete();
    Cache.delete(cacheKey());
    return page;
  }

  public static List<CMSPage> findByTag(String tag) {
		return find("tags like ? order by sort", "%" + tag + "%").fetch();
	}

  public static CMSPage findByName(String name, String locale) {
    String key = cacheKey(name, locale);
    CMSPage page = (CMSPage) Cache.get(key);
    if (page == null) {
      page = find("byNameAndLocale", name, locale).first();
      if (page == null) page = NULL_PAGE;
      Cache.set(key, page, CACHE_EXPIRATION);
    }
    return page.isNullPage() ? null : page;
  }
}
