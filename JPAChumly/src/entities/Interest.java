package entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Interest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private InterestCategory category;
	
	@ManyToMany(mappedBy="interests")
	private List<User> users;
	
	public Interest() { }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Interest [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", category=");
		builder.append(category);
		builder.append("]");
		return builder.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InterestCategory getCategory() {
		return category;
	}

	public void setCategory(InterestCategory category) {
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
