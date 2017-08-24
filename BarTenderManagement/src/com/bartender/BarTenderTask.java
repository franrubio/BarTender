package com.bartender;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bartendertask")
public class BarTenderTask implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int customerId;
    private String drinkType;

    public BarTenderTask() {}

    
	public BarTenderTask(int customerId, String drinkType) {
		this.setCustomerId(customerId);
		this.drinkType = drinkType;
	}

	public int getCustomerId() {
		return customerId;
	}

	@XmlElement
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
    public String getDrinkType() {
		return drinkType;
	}

    @XmlElement
	public void setDrinkType(String drinkType) {
		this.drinkType = drinkType;
	}
    
    @Override
    public boolean equals(Object object){
       if (object == null) {
          return false;
       } else if (!(object instanceof BarTenderTask)) {
          return false;
       } else {
    	   BarTenderTask barTenderTask = (BarTenderTask) object;
           if(customerId == barTenderTask.getCustomerId()
             && drinkType.equals(barTenderTask.getDrinkType())) {
             return true;
          }			
       }
       return false;
    }	
}
