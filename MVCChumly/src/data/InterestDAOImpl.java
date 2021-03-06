package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Category;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import entities.Interest;
import entities.InterestCategory;

@Transactional
@Repository
public class InterestDAOImpl implements InterestDAO {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Interest show(int id) {
		Interest interest =em.find(Interest.class, id);
		return interest;
	}

	@Override
	public Interest create(Integer id, String interest) {
		Interest newInterest = new Interest();
		InterestCategory category = em.find(InterestCategory.class, id);
		newInterest.setName(interest);
		newInterest.setCategory(category);
		em.persist(newInterest);
		em.flush();
		return newInterest;
	}

	@Override
	public Interest update(int id, Interest interest) {
		Interest i =em.find(Interest.class, id);
		i.setCategory(interest.getCategory());
		i.setName(interest.getName());
		//i.setUsers(interest.getUsers());

		return i;
	}

	@Override
	public boolean destroy(int id) {
		Interest i = (em.find(Interest.class, id));

		try{
			em.remove(i);
			em.flush();
			return true;
		}
		catch(Exception e){
			return false;

		}
	}

	@Override
	public List<Interest> index() {
		List<Interest> results = null;
		try {
			String queryString = "SELECT i FROM Interest i";
			results = em.createQuery(queryString, Interest.class).getResultList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return results;
		}
		return results;
	}

	@Override
	public List<Interest> indexByCategory(InterestCategory InterestCategory) {
		List<Interest> results = null;
		try {
			String queryString = "SELECT i FROM Interest i WHERE i.category.id = :id";
			results = em.createQuery(queryString, Interest.class)
					.setParameter("id", InterestCategory.getId()).getResultList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return results;
		}
		return results;
	}

	@Override
	public List<Interest> indexBy(Predicate<Interest> filter) {
		List<Interest> results = new ArrayList<>();
		for (Interest interest : index()) {
			if(filter.test(interest)){
				results.add(interest);
			}
		}
		return results;
	}

	@Override
	public Map<String, List<Interest>> mapByCategory() {
		Map<String, List<Interest>> map = new TreeMap<>();

		List<Interest> interests = index();
		interests.sort( (a,b) -> a.getName().compareToIgnoreCase(b.getName()) );

		for(Interest i : interests) {
			String cat = i.getCategory().getName();
			if(map.get(cat) == null) {
				map.put(cat, new ArrayList<Interest>());
			}

			map.get(cat).add(i);
		}

		return map;
	}

	@Override
	public InterestCategory showCategory(int id) {
		InterestCategory interestCategory = em.find(InterestCategory.class, id);
		return interestCategory;
	}

	@Override
	public InterestCategory createCategory(InterestCategory interestCategory) {
		em.persist(interestCategory);
		em.flush();
		return interestCategory;
	}

	@Override
	public InterestCategory updateCategory(int id, InterestCategory interestCategory) {
		InterestCategory iC = em.find(InterestCategory.class, id);

		iC.setName(interestCategory.getName());

		return iC;
	}

	@Override
	public boolean destroyCategory(int id) {
		InterestCategory iC = (em.find(InterestCategory.class, id));

		try{
			em.remove(iC);
			em.flush();
			return true;
		}
		catch(Exception e){
			return false;

		}
	}

	@Override
	public List<InterestCategory> indexCategories() {
		List<InterestCategory> results = null;
		try {
			String queryString = "SELECT ic FROM InterestCategory ic";
			results = em.createQuery(queryString, InterestCategory.class).getResultList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return results;
		}
		return results;
	}

	@Override
	public List<InterestCategory> indexCategoriesBy(Predicate<InterestCategory> filter) {
		List<InterestCategory> results = new ArrayList<>();
		for (InterestCategory category : indexCategories()) {
			if(filter.test(category)){
				results.add(category);
			}
		}
		return results;
	}

	@Override
	public List<Interest> indexByContainsText(String text) {
		List<Interest> results = null;
		try {
			String queryString = "SELECT i FROM Interest i WHERE i.name LIKE :text";
			results = em.createQuery(queryString, Interest.class).setParameter("text", ("%"+text+"%"))
					.getResultList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return results;
		}
		return results;
	}
}
