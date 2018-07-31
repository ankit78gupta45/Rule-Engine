/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication3;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author Ankit
 */
public class JavaApplication3 {
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
        
        Scanner scan = new Scanner(System.in).useDelimiter("\\n"); 
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Regular Expressions for matching the string, integer and datetime
        Pattern pattern = Pattern.compile("^[a-zA-Z]+[0-9]+$");
        Pattern cpattern = Pattern.compile("^[<>=]{1}|<>$");
        Pattern vtpattern = Pattern.compile("^[iI]{1}[nN]{1}[tT]{1}[eE]{1}[gG]{1}[eE]{1}[rR]{1}|[dD]{1}[aA]{1}[tT]{1}[eE]{1}[tT]{1}[iI]{1}[mM]{1}[eE]|[sS]{1}[tT]{1}[rR]{1}[iI]{1}[nN]{1}[gG]{1}$");
        Pattern vpattern = Pattern.compile("^[0-9]+([.][0-9]+)?$");
        Pattern spattern = Pattern.compile("^[hH]{1}[iI]{1}[gG]{1}[hH]{1}|[lL]{1}[oO]{1}[wW]{1}$");
        //Pattern tpattern = Pattern.compile("^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))$");
        Pattern dtpattern = Pattern.compile("^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))(\\s)(([0-1][0-9])|(2[0-3])):([0-5][0-9]):([0-5][0-9])$");
        System.out.println("Do you want to enter the rule? Yes/No");
        String choice = scan.next();        
        
        
        try{
            //Enter the path of the raw_data.json file
            String rawDataPath="C:\\Desktop\\RuleEngine\\JavaApplication3\\src\\javaapplication3\\raw_data.json";             
            //Enter the path where you want to create a file with name as rules.json.
            String rulePath="C:\\Desktop\\RuleEngine\\JavaApplication3\\src\\javaapplication3\\rules.json";  
            FileWriter file = new FileWriter(rulePath);        
                
            JSONArray rules = new JSONArray();
            while(choice.equalsIgnoreCase("yes")){
                boolean matcher;
                String signalRule;
                String valueTypeRule;
                String conditionRule;
                String valueRule="";
                //Asking for the signalRule until correct pattern signal is returned to it.
                do{
                    System.out.println("Enter the signal {alphanumeric e.g : ATL2 OR A2}: ");
                    signalRule = scan.next();                
                    matcher = pattern.matcher(signalRule).matches();                
                    if(!matcher) System.out.println("Enter the correct signal {alphanumeric e.g : ATL2 OR A2} : "); 
                }while(!matcher);
                //Asking for the conditionRule until correct pattern signal is returned to it.
                do{
                    System.out.println("Enter the condition from : < , > , <> , = ");
                    conditionRule = scan.next();                
                    matcher = cpattern.matcher(conditionRule).matches();                
                    if(!matcher) System.out.println("Enter the correct condition from : < , > , <> , = "); 
                }while(!matcher);
                //Asking for the valueTypeRule until correct pattern signal is returned to it.
                do{
                    System.out.println("Enter the value type: Integer, String, Datetime ");
                    valueTypeRule = scan.next();               
                    matcher = vtpattern.matcher(valueTypeRule).matches();                
                    if(!matcher) System.out.println("Enter the correct value type: Integer, String, Datetime "); 
                }while(!matcher);
                //Asking for the valueRule until correct pattern signal is returned to it.
                if(valueTypeRule.equalsIgnoreCase("integer"))
                {
                    do{
                        System.out.println("Enter the value: ");
                        valueRule = scan.next();               
                        matcher = vpattern.matcher(valueRule).matches();                
                        if(!matcher) System.out.println("Enter the correct value:"); 
                    }while(!matcher);
                }
                else if(valueTypeRule.equalsIgnoreCase("datetime"))
                {
                    do{
                        System.out.println("Enter the value in this format (YYYY-MM-DD HH:MM:SS): ");
                        valueRule = scan.next();                          
                        matcher = dtpattern.matcher(valueRule).matches();                
                        if(!matcher) System.out.println("Enter the correct value in this format (YYYY-MM-DD HH:MM:SS):"); 
                    }while(!matcher);                    
                }
                else if(valueTypeRule.equalsIgnoreCase("string"))
                {
                    do{
                        System.out.println("Enter the value : ");
                        valueRule = scan.next();               
                        matcher = spattern.matcher(valueRule).matches();                
                        if(!matcher) System.out.println("Enter the correct value :"); 
                    }while(!matcher);                                 
                }      

        
                //creating new JSON object
                JSONObject obj = new JSONObject();
                obj.put("signalRule", signalRule);
                obj.put("conditionRule", conditionRule);
                obj.put("valueTypeRule", valueTypeRule);
                obj.put("valueRule", valueRule);
                rules.add(obj);

                System.out.println("Do you want to add more rules? Yes/No");
                choice = scan.next();
            }
            long startTime = System.currentTimeMillis();
            file.write(rules.toJSONString());
            file.flush(); //if somedata stored as buffered and doesn't get written on file then it will write those buffered data.
                        
            
            JSONParser parser = new JSONParser();
            JSONArray inputs = (JSONArray) parser.parse(new FileReader(rawDataPath));

            JSONParser parser2 = new JSONParser();
            JSONArray rulesInput = (JSONArray) parser2.parse(new FileReader(rulePath));


            int inputLength = inputs.size();
            //This will store the violated signals.
            //it won't allow duplicates because of set property.
            Set<String> vSignals = new HashSet<String>();
            
            //In this nested-for loop every raw_data obj is checked with every ruleinputs obj.  
            for (Object o : inputs){
                JSONObject input = (JSONObject) o;
                String signal = (String) input.get("signal");
                String valueType = (String) input.get("value_type");
                String value = (String) input.get("value");

                for (Object obj : rulesInput){

                   JSONObject rule = (JSONObject) obj;
                   String signalRule = (String) rule.get("signalRule");
                   String conditionRule = (String) rule.get("conditionRule");
                   String valueTypeRule = (String) rule.get("valueTypeRule");
                   String valueRule = (String) rule.get("valueRule");

                   //System.out.println("Parsing : " + signal + " " + valueType +" "+ value + " against: "+ signalRule + " " + valueTypeRule +" "+ valueRule);

                   if(!signalRule.equalsIgnoreCase(signal) || !valueTypeRule.equalsIgnoreCase(valueType))
                       continue;
                   Integer vRule;
                   Integer val;
                   if(valueTypeRule.equalsIgnoreCase("Integer")){
                    switch(conditionRule){                   
                         //This " < " Condition will check whether value > valueRule or not. 
                        case "<" : vRule = (int)Double.parseDouble(valueRule); 
                                    val = (int)Double.parseDouble(value);
                                    if(!(val < vRule))
                                     vSignals.add(signalRule);
                                   break;
                         //This " > " Condition will check whether value < valueRule or not.
                        case ">" :  vRule = (int)Double.parseDouble(valueRule);
                                    val = (int)Double.parseDouble(value);
                                    if(!(val > vRule))
                                     vSignals.add(signalRule);
                                   break;
                         //This " <> " Condition will check whether value != valueRule, if equal then rule violated.
                        case "<>" : vRule = (int)Double.parseDouble(valueRule);
                                    val = (int)Double.parseDouble(value);
                                    if(Objects.equals(val, vRule))
                                     vSignals.add(signalRule);
                                   break;
                         //This " = " Condition will check whether value == valueRule, if not equal then rule violated.
                        case "=" : vRule = (int)Double.parseDouble(valueRule);
                                    val = (int)Double.parseDouble(value);
                                    if(!(Objects.equals(val, vRule)))
                                     vSignals.add(signalRule);
                                   break;
                        //if No above cases follows 
                        default  : System.out.println("Wrong rule condition : " + conditionRule);
                                   break; 
                    }
                   }
                   else if(valueTypeRule.equalsIgnoreCase("Datetime")){
                    switch(conditionRule){
                        //This " < " Condition will check whether value >= valueRule or not. 
                        case "<" : if((format.parse(value).compareTo(format.parse(valueRule))) >= 0)
                                     vSignals.add(signalRule);
                                   break;
                        //This " > " Condition will check whether value <= valueRule or not.
                        case ">" : if((format.parse(value).compareTo(format.parse(valueRule))) <= 0)
                                     vSignals.add(signalRule);
                                   break;
                        //This " <> " Condition will check whether value != valueRule, if equal then rule violated.
                        case "<>" : if((format.parse(value).compareTo(format.parse(valueRule))) == 0)
                                     vSignals.add(signalRule);
                                   break;
                        //This " = " Condition will check whether value == valueRule, if not equal then rule violated.
                        case "=" : if((format.parse(value).compareTo(format.parse(valueRule))) != 0)
                                     vSignals.add(signalRule);
                                   break;
                        default  : System.out.println("Wrong rule condition : " + conditionRule);
                                   break; 
                    }
                   }
                   else if(valueTypeRule.equalsIgnoreCase("String")){
                    switch(conditionRule){
                        //if " < " then rule violated
                        case "<" : vSignals.add(signalRule);
                                   break;
                        //if " > " then rule violated
                        case ">" : vSignals.add(signalRule);
                                   break;           
                        //This " <> " Condition will check whether value != valueRule, if equal then rule violated.
                        case "<>" : if(value.equalsIgnoreCase(valueRule))
                                     vSignals.add(signalRule);
                                   break;
                        //This " = " Condition will check whether value == valueRule, if not equal then rule violated.
                        case "=" : if(!(value.equalsIgnoreCase(valueRule)))
                                     vSignals.add(signalRule);
                                   break;
                        default  : System.out.println("Wrong rule condition : "  + conditionRule);
                                   break; 
                    }
                   }
                } 
            }
            //prints all the violated signals matching with raw_data.json signals.
            System.out.println("Signals violated : ");
            System.out.println(vSignals);
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Time Taken to perform the task : " + elapsedTime + " ms");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        
    }   
}
