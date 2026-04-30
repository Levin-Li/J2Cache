package net.oschina.j2cache;

import net.oschina.j2cache.util.FastjsonSerializer;
import net.oschina.j2cache.util.Fastjson2Serializer;
import net.oschina.j2cache.util.FstJSONSerializer;
import net.oschina.j2cache.util.Jackson3Serializer;
import net.oschina.j2cache.util.JacksonSerializer;
import net.oschina.j2cache.util.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;


import static org.junit.Assert.*;

/**
 * 测试 json 序列化
 */
public class JSONSerializerTest {

    Properties mapping;
    Person person ;

    @Before
    public void setUp() {
        person = new Person();
        person.setName("Winter Lau");
        person.setAge(19);
        person.setSchoolList(Arrays.asList(new School("西北工业大学"), new School("泉州第五中学"), new School("城东中学"), new School("洛南小学")));
        HashMap<String, Integer> jobs = new HashMap<String, Integer>();
        jobs.put("creawor", 3);
        jobs.put("moabc", 5);
        jobs.put("huateng", 3);
        jobs.put("oschina", 8);
        person.setJobs(jobs);

        mapping = new Properties();
        mapping.setProperty("map.person", "net.oschina.j2cache.Person");
        mapping.setProperty("map.school", "net.oschina.j2cache.School");
        mapping.setProperty("map.list", "java.util.Arrays$ArrayList");
    }

    @After
    public void tearDown() {
        person = null;
        mapping = null;
    }

    @Test
    public void fst_json() {

        FstJSONSerializer serializer = new FstJSONSerializer(mapping);

        byte[] bytes = serializer.serialize(person);

        System.out.println(new String(bytes));


        Person p = (Person)serializer.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());

        System.out.println(p);

    }


    @Test
    public void fast_json() {

        FastjsonSerializer serializer = new FastjsonSerializer();

        byte[] bytes = serializer.serialize(person);

        System.out.println(new String(bytes));


        Person p = (Person)serializer.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());

        System.out.println(p);
    }

    @Test
    public void fast_json2() {

        Fastjson2Serializer serializer = new Fastjson2Serializer();

        byte[] bytes = serializer.serialize(person);

        System.out.println(new String(bytes));


        Person p = (Person)serializer.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());

        System.out.println(p);
    }

    @Test
    public void serialization_utils_supports_fastjson2() throws Exception {
        SerializationUtils.init("fastjson2", new Properties());

        byte[] bytes = SerializationUtils.serialize(person);
        Person p = (Person) SerializationUtils.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());
    }

    @Test
    public void jackson() throws Exception {
        JacksonSerializer serializer = new JacksonSerializer();

        byte[] bytes = serializer.serialize(person);
        Person p = (Person) serializer.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());
    }

    @Test
    public void jackson3() throws Exception {
        Jackson3Serializer serializer = new Jackson3Serializer();

        byte[] bytes = serializer.serialize(person);
        Person p = (Person) serializer.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());
    }

    @Test
    public void serialization_utils_supports_jackson() throws Exception {
        SerializationUtils.init("jackson", new Properties());

        byte[] bytes = SerializationUtils.serialize(person);
        Person p = (Person) SerializationUtils.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());
    }

    @Test
    public void serialization_utils_supports_jackson3() throws Exception {
        SerializationUtils.init("jackson3", new Properties());

        byte[] bytes = SerializationUtils.serialize(person);
        Person p = (Person) SerializationUtils.deserialize(bytes);

        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());
    }

    @Test
    public void serialization_utils_defaults_to_fastjson2_when_not_configured() throws Exception {
        SerializationUtils.init(null, new Properties());

        byte[] bytes = SerializationUtils.serialize(person);

        assertTrue(new String(bytes).contains("\"@type\":\"net.oschina.j2cache.Person\""));

        Person p = (Person) SerializationUtils.deserialize(bytes);
        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());
    }

    @Test
    public void serialization_utils_defaults_to_fastjson2_when_blank() throws Exception {
        SerializationUtils.init("  ", new Properties());

        byte[] bytes = SerializationUtils.serialize(person);

        assertTrue(new String(bytes).contains("\"@type\":\"net.oschina.j2cache.Person\""));

        Person p = (Person) SerializationUtils.deserialize(bytes);
        assertEquals(person.getName(), p.getName());
        assertEquals(person.getAge(), p.getAge());
    }
}
