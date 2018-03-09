package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.tools.Tool;

public class ReleaseToolTask extends Task<Integer>{

    private Warehouse warehouse;
    private Tool tool;


    public ReleaseToolTask(Warehouse warehouse, Tool tool) {
        this.warehouse = warehouse;
        this.tool = tool;
    }

    @Override
    protected void start() {
        warehouse.releaseTool(tool);
        complete(new Integer(0));
    }

}
