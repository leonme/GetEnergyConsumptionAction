package com.sap.mii.action.custom;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.xmii.xacute.actions.ActionReflectionBase;
import com.sap.xmii.xacute.core.ILog;
import com.sap.xmii.xacute.core.Transaction;


public class GetEnergyConsumptionAction extends ActionReflectionBase {
    
    /*
     * This will take the First Input in the Transaction
     */
    private String strJSONString;
    /*
     * This will take the Second Input in the Transaction
     */
    private String strNodeNames;
    
    /*
     * This will give the result.
     */
    private Document outXML;
    /*
     * Constructor
     */
    public GetEnergyConsumptionAction() throws ParserConfigurationException {
        // Initialise all attributes in the Cunstructor
        strJSONString="";
        strNodeNames="";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        outXML= docBuilder.newDocument();
        Element rootNode = outXML.createElement("Energy");
        Element resultSetNode = outXML.createElement("ResultSet");
        rootNode.appendChild(resultSetNode);
        outXML.appendChild(rootNode);
        
    }
    
   /**
     * This will take the Icon to display in the BLS
     */
    public String GetIconPath() {
        return "/CustomAction.png";
    }

    /*
     * This method contains the actual business logic for the
     * Action Block
     */
    public void Invoke(Transaction trx, ILog ilog)
    {
       try{
    	  String[] nodeNames = strNodeNames.split(",");
           for(String nodeName : nodeNames){
               Map<String,BigDecimal> map = getEnergyConsumptionMap(strJSONString);
               if(map.containsKey(nodeName)) {
                   Element resultNode = outXML.createElement("Result");
                   Element nodeNameNode = outXML.createElement("NodeName");
                   nodeNameNode.appendChild(outXML.createTextNode(nodeName));
                   Element valueNode = outXML.createElement("Value");
                   valueNode.appendChild(outXML.createTextNode(map.get(nodeName).toString()));
                   resultNode.appendChild(nodeNameNode);
                   resultNode.appendChild(valueNode);
                   outXML.getFirstChild().getFirstChild().appendChild(resultNode);
               }
           }
          /*

           // This varaible is defined in ActionReflectionBase class

            */
           _success=true;
       }catch (Exception e) {
            _success=false;// Set _success to false if any exception is cought
            ilog.error(e);
   }
}

// Getter for the Fisrt Input

    public String getJSONString() {
        return strJSONString;
    }

// Setter for the first Input

   public void setJSONString(String strJSONString) {
        this.strJSONString = strJSONString;
    }

//Getter for the Second Input
    public String getNodeNames() {
        return strNodeNames;
    }

// Setter for the Second Input

   public void setNodeNames(String strNodeNames) {
        this.strNodeNames = strNodeNames;
    }

// getter for the Output . Note there is no setter as this is output property

   public Document getOutXML() {
        return outXML;
    }

/**
     * This is required to make the Configure Button Disabled 
     * Note: If you want to have Custom ConfigureDialog, you need not put this method.

    */
    public boolean isConfigurable(){
        return false;
    }
    
	public Map<String, BigDecimal> getEnergyConsumptionMap(String jsonString) {
		JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
		JsonObject meterObjs = obj.get("meters").getAsJsonObject();
		Set<Entry<String,JsonElement>> metersEntrySet = meterObjs.entrySet();
		Map<String, BigDecimal> resultMap = new HashMap<>();
		for(Entry<String,JsonElement> entry: metersEntrySet){
			JsonObject node = entry.getValue().getAsJsonObject();
			resultMap.put(node.get("id").getAsString(), node.get("value").getAsBigDecimal());
		}
		return resultMap;
	}
}
