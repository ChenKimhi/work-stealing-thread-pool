package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.Deferred;
import java.util.ArrayDeque;
import java.util.HashMap;

/**
 * A class representing the warehouse in your simulation
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {

	private ArrayDeque<RandomSumPliers> rs;
	private ArrayDeque<GcdScrewDriver> gs;
	private ArrayDeque<NextPrimeHammer> np;
	private HashMap<String, ManufactoringPlan> plans;
	private ArrayDeque<Deferred<Tool>> rsWaitingList, gsWaitingList, npWaitingList;

	/**
	 * Constructor
	 */


	public Warehouse() {
		rs = new ArrayDeque<RandomSumPliers>();
		gs = new ArrayDeque<GcdScrewDriver>();
		np = new ArrayDeque<NextPrimeHammer>();
		this.plans = new HashMap<String, ManufactoringPlan>();
		rsWaitingList = new ArrayDeque<Deferred<Tool>>();
		gsWaitingList = new ArrayDeque<Deferred<Tool>>();
		npWaitingList = new ArrayDeque<Deferred<Tool>>();

	}

	/**
	 * Tool acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * @param type - string describing the required tool
	 * @return a deferred promise for the  requested tool
	 *
	 * we add synchronized to prevent the option that someone release tool and we not add Ourselves to the deferred queue yet.
	 */
	public synchronized Deferred<Tool> acquireTool(String type){

		Deferred<Tool> toReturn = new Deferred<Tool>();
		switch (type) {
			case "rs-pliers":
				if(rs.isEmpty()){
					rsWaitingList.add(toReturn);
					return toReturn;
				}
				else{
					toReturn.resolve(rs.pop());
					return toReturn;
				}

			case "gs-driver":
				if(gs.isEmpty()){
					gsWaitingList.add(toReturn);
					return toReturn;
				}
				else{
					toReturn.resolve(gs.pop());
					return toReturn;
				}

			case "np-hammer":
				if(np.isEmpty()){
					npWaitingList.add(toReturn);
					return toReturn;
				}
				else{
					toReturn.resolve(np.pop());
					return toReturn;
				}

			default:
				//TODO throw exception
				System.out.println("error: acquireTool got unexpected tool type");
				return null;


		}
	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	 * @param tool - The tool to be returned
	 */
	public synchronized void releaseTool(Tool tool){

		switch (tool.getType()) {
			case "rs-pliers":
				if(rs.isEmpty() && !rsWaitingList.isEmpty()){
					rsWaitingList.pop().resolve(tool);
				}
				else
					rs.add((RandomSumPliers) tool);
				break;

			case "gs-driver":
				if(gs.isEmpty() && !gsWaitingList.isEmpty()){
					gsWaitingList.pop().resolve(tool);
				}

				else
					gs.add((GcdScrewDriver) tool);
				break;

			case "np-hammer":
				if(np.isEmpty() && !npWaitingList.isEmpty()){
					npWaitingList.pop().resolve(tool);
				}
				else
					np.add( (NextPrimeHammer) tool);
				break;

			default:
				break;
		}
	}


	/**
	 * Getter for ManufactoringPlans
	 * @param product - a string with the product name for which a ManufactoringPlan is desired
	 * @return A ManufactoringPlan for product
	 */
	public ManufactoringPlan getPlan(String product){
		return plans.get(product);
	}

	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 * @param plan - a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan){
		String key = plan.getProduct();
		plans.put(key, plan);
	}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later retrieval
	 * @param tool - type of tool to be stored
	 * @param qty - amount of tools of type tool to be stored
	 */
	public void addTool(Tool tool, int qty){

		switch (tool.getType()) {
			case "rs-pliers":
				for(int i = 0; i < qty; i++){
					releaseTool(tool);
				}
				break;

			case "gs-driver":
				for(int i = 0; i < qty; i++){
					releaseTool(tool);
				}
				break;

			case "np-hammer":
				for(int i = 0; i < qty; i++){
					releaseTool(tool);
				}
				break;

			default:
				break;
		}

	}

}