package modle;

import java.util.ArrayList;

public class ObjectNode {
	
	
	private ArrayList<Integer>  flagList=new ArrayList<Integer>(); 
	//private ArrayList<MessageNode> msgNodeList=new ArrayList<MessageNode>();//
	protected String nodeName;
	protected String xmiId;
	protected int number;
	protected int localId;
//	protected String documentation=null;
	
	public ObjectNode(){
		
	}
	public ObjectNode(String nodeName,String xmiId,int localId)
	{
		
		this.setNodeName(nodeName);
		this.setXmiId(xmiId);
		this.setLocalId(localId);
//		this.setDocumentation(documentation);
		
	}
	/*public void addmsgNodeList(MessageNode msgNode){
		msgNodeList.add(msgNode);
	}*/

	
	public String getNodeName(){
		return nodeName;
	}
	public void setNodeName(String nodeName){
		this.nodeName=nodeName; 
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
//	public void setDocumentation(String documentation){
//		this.documentation=documentation;
//	}
//    public String getDocumentation(){
//    	return documentation;
//    }

	public void updateLastFlag(int n){
		flagList.set(flagList.size()-1,flagList.get(flagList.size()-1)+n );
	}

	public int getFlagListElement(int index){
		return flagList.get(index);
	}
	public int getFlagListCount() {
		// TODO Auto-generated method stub
		return flagList.size();
	}
	public void addfFlag(int n) {
		// TODO Auto-generated method stub
		flagList.add(n);
	}
	
//	public void printlist(){
//		for(int i=0;i<flagList.size();i++)
//			System.out.println(flagList.get(i));
//	}
	
}
