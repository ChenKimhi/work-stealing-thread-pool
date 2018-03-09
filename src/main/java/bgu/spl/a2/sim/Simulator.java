/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;
//import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.ManufactureTask;
//import bgu.spl.a2.sim.tools.GcdScrewDriver;
//import bgu.spl.a2.sim.tools.NextPrimeHammer;
//import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


/**
 * A class describing the simulator for part 2 of the assignment
 */

public class Simulator {
    private ConcurrentLinkedQueue<Product> products;
    private static WorkStealingThreadPool simulationPool;
    private static Order[][] waves;
    private static Warehouse warehouse;
    private static int[] numOfProductsPerWave;


    /**
     * Begin the simulation
     * Should not be called before attachWorkStealingThreadPool()
     *
     */

    public static ConcurrentLinkedQueue<Product> start(){
        ConcurrentLinkedQueue<Product> simulationResult = new ConcurrentLinkedQueue<>();
        simulationPool.start();

        for (int i = 0; i < waves.length; i++)
        {    //for each wave
            ArrayList<ManufactureTask> waveTasksByOrder = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(numOfProductsPerWave[i]);     //shouldn't proceed to next wave before all current wave tasks are done.
            for (int j = 0; j < waves[i].length; j++)
            {     //for each order in the wave
                Order currentOrder = waves[i][j];
                int quantity = currentOrder.getQty();
                long currentOrderStartId = currentOrder.getStartId();
                for (long q = 0; q < quantity; q++) {
                    ManufactureTask task = new ManufactureTask(warehouse, new Product(currentOrderStartId + q, currentOrder.getProduct()));
                    waveTasksByOrder.add(task);
                    simulationPool.submit(task);
                    task.getResult().whenResolved(() -> {
                        latch.countDown();
                    });
                }
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //all current wave's tasks are done - adding the products to the queue in proper sequence
            for (int j = 0; j < waveTasksByOrder.size(); j++) {
                ManufactureTask currTask = waveTasksByOrder.get(j);
                simulationResult.add((Product) currTask.getResult().get());
            }

        }
        try {
            simulationPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return simulationResult;
    }


    /**
     * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
     *
     * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
     */

    public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool){
        simulationPool = myWorkStealingThreadPool;
    }

    public static void main(String[] args){
        //reading json file
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("file not found!  )= ");
        }
        Gson myGson = new GsonBuilder().setPrettyPrinting().create();
        JReader jReader = myGson.fromJson(reader, JReader.class);
        //initializes the simulator
        init(jReader);

        ConcurrentLinkedQueue<Product> SimulationResult;
        SimulationResult = start();

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("result.ser");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.writeObject(SimulationResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * initializes the {@link Warehouse}, the products {@link #waves} and the {@link WorkStealingThreadPool}
     * of the simulator, according to the input file
     * @param jReader class we created according to the input JSON file
     */
    private static void init(JReader jReader) {
        int nThreads = jReader.getThreads();
        attachWorkStealingThreadPool(new WorkStealingThreadPool(nThreads));
        warehouse = new Warehouse();
        Jtool[] tools = jReader.getTools();
        ManufactoringPlan[] plans = jReader.getPlans();
        for(Jtool jtool: tools){
            String type = jtool.getTool();

            switch (type) {
                case "rs-pliers":
                    warehouse.addTool(new RandomSumPliers(), jtool.getQty());
                    break;

                case "gs-driver":
                    warehouse.addTool(new GcdScrewDriver(), jtool.getQty());
                    break;

                case "np-hammer":
                    warehouse.addTool(new NextPrimeHammer(), jtool.getQty());
                    break;

                default:
                    System.out.println("warehouse init got unexpected tool type");
                    break;
            }
        }
        for(ManufactoringPlan plan : plans){
            warehouse.addPlan(plan);
        }


        waves = jReader.getWaves();
        //building numOfProductsPerWave
        numOfProductsPerWave  = new int[waves.length];
        for (int i = 0; i < waves.length; i++) {
            int sumWave = 0;
            for (int j = 0; j < waves[i].length; j++) {
                sumWave += waves[i][j].getQty();
            }
            numOfProductsPerWave[i] = sumWave;
        }

    }


}

