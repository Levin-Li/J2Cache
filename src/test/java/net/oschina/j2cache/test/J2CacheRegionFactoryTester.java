package net.oschina.j2cache.test;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * 
 * J2CacheRegionFactoryTester
 * @author honganan
 * @date   2015年3月11日22:59:40
 *
 */
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

		//J2Cache测试
		test1();
		
		//J2Cache配合查询缓存
		//test2();
		
	}
	
	public static void test1(){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		User u1 = (User) session.get(User.class, 1);
		System.out.println(u1.getName());
		session.getTransaction().commit();
		session.close();
		
		Session session2 = sessionFactory.openSession();
		session2.beginTransaction();
		User u2 = (User) session2.get(User.class, 1);
		System.out.println(u2.getName());
		session2.getTransaction().commit();
		session2.close();
	}
	
	public static void test2(){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		String hql = "select t from User t where t.age > 30";
		Query query = session.createQuery(hql);
		query.setCacheable(true);
		System.out.println("list.size="+query.list().size());
		session.getTransaction().commit();
		session.close();
		
		Session session2 = sessionFactory.openSession();
		session2.beginTransaction();
		String hql1 = "select t from User t where t.age > 30";
		Query query1 = session2.createQuery(hql1);
		query1.setCacheable(true);
		System.out.println("list.size="+query1.list().size());
		session2.getTransaction().commit();
		session2.close();
	}
	
}
