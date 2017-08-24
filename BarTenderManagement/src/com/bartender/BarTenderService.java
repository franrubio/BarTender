package com.bartender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/BarTenderService")
@Produces("application/json")
public class BarTenderService {
	
   BarTenderTaskDao barTenderTaskDao = new BarTenderTaskDao();   
   private static List<BarTenderTask> listaTask = new ArrayList<BarTenderTask>();
   
   // The barman can prepare at once 2 beers (drinks of BEER type) or 1 drink (DRINK type)  
   private ThreadPoolExecutor beerPoolExecutor = new ThreadPoolExecutor(2, 2, 5, TimeUnit.SECONDS,  new LinkedBlockingQueue<Runnable>(2));
      
   public BarTenderService() {
	   super();
   }
	 
   @POST
   @Path("{customernumber}/{drinktype}")
   public void addBarTenderTask(@PathParam("customernumber")int customernumber,
			@PathParam("drinktype")String drinktype, 
			final @Suspended AsyncResponse asyncResponse) {
   	
	// Drink type must be BEER or DRINK	    
	if (drinktype != null && (drinktype.equals("BEER") || drinktype.equals("DRINK"))) {
		// Preparing one drink takes X seconds (5 by default but value should be configurable) regardless of drink type 
		asyncResponse.setTimeout(5, TimeUnit.SECONDS);
		
		final BarTenderTask barTenderTask = new BarTenderTask(customernumber, drinktype);
		
		// Drink request should get the response as soon as barman starts to prepare a drink. It should not be delayed for the time of the drink preparation.
		if (drinktype.equals("BEER")) {
			beerPoolExecutor.submit(new Runnable() {
				@Override
				public void run() { 
					Response response = getTaskResponse(beerPoolExecutor, barTenderTask);
					asyncResponse.resume(response);
				}
	        });
		} else if (drinktype.equals("DRINK")) {
			new Thread() {
		         public void run() {
		        	 try {
		        		barTenderTaskDao.addBarTenderTask(barTenderTask, listaTask);					
		        	 	asyncResponse.resume(Response.status(200).entity(listaTask).build());
		              } catch (Exception ex) {
		            	 asyncResponse.resume(Response.status(429).entity(listaTask).build());
		              }

		         }
		      }.start();
		}
	}
   }	
	
   @GET
   @Path("/bartendertasks")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getBarTenderTasks() {
	  // endpoint with list of served drinks
      return Response.ok().entity(listaTask).build();
   }

   @GET
   @Path("/bartendertasks/{customerId}")
   @Produces(MediaType.APPLICATION_JSON)
   public BarTenderTask getBarTenderTask(@PathParam("customerId") int customerId) {
	  // Retrieve task for customerId
      return barTenderTaskDao.getBarTenderTask(customerId, listaTask);
   }
   
   //   1. respond with 200 code when ordered drink will be served
   //   2. respond with 429 code when order is not accepted at the moment
   private Response getTaskResponse(ThreadPoolExecutor executor, BarTenderTask barTenderTask) {
	   // The barman can prepare at once 2 beers (drinks of BEER type) or 1 drink (DRINK type)
	   if (executor.getActiveCount() <= executor.getMaximumPoolSize()) {
			barTenderTaskDao.addBarTenderTask(barTenderTask, listaTask);
			return Response.status(200).entity(listaTask).build();
       } else {
       	return Response.status(429).entity(listaTask).build();
       }
   }
}

