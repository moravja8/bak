package services;

import services.Impl.KnimeLogCostsMapperService;
import services.Impl.KnimeNodeServiceImpl;
import services.Impl.KnimeWorkflowServiceImpl;

/**
 * not thread safe
 * Created by cloudera on 3/29/16.
 */
public class ServiceFactory {

    private static KnimeNodeService knimeNodeService = null;
    private static KnimeWorkflowService knimeWorkflowService = null;
    private static KnimeCostsMapperService knimeCostsMapperService = null;

    public static KnimeNodeService getKnimeNodeService() {
        if(knimeNodeService == null){
            knimeNodeService = new KnimeNodeServiceImpl();
        }
        return knimeNodeService;
    }

    public static KnimeWorkflowService getKnimeWorkflowService() {
        if(knimeWorkflowService == null){
            knimeWorkflowService = new KnimeWorkflowServiceImpl();
        }
        return knimeWorkflowService;
    }

    public static KnimeCostsMapperService getKnimeCostsMapperService() {
        if(knimeCostsMapperService == null){
            knimeCostsMapperService = new KnimeLogCostsMapperService();
        }
        return knimeCostsMapperService;
    }
}
