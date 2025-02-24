package ioc.framework;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class XMLBeanFactory implements BeanFactory {

    private Map<String, Object> beans = new HashMap<>();

    public XMLBeanFactory(String xmlFilePath) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            NodeList beanList = document.getElementsByTagName("bean");
            for (int i = 0; i < beanList.getLength(); i++) {
                Node beanNode = beanList.item(i);
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element) beanNode;
                    String id = beanElement.getAttribute("id");
                    String className = beanElement.getAttribute("class");
                    Class<?> clazz = Class.forName(className);
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    beans.put(id, instance);
                }
            }

            // Inject dependencies
            for (int i = 0; i < beanList.getLength(); i++) {
                Node beanNode = beanList.item(i);
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element) beanNode;
                    String id = beanElement.getAttribute("id");
                    NodeList propertyList = beanElement.getElementsByTagName("property");
                    Object bean = beans.get(id);

                    for (int j = 0; j < propertyList.getLength(); j++) {
                        Node propertyNode = propertyList.item(j);
                        if (propertyNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element propertyElement = (Element) propertyNode;
                            String name = propertyElement.getAttribute("name");
                            String ref = propertyElement.getAttribute("ref");

                            Object dependency = beans.get(ref);
                            if (dependency != null) {
                                Field field = bean.getClass().getDeclaredField(name);
                                field.setAccessible(true);
                                field.set(bean, dependency);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error creating XML-based bean factory", e);
        }
    }

    @Override
    public Object getBean(String beanName) {
        return beans.get(beanName);
    }
}
