package chapter_05.ioc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

@Slf4j
public class SpringApp {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        new XmlBeanDefinitionReader(context).loadBeanDefinitions("services.xml");
        new GroovyBeanDefinitionReader(context).loadBeanDefinitions("services.groovy");
        new PropertiesBeanDefinitionReader(context).loadBeanDefinitions("services.properties");
        context.refresh();

        System.out.println(context.getBean("xmlBean"));
        System.out.println(context.getBean("groovyBean"));
        System.out.println(context.getBean("propsBean"));

    }
}
