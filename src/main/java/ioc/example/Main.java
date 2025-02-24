package ioc.example;

import ioc.framework.AnnotationBeanFactory;
import ioc.framework.BeanFactory;
import ioc.framework.XMLBeanFactory;

public class Main {
    public static void main(String[] args) {
        // XML Configuration
        BeanFactory xmlBeanFactory = new XMLBeanFactory("beans.xml");
        IMetier metier = (IMetier) xmlBeanFactory.getBean("metier");
        System.out.println("Result from XML config: " + metier.calcul());

        // Annotation Configuration
        AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory("ioc.example");
        IMetier metierAnnotation = (IMetier) annotationBeanFactory.getBean("Metier");
        System.out.println("Result from Annotation config: " + metierAnnotation.calcul());
    }
}
