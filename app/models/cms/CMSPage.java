package models.cms;

import javax.persistence.Entity;
import javax.persistence.Id;

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
	public String body;

}
