package es.upm.common03;

import jade.core.AID;

public class CompanyAIDTuple {
    private String companyName;
    private AID company;
    private AID capsule;
    private AID rover;

    public CompanyAIDTuple(String companyName, AID company) {
        this.companyName = companyName;
        this.company = company;
    }

    public CompanyAIDTuple(String companyName, AID company, AID capsule, AID rover) {
        this.companyName = companyName;
        this.company = company;
        this.capsule = capsule;
        this.rover = rover;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public AID getCompany() {
        return company;
    }

    public void setCompany(AID company) {
        this.company = company;
    }

    public AID getCapsule() {
        return capsule;
    }

    public void setCapsule(AID capsule) {
        this.capsule = capsule;
    }

    public AID getRover() {
        return rover;
    }

    public void setRover(AID rover) {
        this.rover = rover;
    }
}
