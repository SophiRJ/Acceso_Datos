import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        File mercados = new File("src/mercados.xml");
        DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();
        Document document=documentBuilder.parse(mercados);

        //Normalizamos el documento
        document.getDocumentElement().normalize();

        //Elemento raiz
        Element root=document.getDocumentElement();

        //Accedemos al titulo del canal y copyright
        Element channel=(Element) root.getElementsByTagName("channel").item(0);
        String nombreCanal=channel.getElementsByTagName("title").item(0).getTextContent();
        String copyright=channel.getElementsByTagName("copyright").item(0).getTextContent();
        System.out.println(channel.getNodeName()+": "+nombreCanal);
        System.out.println("Copyright: "+copyright);

        String tituloNoticia="";
        String creador="";
        String categoria="";
        String tituloImagen="";
        String atributoImg="";
        String atributoImagen="";

        //Lista donde guardaremos cada uno de los items
        NodeList list= document.getElementsByTagName("item");
        //Recorremos la lista y guardamos cada item en un nodo(Node)
        for (int i=0;i<list.getLength();i++){
            Node nodo= list.item(i);
            System.out.println();
            System.out.println("====================");

            if(nodo.getNodeType()== Node.ELEMENT_NODE){
                //Casteamos cada nodo a elemento para poder acceder a sus metodos
                Element elemento= (Element) nodo;
                tituloNoticia= elemento.getElementsByTagName("title").item(0).getTextContent();
                creador= elemento.getElementsByTagName("dc:creator").item(0).getTextContent();
                categoria= elemento.getElementsByTagName("category").item(0).getTextContent();
                tituloImagen= elemento.getElementsByTagName("media:title").item(0).getTextContent();
                //otra forma de acceder a los atributos
                //atributoImg=elemento.getElementsByTagName("media:title").item(0).getAttributes().getNamedItem("type").getTextContent();
                Element tituloImg= (Element) elemento.getElementsByTagName("media:title").item(0);
                atributoImagen= tituloImg.getAttribute("type");
            }
            //Imprimimos resultados
            System.out.println("Titulo Noticia: "+tituloNoticia);
            System.out.println("Creador: "+creador);
            System.out.println("Categoria: "+categoria);
            //System.out.println("Media:title: type="+atributoImg+" "+tituloImagen);
            System.out.println("Media:title: type="+atributoImagen+" "+tituloImagen);
        }
    }
}