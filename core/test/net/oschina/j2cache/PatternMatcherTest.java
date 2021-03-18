package net.oschina.j2cache;

import net.oschina.j2cache.util.AntPathMatcher;
import net.oschina.j2cache.util.PatternMatcher;

public class PatternMatcherTest {

    public static void main(String[] args) {
        PatternMatcher matcher = new AntPathMatcher();
        System.out.println(matcher.matches("abc_*","abc_sdfsd"));
        System.out.println(matcher.matches("asf_*","dsffsd"));
        System.out.println(matcher.matches("default","default"));
    }
}
