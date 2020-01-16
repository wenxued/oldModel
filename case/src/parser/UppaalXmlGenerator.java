package parser;

import modle.Diagram;
import modle.MessageNode;
import modle.ObjectNode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UppaalXmlGenerator {

	public UppaalXmlGenerator() {

	}

	public Document generateModel(Diagram diagram) {

		Element root = DocumentHelper.createElement("nta");
		Document document = DocumentHelper.createDocument(root);

		Element declarationElement = root.addElement("declaration");
		Element templateElement = root.addElement("template");
		Element templateElement1 = root.addElement("template");
		Element systemElement = root.addElement("system");

		createDeclarationPart(declarationElement, diagram);
		createTemplateObjectPart(templateElement);
		createTemplateMessagePart(templateElement1);
		createSystemElement(systemElement, diagram);

		return document;
	}

	// public void flagSequence(Diagram diagram){//flagList 发送接收顺序确定
	// for(int i=0;i<diagram.getMessageCount();i++){
	// MessageNode msgNode=diagram.getMsgNode(i);
	// ObjectNode Obj_send=msgNode.getPreNode();
	// ObjectNode Obj_recieve=msgNode.getNextNode();
	//// System.out.println(Obj_send.getNodeName()+"\t"+Obj_recieve.getNodeName()+"\t"+"=======");
	// diagram.addObjNodeFlag(Obj_send,0);
	// diagram.addObjNodeFlag( Obj_recieve,1);
	//
	// }
	//
	//// System.out.println(diagram.getObjNode(0).getNodeName());
	//// diagram.getObjNode(0).printlist();
	//// System.out.println(diagram.getObjNode(1).getNodeName());
	//// diagram.getObjNode(1).printlist();
	//// System.out.println(diagram.getObjNode(2).getNodeName());
	//// diagram.getObjNode(2).printlist();
	// }
	//
	public void createDeclarationPart(Element declarationElement, Diagram diagram) {
		String n1 = "\n";
		String declarationString = "// Place global declarations here.\n";
		int messageNum = diagram.getMessageCount();
		int objectNum = diagram.getObjectCount();
		int N = messageNum + objectNum;
		declarationString = declarationString + n1 + "const int OBJECT= " + objectNum + ";" + n1 + "const int MESSAGE="
				+ messageNum + ";" + n1 + "const int N=MESSAGE+OBJECT;" + n1 + "typedef int[0,N] id_t;" + n1
				+ "typedef int[0,OBJECT-1] obj_t;" + n1 + "typedef int[OBJECT,N-1] msg_t;" + n1
				+ "typedef int[-1, N*N-1] message_t;" + n1 + "urgent broadcast chan msg[N*N];" + n1;
		String mean_tableString = "double mean_table[N][N] ={" + n1;
		String deviation_tableString = "double deviation_table[N][N] = {"+n1;

		double[][] Mean_table = new double[N][N];
		double[][] Deviation_table = new double[N][N];
		for (int j = 0; j < messageNum; j++) {
			String documentation = diagram.getMsgNode(j).getDocumentation();
			if (documentation != null && documentation != "") {
				int iMean = documentation.indexOf("Mean");
				if (iMean != -1) {
					if (documentation.indexOf(",", iMean + 5) != -1) {
						double mean = Double.valueOf(
								documentation.substring(iMean + 5, documentation.indexOf(",", iMean + 5)).toString());
						double mean1 = Double.valueOf(documentation.substring(documentation.indexOf(",", iMean + 5) + 1,
								documentation.indexOf(";", documentation.indexOf(",", iMean + 5))));
						Mean_table[diagram.getMsgNode(j).getPreNode().getNumber()][diagram.getMsgNode(j)
								.getNumber()] = mean;
						Mean_table[diagram.getMsgNode(j).getNumber()][diagram.getMsgNode(j).getNextNode()
								.getNumber()] = mean1;
					} else {
						double mean = Double.valueOf(
								documentation.substring(iMean + 5, documentation.indexOf(";", iMean + 5)).toString());
						Mean_table[diagram.getMsgNode(j).getPreNode().getNumber()][diagram.getMsgNode(j)
								.getNumber()] = mean;
					}

				}
				int iDeviation = documentation.indexOf("Deviation:");
				if (iDeviation != -1) {
					if (documentation.indexOf(",", iDeviation + 10) != -1) {
						double mean = Double.valueOf(documentation
								.substring(iDeviation + 10, documentation.indexOf(",", iDeviation + 10)).toString());
						double mean1 = Double
								.valueOf(documentation.substring(documentation.indexOf(",", iDeviation + 10) + 1,
										documentation.indexOf(";", documentation.indexOf(",", iDeviation + 10))));
						Deviation_table[diagram.getMsgNode(j).getPreNode().getNumber()][diagram.getMsgNode(j)
								.getNumber()] = mean;
						Deviation_table[diagram.getMsgNode(j).getNumber()][diagram.getMsgNode(j).getNextNode()
								.getNumber()] = mean1;
					} else {
						double mean = Double.valueOf(documentation
								.substring(iDeviation + 10, documentation.indexOf(";", iDeviation + 10)).toString());
						Deviation_table[diagram.getMsgNode(j).getPreNode().getNumber()][diagram.getMsgNode(j)
								.getNumber()] = mean;
					}
				}

			}
		

		}

		for (int i = 0; i < N; i++) {
			mean_tableString += "   {";
			deviation_tableString += "   {";
			for (int j = 0; j < N; j++) {
				mean_tableString = mean_tableString + Mean_table[i][j] ;
				deviation_tableString=deviation_tableString+Deviation_table[i][j];
				if(j!=N-1){
					mean_tableString +=","; 
					deviation_tableString+=",";
				}
			}
			mean_tableString += "}";
			deviation_tableString += "}";
			if(i!=N-1){
			    mean_tableString +="," + n1; 
				deviation_tableString+="," + n1;
			}
		}

		declarationString = declarationString + mean_tableString + n1+"};"+n1;
		declarationString = declarationString + deviation_tableString + n1+"};"+n1;
		String encode_msgFunctionString = "int encode_msg(id_t send_id,id_t receive_id)" + n1 + "{" + n1
				+ "   return N * send_id + receive_id;" + n1 + "}" + n1;
		declarationString = declarationString + encode_msgFunctionString;
		String connectedString = "int Connected[OBJECT][MESSAGE]={" + n1;
		for (int i = 0; i < objectNum; i++) {
			String connectedElement = "  {";
			for (int j = 0; j < messageNum; j++) {

				if (j < diagram.getObjNode(i).getFlagListCount()) {
					connectedElement = connectedElement + diagram.getObjNode(i).getFlagListElement(j);
				} else {
					connectedElement = connectedElement + "-1";
				}
				if (j != messageNum - 1) {
					connectedElement = connectedElement + ",";
				}
			}
			connectedElement = connectedElement + "}";
			if (i != objectNum - 1) {
				connectedElement = connectedElement + "," + n1;
			}
			connectedString = connectedString + connectedElement;
		}
		connectedString = connectedString + n1 + "};";
		declarationString = declarationString + connectedString + n1;

		String sendString = "message_t Send[OBJECT][MESSAGE]={   " + n1;
		for (int s = 0; s < objectNum; s++) {
			sendString = sendString + "  {";
			int count = 0;
			for (int r = 0; r < messageNum; r++) {
				if (diagram.getObjNode(s).getLocalId() == diagram.getMsgNode(r).getPreNode().getLocalId()) {
					int msgId = diagram.getMsgNode(r).getNumber();
					sendString = sendString + "encode_msg(" + diagram.getObjNode(s).getNumber() + "," + msgId + ")";
					count++;
					if (r != messageNum - 1 || count < messageNum - 1) {
						sendString = sendString + ",";
					}
				}
			}
			for (int a = count; a < messageNum; a++) {
				sendString = sendString + "-1";
				if (a != messageNum - 1) {
					sendString = sendString + ",";
				}
			}
			sendString = sendString + "}";
			if (s != objectNum - 1) {
				sendString = sendString + "," + n1;
			}
		}
		sendString = sendString + n1 + "};";
		declarationString = declarationString + sendString + n1;

		String MsgString = "message_t Msg[MESSAGE]=" + n1;
		MsgString = MsgString + "{" + n1;
		for (int m = 0; m < messageNum; m++) {
			int MsgNumber = diagram.getMsgNode(m).getNumber();
			MsgString = MsgString + "encode_msg(" + MsgNumber + "," + diagram.getMsgNode(m).getNextNode().getNumber()
					+ ")";
			if (m != messageNum - 1) {
				MsgString = MsgString + ",";
			}
		}
		MsgString = MsgString + n1 + "};" + n1;
		declarationString = declarationString + MsgString;

		String funString = "double normal_distribution(double mean, double deviation)" + n1 + "{" + n1
				+ "	double x1, y1, z1;" + n1 + "	x1 = random(1.0);" + n1 + "	y1 = random(1.0);" + n1
				+ "	z1 = ((deviation * pow(((-2.0) * ln(x1)), 0.5) * cos(2.0 * 3.14 * y1)) + mean);" + n1
				+ "	if(z1 < 0)" + n1 + "	{" + n1 + "		z1 = 0;" + n1 + "	}" + n1 + "	return z1;" + n1 + "}" + n1;
		declarationString += funString;
		declarationElement.addText(declarationString);
		System.out.println(declarationString);
	}

	public void createTemplateObjectPart(Element templateElement) {
		Element nameElement = null;
		Element parameterElement = null;
		Element declarationElement = null;
		Element locationElement = null;
		Element labelElement = null;
		Element initElement = null;
		Element transitionElement = null;
		Element sourceElement = null;
		Element targetElement = null;
		Element nailElement = null;

		// name
		nameElement = templateElement.addElement("name");
		nameElement.addText("Object");
		nameElement.addAttribute("x", "5");
		nameElement.addAttribute("y", "5");

		// parameter
		parameterElement = templateElement.addElement("parameter");
		parameterElement.addText("const obj_t obj_id");

		// declaration
		declarationElement = templateElement.addElement("declaration");
		declarationElement.addText("// Place local declarations here.\n" 
				+ "clock x;\n" + "double time;\n"
				+ "int flag=-1;\n" 
				+ "message_t send_msg=-1;\n"
				+ "int exe_num=0;\n" 
				+ "int send_num=0;\n"
				+ "void initialize(){\n"
				+ "		flag=Connected[obj_id][exe_num];\n"
				+ "		if(Connected[obj_id][exe_num]==0)\n" 
				+ " 		{  \n"
				+ "			send_msg=Send[obj_id][send_num];\n"
				+ "	 		send_num++;\n" + "		}\n" 
				+ "			}\n"
				+ "void exe_send( ){\n" 
				+ "	if(exe_num!=MESSAGE-1){" 
				+ "		++exe_num;\n"
				+ "		flag=Connected[obj_id][exe_num];\n"
				+ "	    if(Connected[obj_id][exe_num]==0)\n" 
				+ "		{\n"
				+ "			send_msg=Send[obj_id][send_num];\n" 
				+ "			send_num++;\n" + "		}\n" 
				+ "	}\n"
				+ "	else\n"
				+ "		flag=-1;\n"
				+ "}\n" 
				+ "void exe_rec(){\n" 
				+ "	if(flag==0){\n"
				+ "		 if(exe_num!=MESSAGE-1){\n" 
				+ "			++exe_num;\n"
				+ "			flag=Connected[obj_id][exe_num];\n"
				+ "			if(Connected[obj_id][exe_num]==0){\n"
				+ "				send_msg=Send[obj_id][send_num];\n"
				+ "			send_num++;\n" 
				+ "			}\n"
				+ "		}\n" 
				+ "		else\n" 
				+ "			flag=-1;\n" + "	}\n" 
				+ "}\n"
				+ "double running_time(id_t id){\n"
				+ "	 if(send_msg!=-1)\n"
				+ "  {\n"
				+ "	   int d=send_msg % N;\n"
				+ "    return normal_distribution(mean_table[id][d], deviation_table[id][d]); \n"
				+ "  }\n"
				+ "return 0.0;\n"
				+ "}\n"
				);

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id0");
		locationElement.addAttribute("x", "-25");
		locationElement.addAttribute("y", "-408");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "-35");
		nameElement.addAttribute("y", "-442");
		nameElement.addText("Sending");
		labelElement = locationElement.addElement("label");
		labelElement.addAttribute("kind", "invariant");
		labelElement.addAttribute("x", "-42");
		labelElement.addAttribute("y", "-467");
		labelElement.addText("x<=time");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id1");
		locationElement.addAttribute("x", "178");
		locationElement.addAttribute("y", "-255");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "168");
		nameElement.addAttribute("y", "-289");
		nameElement.addText("Wait");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id2");
		locationElement.addAttribute("x", "-34");
		locationElement.addAttribute("y", "-255");
		locationElement.addElement("committed");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id3");
		locationElement.addAttribute("x", "-416");
		locationElement.addAttribute("y", "-535");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "-426");
		nameElement.addAttribute("y", "-569");
		nameElement.addText("Recieving");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id4");
		locationElement.addAttribute("x", "-416");
		locationElement.addAttribute("y", "-255");
		locationElement.addElement("committed");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id5");
		locationElement.addAttribute("x", "-527");
		locationElement.addAttribute("y", "-255");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "-537");
		nameElement.addAttribute("y", "-289");
		nameElement.addText("Init");
		locationElement.addElement("committed");

		// init
		initElement = templateElement.addElement("init");
		initElement.addAttribute("ref", "id5");

		// transition
		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id4");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id2");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "-340");
		labelElement.addAttribute("y", "-255");
		labelElement.addText("flag<=0");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-221");
		nailElement.addAttribute("y", "-255");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id1");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id4");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "select");
		labelElement.addAttribute("x", "-170");
		labelElement.addAttribute("y", "-161");
		labelElement.addText("e : message_t");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "-170");
		labelElement.addAttribute("y", "-144");
		labelElement.addText("flag>0&&e % N== obj_id");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "synchronisation");
		labelElement.addAttribute("x", "-170");
		labelElement.addAttribute("y", "-127");
		labelElement.addText("msg[e]?");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "assignment");
		labelElement.addAttribute("x", "-170");
		labelElement.addAttribute("y", "-102");
		labelElement.addText("flag--,exe_rec()");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "170");
		nailElement.addAttribute("y", "-102");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-416");
		nailElement.addAttribute("y", "-102");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id2");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id0");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "-144");
		labelElement.addAttribute("y", "-382");
		labelElement.addText("flag==0");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "assignment");
		labelElement.addAttribute("x", "-195");
		labelElement.addAttribute("y", "-340");
		labelElement.addText("x=0,\n time=running_time(obj_id)");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-68");
		nailElement.addAttribute("y", "-348");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id0");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id2");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "-4");
		labelElement.addAttribute("y", "-408");
		labelElement.addText("x>=time");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "synchronisation");
		labelElement.addAttribute("x", "34");
		labelElement.addAttribute("y", "-357");
		labelElement.addText("msg[send_msg]!");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "assignment");
		labelElement.addAttribute("x", "25");
		labelElement.addAttribute("y", "-331");
		labelElement.addText("exe_send()");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "17");
		nailElement.addAttribute("y", "-340");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id3");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id4");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "select");
		labelElement.addAttribute("x", "-315");
		labelElement.addAttribute("y", "-502");
		labelElement.addText("e : message_t");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "-315");
		labelElement.addAttribute("y", "-485");
		labelElement.addText("e % N== obj_id");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "synchronisation");
		labelElement.addAttribute("x", "-315");
		labelElement.addAttribute("y", "-468");
		labelElement.addText("msg[e]?");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "assignment");
		labelElement.addAttribute("x", "-306");
		labelElement.addAttribute("y", "-433");
		labelElement.addText("flag--,exe_rec()");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-323");
		nailElement.addAttribute("y", "-408");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-366");
		nailElement.addAttribute("y", "-323");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id2");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id1");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "34");
		labelElement.addAttribute("y", "-246");
		labelElement.addText("flag!=0");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id4");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id3");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "-629");
		labelElement.addAttribute("y", "-433");
		labelElement.addText("flag>0");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-518");
		nailElement.addAttribute("y", "-408");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id5");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id4");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "assignment");
		labelElement.addAttribute("x", "-509");
		labelElement.addAttribute("y", "-255");
		labelElement.addText("initialize()");

	}

	public void createTemplateMessagePart(Element templateElement) {
		Element nameElement = null;
		Element parameterElement = null;
		Element declarationElement = null;
		Element locationElement = null;
		Element labelElement = null;
		Element initElement = null;
		Element transitionElement = null;
		Element sourceElement = null;
		Element targetElement = null;
		Element nailElement = null;

		// name
		nameElement = templateElement.addElement("name");
		nameElement.addText("Message");

		// parameter
		parameterElement = templateElement.addElement("parameter");
		parameterElement.addText("const msg_t msg_id");

		// declaration
		declarationElement = templateElement.addElement("declaration");
		declarationElement.addText("clock x;\n" 
				+ "double time;\n" 
				+ "message_t send_msg=-1;\n"
				+ "void initialize(){\n"
				+ "		for(i:int[0,MESSAGE-1])\n"
				+ "		{\n"
				+ "       if(Msg[i]/N==msg_id)\n"
				+ "  		send_msg=Msg[i];\n" 
				+ "  	 }\n"
				+ "}\n"
				+ "double running_time(id_t id){\n"
				+ "	 if(send_msg!=-1)\n"
				+ "  {\n"
				+ "	   int d=send_msg % N;\n"
				+ "    return normal_distribution(mean_table[id][d], deviation_table[id][d]); \n"
				+ "  }\n"
				+ "return 0.0;\n"
				+ "}\n"
				);
		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id6");
		locationElement.addAttribute("x", "323");
		locationElement.addAttribute("y", "-17");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "313");
		nameElement.addAttribute("y", "-51");
		nameElement.addText("End");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id7");
		locationElement.addAttribute("x", "-8");
		locationElement.addAttribute("y", "-17");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "-18");
		nameElement.addAttribute("y", "-51");
		nameElement.addText("Sending");
		labelElement = locationElement.addElement("label");
		labelElement.addAttribute("kind", "invariant");
		labelElement.addAttribute("x", "-18");
		labelElement.addAttribute("y", "0");
		labelElement.addText("x<=time");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id8");
		locationElement.addAttribute("x", "-272");
		locationElement.addAttribute("y", "-17");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "-282");
		nameElement.addAttribute("y", "-51");
		nameElement.addText("Recieving");

		locationElement = templateElement.addElement("location");
		locationElement.addAttribute("id", "id9");
		locationElement.addAttribute("x", "-442");
		locationElement.addAttribute("y", "-17");
		nameElement = locationElement.addElement("name");
		nameElement.addAttribute("x", "-452");
		nameElement.addAttribute("y", "-51");
		nameElement.addText("Init");
		locationElement.addElement("committed");

		// init
		initElement = templateElement.addElement("init");
		initElement.addAttribute("ref", "id9");

		// transition
		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id7");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id6");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "34");
		labelElement.addAttribute("y", "-51");
		labelElement.addText("x>=time");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "synchronisation");
		labelElement.addAttribute("x", "68");
		labelElement.addAttribute("y", "-42");
		labelElement.addText("msg[send_msg]!");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id8");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id7");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "select");
		labelElement.addAttribute("x", "-195");
		labelElement.addAttribute("y", "-76");
		labelElement.addText("e : message_t");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "guard");
		labelElement.addAttribute("x", "-195");
		labelElement.addAttribute("y", "-59");
		labelElement.addText("e % N== msg_id");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "synchronisation");
		labelElement.addAttribute("x", "-195");
		labelElement.addAttribute("y", "-42");
		labelElement.addText("msg[e]?");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "assignment");
		labelElement.addAttribute("x", "-254");
		labelElement.addAttribute("y", "-17");
		labelElement.addText("x=0,time=running_time(msg_id)");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-119");
		nailElement.addAttribute("y", "-17");
		nailElement = transitionElement.addElement("nail");
		nailElement.addAttribute("x", "-161");
		nailElement.addAttribute("y", "-17");

		transitionElement = templateElement.addElement("transition");
		sourceElement = transitionElement.addElement("source");
		sourceElement.addAttribute("ref", "id9");
		targetElement = transitionElement.addElement("target");
		targetElement.addAttribute("ref", "id8");
		labelElement = transitionElement.addElement("label");
		labelElement.addAttribute("kind", "assignment");
		labelElement.addAttribute("x", "-424");
		labelElement.addAttribute("y", "-17");
		labelElement.addText("initialize()");

	}

	public void createSystemElement(Element systemElement, Diagram diagram) {
		systemElement.addText("// Place template instantiations here.\n"
				+ "// List one or more processes to be composed into a system.\n" + "system Object,Message;\n");
	}

	public void printXmlToFile(Document document, String outputPath) throws IOException {
		OutputFormat format = new OutputFormat("    ", true);
		XMLWriter xmlFileWriter = new XMLWriter(new FileWriter(outputPath), format);
		xmlFileWriter.write(document);
		xmlFileWriter.close();

	}

	public static void main(String[] args) throws DocumentException, IOException {
		XMIParser parser = new XMIParser();
		UppaalXmlGenerator generator = new UppaalXmlGenerator();
		Diagram diagram = parser.parse("./data/test_seq4.xml");
		Document document = generator.generateModel(diagram);
		generator.printXmlToFile(document,"./output/seq5.xml");
	}
}
