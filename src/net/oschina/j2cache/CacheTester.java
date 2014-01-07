/**
 * 
 */
package net.oschina.j2cache;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

/**
 * 缓存测试入口
 * @author Winter Lau
 */
public class CacheTester {

	public static void main(String[] args) {
		CacheChannel cache = CacheChannel.getInstance();
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));

	    do{
	        try {
	            System.out.print("> "); 
	            System.out.flush();
	            
	            String line=in.readLine().trim();
	            if(line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
	                break;

	            String[] cmds = StringUtils.split(line);
	            if("get".equalsIgnoreCase(cmds[0])){
	            	String value = cache.get(cmds[1], cmds[2]);
	            	System.out.printf("[%s,%s]=>%s\n",cmds[1], cmds[2], value);
	            }
	            else
	            if("set".equalsIgnoreCase(cmds[0])){
	            	cache.set(cmds[1], cmds[2],cmds[3]);
	            	System.out.printf("[%s,%s]<=%s\n",cmds[1], cmds[2], cmds[3]);
	            }
	            else
	            if("evict".equalsIgnoreCase(cmds[0])){
	            	cache.evict(cmds[1], cmds[2]);
	            	System.out.printf("[%s,%s]=>null\n",cmds[1], cmds[2]);
	            }
	            else
	            if("help".equalsIgnoreCase(cmds[0])){
	            	printHelp();
	            }
	            else{
	            	System.out.println("Unknown command.");
	            	printHelp();
	            }
	        }
	        catch(ArrayIndexOutOfBoundsException e) {
            	System.out.println("Wrong arguments.");
	        	printHelp();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
	    }while(true);
	    
	    System.exit(0);
	}
	
	private static void printHelp() {
		System.out.println("Usage: [cmd] region key [value]");
		System.out.println("cmd: get/set/evict/quit/exit/help");
	}

}
