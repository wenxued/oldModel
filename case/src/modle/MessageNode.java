package modle;


public class MessageNode {
	protected ObjectNode preNode=null;
	protected String messageName;
	protected String xmiId;
	protected String documentation=null;
	protected int number;
	protected int localId;
	protected int positionY;
	protected ObjectNode nextNode=null;
	public MessageNode(){
		
	}
	public MessageNode(String messageName,String xmiId,int localId,
			int positiony,ObjectNode objNode,ObjectNode objNode1,String documentation)
	{
		
		this.setNodeName(messageName);
		this.setXmiId(xmiId);
		this.setLocalId(localId);
		this.setPositionY(positiony);
		this.setPreNode(objNode);
		this.setNextNode(objNode1);
		this.setDocumentation(documentation);
	}
	public String getNodeName(){
		return messageName;
	}
	public void setNodeName(String nodeName){
		this.messageName=nodeName; 
	}
	public int getNumber(){
		return number;
	}
	public void setNumber(int number){
		this.number=number;
	}
	public String getXmiId(){
		return xmiId;
	}
	public void setXmiId(String xmiId){
		this.xmiId=xmiId;
		
	}
	public int getLocalId(){
		return localId;
	}
	
	public void setLocalId(int localId){
		this.localId=localId;
	}
	
	public int getPositionY(){
		return  positionY;
	}
	public void setPositionY(int positiony){
		this.positionY=positiony;
	}
	public void setPreNode(ObjectNode objNode){
		this.preNode=objNode;
		
	}
	public  ObjectNode  getPreNode(){
		return preNode;
	}
	public void setNextNode(ObjectNode objNode){
		this.nextNode=objNode;
	}
	public  ObjectNode getNextNode(){
		return nextNode;
	}
	public void setDocumentation(String documentation){
		this.documentation=documentation;
	}
    public String getDocumentation(){
    	return documentation;
    }

	


}


