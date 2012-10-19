package models.cms;

import play.data.validation.Required;
import play.db.jpa.GenericModel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
public class CMSImage extends GenericModel {
  @Id @Required
	public String name;

  @Temporal(TIMESTAMP)
  public Date lastModified;

	@Required @Lob
	public byte[] data;
}
