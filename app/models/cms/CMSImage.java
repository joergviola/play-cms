package models.cms;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.GenericModel;

@Entity
public class CMSImage extends GenericModel {
	@Required
	@Id
	public String name;
	@Required
	public String title;
	@Required
	public Blob data;

}
