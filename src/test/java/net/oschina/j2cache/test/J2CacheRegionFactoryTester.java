package net.oschina.j2cache.test;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class J2CacheRegionFactoryTester {

	private static SessionFactory sessionFactory = null;

	private static void beforeTest() {

		Configuration configuration = new AnnotationConfiguration();
		try {
			configuration.configure();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		sessionFactory = configuration.buildSessionFactory();
	}

	public static void main(String[] args) {
		beforeTest();

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		User u1 = (User) session.load(User.class, 1);
		System.out.println(u1.getName());
		session.getTransaction().commit();
		session.close();
		
		Session session2 = sessionFactory.openSession();
		session2.beginTransaction();
		User u2 = (User) session2.load(User.class, 1);
		System.out.println(u2.getName());
		session2.getTransaction().commit();
		session2.close();
		
		/*Session session3 = sessionFactory.openSession();
		session3.beginTransaction();
		User u3 = (User) session3.load(User.class, 1);
		System.out.println(u3.getName());
		session3.getTransaction().commit();
		session3.close();*/

	}

}
