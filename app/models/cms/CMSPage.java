package models.cms;

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
}
