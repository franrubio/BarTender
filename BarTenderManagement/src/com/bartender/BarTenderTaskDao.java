package com.bartender;

import java.util.List;

public class BarTenderTaskDao {

   public BarTenderTask getBarTenderTask(int customerId, List<BarTenderTask> barTenderTasks) {

      for (BarTenderTask barTenderTask: barTenderTasks) {
         if(barTenderTask.getCustomerId() == customerId){
            return barTenderTask;
         }
      }
      return null;
   }
   

   public int addBarTenderTask (BarTenderTask pBarTenderTask, List<BarTenderTask> barTenderTaskList) {
      
      boolean bTenderTaskExists = false;
      for (BarTenderTask barTenderTask: barTenderTaskList) {
         if (barTenderTask.getCustomerId() == pBarTenderTask.getCustomerId()
        	 && barTenderTask.getDrinkType().equals(pBarTenderTask.getDrinkType())) {
        	 bTenderTaskExists = true;
            break;
         }
      }		
      if (!bTenderTaskExists) {
    	  barTenderTaskList.add(pBarTenderTask);
    	  return 1;
      }
      return 0;
   }
}
