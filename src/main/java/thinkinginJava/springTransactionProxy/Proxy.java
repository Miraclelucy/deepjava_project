package thinkinginJava.springTransactionProxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Proxy {
	public static Object newProxyInstacne(Class interf,InvocationHandler th) throws IOException, ClassNotFoundException{
		String rt= "\r\n" ;
		String methodStr="";
		Method[] methods=interf.getMethods();
		/*// 1引入了动态生成方法
		for(Method m : methods){
			methodStr+="@Override"+ rt+
					"    	public void "+m.getName()+"() {"+rt  +
					"    		long startTime=System.currentTimeMillis();"+rt  +
					"    		t."+m.getName()+"();"+rt  +
					"    		long endTime=System.currentTimeMillis();"+rt  +
					"    		System.out.println(\"---耗时---\"+(startTime-endTime));"+rt  +
					"    	}"+rt  ;
					
		}*/
		//2 动态生成方法里面的处理逻辑，比如做时间记录处理，做日志记录的处理
		for(Method m : methods){
			methodStr+="@Override"+ rt+
					"    	public void "+m.getName()+"() {"+rt  +
					"        try{ "+rt  +
					"       Method md="+interf.getName()+".class.getMethod(\""+m.getName() +"\"); "+rt  +
					"    	th.invoke(this,md);"+rt  +
					"       } catch(Exception e) {"+rt  +
					"      e.printStackTrace(); }"+rt  +
					"}"+rt  ;
		}
		
		String src="    package springTransactionProxy;"+  rt  +
				"  import java.lang.reflect.Method; "+  rt  +
				"    "+rt  +
				"    public class TankTimeProxy implements " + interf.getName() + "{"+ rt  +
				"    	"+rt  +
				"    	public TankTimeProxy(InvocationHandler th) {"+rt  +
				"    		this.th = th;"+rt  +
				"    	}"+rt  +
				"    "+
				"    	InvocationHandler th;"+rt  +
				"    	"+
				methodStr+
				"    }";
		
	
	//1生成类文件
	String  filename=System.getProperty("user.dir")+"/src/springTransactionProxy/TankTimeProxy.java";//当前系统的目录
	//System.out.println(filename);
	File f=new File(filename);
	FileWriter fw=new FileWriter(f);
	fw.write(src);
	fw.flush();
	fw.close();
	//System.out.println("文件写入成功----");
	//2编译类 使用jdk6的自带方法
	JavaCompiler cp=ToolProvider.getSystemJavaCompiler();
	StandardJavaFileManager filemg=cp.getStandardFileManager(null, null, null);
	Iterable utils=filemg.getJavaFileObjects(filename); 
	CompilationTask t=cp.getTask(null, filemg, null, null, null, utils);
	t.call();
	filemg.close();
	//System.out.println(cp);
	//3load至内存中
	URL[] urls=new URL[]{new URL("file:/"+System.getProperty("user.dir")+"/src")};
	URLClassLoader cl=new URLClassLoader(urls);
	Class c=cl.loadClass("springTransactionProxy.TankTimeProxy");
	System.out.println(c);
	//4创建一个实例
	Constructor ctr=null;
	Object tp=null;
	try {
		ctr = c.getConstructor(InvocationHandler.class);
		tp = ctr.newInstance(th); 
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	return  tp;
	
}
	
}
