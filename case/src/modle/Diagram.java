package modle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
public class Diagram {
	
	private ArrayList<ObjectNode> objectList=new ArrayList<ObjectNode>();
	private ArrayList<MessageNode> msgList= new ArrayList<MessageNode>();
	
	public Diagram(){
		
	}
	public void addObject(ObjectNode node){
		objectList.add(node);
	}
	public void addMessage(MessageNode node){
		msgList.add(node);
		
	}
	public ObjectNode getObjNode(int index){
		return objectList.get(index);
	}
	public MessageNode getMsgNode(int index){
		return msgList.get(index);
	}
	public int getObjectCount(){
		return objectList.size();
	}
	public int getMessageCount(){
		return msgList.size();
	}
	public ObjectNode getObjectByLocalId(int localId){
		for(ObjectNode i: objectList){
			if(i.getLocalId()==localId)
				return i;
		}
		return null;
	}

	public void sortByPositionY(){
		ComparePositionY pY=new ComparePositionY();
		Collections.sort(msgList,pY);
	}
	public void sortByNumber(){
		CompareNumber localId=new CompareNumber();
		Collections.sort(objectList,localId);
	}
	
	public void addObjNodeFlag(ObjectNode obj,int index){
		obj=objectList.get(objectList.indexOf(obj));		
//		System.out.println(obj.getNodeName()+"-------");
		if(obj.getFlagListCount()==0){
			obj.addfFlag(index);			
		}
		else{
			if(index==1){
				if(obj.getFlagListElement(obj.getFlagListCount()-1)>0){
					obj.updateLastFlag(index);// ���գ��޸�ǰһλֵ
				}
				if(obj.getFlagListElement(obj.getFlagListCount()-1)==0)
				{
					obj.addfFlag(index);
				}
			}
			if(index==0){
				obj.addfFlag(index);
			 }
			
			}			
		objectList.set(objectList.indexOf(obj),obj);
//		obj.printlist();			
	}
	public void parseDiagram(){
		flagSequence();
	}
	
	
	public void flagSequence(){//flagList ���ͽ���˳��ȷ��
		
		for(int i=0;i<getMessageCount();i++){			
			MessageNode msgNode=getMsgNode(i);
			ObjectNode Obj_send=msgNode.getPreNode();	
			ObjectNode  Obj_recieve=msgNode.getNextNode();
//	     System.out.println(Obj_send.getNodeName()+"\t"+Obj_recieve.getNodeName()+"\t"+"=======");
			addObjNodeFlag(Obj_send,0);  
			addObjNodeFlag( Obj_recieve,1);
	 
		}
		sortByNumber();
//		System.out.println(diagram.getObjNode(0).getNodeName());
//		diagram.getObjNode(0).printlist(); 
//		System.out.println(diagram.getObjNode(1).getNodeName());
//     diagram.getObjNode(1).printlist(); 
// 	 	System.out.println(diagram.getObjNode(2).getNodeName());
//     diagram.getObjNode(2).printlist(); 
	}
	
	
}	

class ComparePositionY implements Comparator<MessageNode>{

	public int compare(MessageNode msg1,MessageNode msg2) {
		if(msg1.getPositionY()<msg2.getPositionY())
			return 1;
		else return -1;
					
	}
}

class CompareNumber implements Comparator<ObjectNode>{
	public int compare(ObjectNode obj1,ObjectNode obj2){
		if(obj1.getNumber()>obj2.getNumber())
			return 1;
		else 
			return -1;

		
	}
}



