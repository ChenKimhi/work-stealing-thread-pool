package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chen on 31-Dec-16.
 */
public class ManufactureTask extends Task {

    /*package*/ Warehouse warehouse;
    /*package*/ Product product;
    /*package*/ AtomicInteger toolsCounter;


    public ManufactureTask(Warehouse warehouse, Product product) {
        super();
        this.warehouse = warehouse;
        this.product = product;
        toolsCounter = new AtomicInteger(warehouse.getPlan(product.getName()).getTools().length);
    }

    /**
     * main method of the task
     */
    @Override
    protected void start() {
        ManufactoringPlan myProductPlan = warehouse.getPlan(product.getName());
        String[] planParts = myProductPlan.getParts();
        for (int i = 0; i < planParts.length; i++)
        {    //build the parts of the product built by this task with startId increased by one
            product.addPart(new Product(product.getStartId() + 1, planParts[i]));
        }

        ArrayList<Product> productParts = (ArrayList<Product>) product.getParts();
        ArrayList<ManufactureTask> tasksOfParts = new ArrayList<>();
        int numOfParts = productParts.size();
        if (numOfParts > 0) {
            for (int i = 0; i < numOfParts; i++)
            {  //creating tasks for the parts
                ManufactureTask newTask = new ManufactureTask(warehouse, productParts.get(i));
                spawn(newTask);
                tasksOfParts.add(newTask);
            }

            whenResolved(tasksOfParts, () -> {
                //CountDownLatch toolsDownLatch = new CountDownLatch(myProductPlan.getTools().length);
                AtomicInteger toolsCounter = new AtomicInteger(myProductPlan.getTools().length);
                for (String toolType : myProductPlan.getTools() ) {
                    Deferred<Tool> deferredTool = warehouse.acquireTool(toolType);
                    deferredTool.whenResolved(() -> {
                        product.incFinalId(deferredTool.get().useOn(product));
                        //warehouse.releaseTool(deferredTool.get());
                        //toolsDownLatch.countDown();
                        //toolsCounter.decrementAndGet();
                        ReleaseToolTask releaseToolTask = new ReleaseToolTask(warehouse, deferredTool.get());
                        releaseToolTask.getResult().whenResolved(() -> {
                            if(toolsCounter.decrementAndGet() == 0){
                                this.complete(product);
                            }
                        });
                        spawn(releaseToolTask);


                    });

                }
                //if(count.get() == 0) {
                //    complete(product);
                //}
                if (toolsCounter.get() == 0)
                    complete(product);

                /*
                try {
                    toolsDownLatch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }*/
                //all tools got used and final id is final!
                //complete(product);
            });

        }
        else    //case there's no more parts to spawn ("atomic part/product")
            complete(product);

    }
}
