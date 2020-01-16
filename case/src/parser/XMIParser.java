package parser;

import java.io.File;
import java.util.List;
import java.util.Comparator;
import java.util.Iterator;

import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.Element;

import modle.Diagram;
import modle.ObjectNode;
import modle.MessageNode;


public class XMIParser {


	private static String objectFilterString = "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/"
			+ "UML:Package/UML:Namespace.ownedElement/UML:Collaboration/UML:Namespace.ownedElement/"
			+ "UML:ClassifierRole";
	
	private static String messageFilterString = "/XMI/XMI.content/UML:Model/UML:Namespace.ownedElement/"
			+ "UML:Package/UML:Namespace.ownedElement/UML:Collaboration/UML:Collaboration.interaction/"
			+ "UML:Interaction/UML:Interaction.message/UML:Message";
	
	
	public XMIParser(){
		
	}
	
	public Diagram parse(String filePath) throws DocumentException{
		Diagram diagram =new Diagram();
		SAXReader saxReader= new SAXReader();
		Document document =saxReader.read(new File(filePath));
		
		parseObject(document,diagram);
		parseMessage(document,diagram);
	    diagram.parseDiagram();
		return diagram;
	}
	private void parseObject(Document document,Diagram diagram){

		
		List<Node> objectList=document.selectNodes(objectFilterString);
		int number=diagram.getObjectCount();
		for (Iterator<Node> i= objectList.iterator();i.hasNext();)
		{
			Element element=(Element) i.next();
			String name = element.attributeValue("name");
			String xmiId= element.attributeValue("xmi.id");
			int localId=0;
//			String documentation=null;
			Iterator<Node> j=element.elementIterator();
			if(j.hasNext()){
				Element element2=(Element) j.next();
				for(Iterator<Node> k=element2.elementIterator();k.hasNext();){
					Element element3 =(Element) k.next();
					String value=element3.attributeValue("tag");
					if(value.equals("ea_localid")){
						localId=Integer.parseInt(element3.attributeValue("value"));						
					}
//					if(value.equals("documentation")){
//						documentation=element3.attributeValue("value");
//					}
				}
					
				ObjectNode node =new ObjectNode (name,xmiId,localId);
				node.setNumber(number);
				diagram.addObject(node);
				number++;
				System.out.println(name+"\t"+xmiId+"\t"+localId+"\t"+node.getNumber()+'\t');
		
								
			}
		}
		
	}
	
	private void parseMessage(Document document,Diagram diagram){
		List<Node> msgList= document.selectNodes(messageFilterString );
		int number=diagram.getMessageCount();
		for(Iterator<Node> i= msgList.iterator();i.hasNext();){
			Element element=(Element)i.next();
			String name=element.attributeValue("name");
			String xmiId=element.attributeValue("xmi.id");
			int localId=0;
			ObjectNode preNode=null,nextNode=null;
			int positionY=0;
			String documentation=null;
			Iterator<Node> j =element.elementIterator();
			if(j.hasNext()){
				Element element2=(Element)j.next();
				for(Iterator<Node> k=element2.elementIterator(); k.hasNext();){
					Element element3=(Element) k.next();
					String value =element3.attributeValue("tag");
					if(value.equals("ea_targetID")){
						value=element3.attributeValue("value");
						nextNode=diagram.getObjectByLocalId(Integer.parseInt(value));
					}
					if(value.equals("ea_sourceID")){
						value=element3.attributeValue("value");
						preNode=diagram.getObjectByLocalId(Integer.parseInt(value));
					
					}
					if(value.equals("sequence_points")){
						value=element3.attributeValue("value");
						String[] values=value.split(";|=");								
						value=values[3].toString();
						positionY=Integer.parseInt(value);
					
					}
					if(value.equals("ea_localid")){
						value=element3.attributeValue("value");
						localId=Integer.parseInt(value);
					}
					if(value.equals("documentation")){
						documentation=element3.attributeValue("value");
					}
					
				
 				}
				MessageNode node= new MessageNode(name,xmiId,localId,positionY,preNode,nextNode,documentation);
				node.setNumber(number+diagram.getObjectCount());
				diagram.addMessage(node);
				number++;
			
//				System.out.println(name+"\t"+xmiId+"\t"+localId+"\t"+positionY+"\t"+preNode.getNodeName()+"\t"+nextNode.getNodeName());
			}
		}
		diagram.sortByPositionY();
		for(int i=0;i<diagram.getMessageCount();i++)
		    System.out.println(diagram.getMsgNode(i).getNodeName()+"\t"+diagram.getMsgNode(i).getNumber()+"\t"+diagram.getMsgNode(i).getDocumentation());
		diagram.sortByNumber();	
		//System.out.println(diagram.getObjNode(0).getNumber()+";"+diagram.getObjNode(1).getNumber()+";"+diagram.getObjNode(2).getNumber());
	}

	public static void main(String[] args) throws DocumentException{
		XMIParser parser =new XMIParser();
		Diagram diagram=parser.parse("./data/test_seq1.xml");
	
	}
}

