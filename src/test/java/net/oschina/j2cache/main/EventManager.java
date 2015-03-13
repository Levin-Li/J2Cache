package net.oschina.j2cache.main;

import java.util.List;

import net.oschina.j2cache.domain.Event;
import net.oschina.j2cache.util.HibernateUtil;

import org.hibernate.Criteria;
import org.hibernate.Session;

public class EventManager {
	public static void main(String[] args) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Criteria c = session.createCriteria(Event.class);
		c.setCacheable(true);
		System.out.println("第一次读取");
		List<Event> list = c.list();
		System.out.println(list.size());
		session.close();
		System.out.println("///////");
		//
		Session session2 = HibernateUtil.getSessionFactory().openSession();
		Criteria c2 = session2.createCriteria(Event.class);
		c2.setCacheable(true);
		System.out.println("第二次读取");
		List<Event> list2 = c2.list();
		System.out.println(list2.size());
		session2.close();
		//
		HibernateUtil.getSessionFactory().close();
	}
}
