package services;

import java.util.ArrayList;

/**
 * Created by cloudera on 4/11/16.
 */
public interface KnimeCostsMapperService {
    String mapCostsFromLog(ArrayList<String> inputLog, String db, String table, String wf);
    ArrayList<String[]> createCostMap(ArrayList<String> filteredLog, String db, String table, String wf);
}
