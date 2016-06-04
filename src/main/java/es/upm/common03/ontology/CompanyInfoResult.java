package es.upm.common03.ontology;


import es.upm.ontology.Company;
import jade.content.AgentAction;

/**
* Protege name: CompanyInfoResult
* @author ontology bean generator
* @version 2016/06/4, 20:00:25
*/
public class CompanyInfoResult implements AgentAction {

   /**
* Protege name: result
   */
   private Company result;
   public void setResult(Company value) { 
    this.result=value;
   }
   public Company getResult() {
     return this.result;
   }


}
