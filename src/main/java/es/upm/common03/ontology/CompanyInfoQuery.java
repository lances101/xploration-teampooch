package es.upm.common03.ontology;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: CompanyInfoQuery
* @author ontology bean generator
* @version 2016/06/4, 20:00:25
*/
public class CompanyInfoQuery implements AgentAction {

   /**
* Protege name: company_number
   */
   private int company_number;
   public void setCompany_number(int value) { 
    this.company_number=value;
   }
   public int getCompany_number() {
     return this.company_number;
   }

}
