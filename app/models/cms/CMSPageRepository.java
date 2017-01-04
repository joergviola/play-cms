package models.cms;

import javax.inject.Singleton;
import java.util.List;

import static play.db.jpa.JPA.em;

@Singleton
public class CMSPageRepository {
  public List<CMSPage> all() {
    return em().createQuery("select p from CMSPage p order by time desc", CMSPage.class).getResultList();
  }
  
  public CMSPage byId(long id) {
    return em().find(CMSPage.class, id);
  }
}
