package chapter_05.ioc;

import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.cglib.proxy.Dispatcher;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

//public class MyWebApplication implements WebApplicationInitializer {
//    @Override
//    public void onStartup(javax.servlet.ServletContext servletContext) throws ServletException {
//        AnnotationConfigWebApplicationContext cxt = new AnnotationConfigWebApplicationContext();
//        cxt.register();
//        cxt.refresh();
        //DispatcherServlet ...
        //ServletRegistation registration...
        // registration.setLoadOnStartup(1);
        // regstration.addMapping("/app/*);
//    }
//}
