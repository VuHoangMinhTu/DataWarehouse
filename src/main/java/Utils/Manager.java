package Utils;


import Controllers.Db_Control;
import Controllers.Db_Staging;
import Controllers.Db_Warehouse;

import java.util.List;

public class Manager {
    private Db_Control db_control = new Db_Control();
    private Db_Staging db_staging = new Db_Staging();
    private Db_Warehouse db_warehouse = new Db_Warehouse();


//    1
    public void checkFileCsv(){
        db_control.checkFileCSVInLog();
    }
//    2

    public void loadDataFromCSVToTempStaging(){
        String sourceFile = db_control.sourceFileLocation;
        List<String> listFileName = db_control.getFileNameCSVReadyToTempStaging();
        if(listFileName.size() ==0){
            System.out.println("None of any file to load");
            return;
        }else{
            db_staging.truncateTableOfStaging("temp_kyma_cameras");
            for (String fileName : listFileName) {
                boolean result =db_staging.loadDataFromCSVToTempStaging(sourceFile,fileName);
                if(result == false){
                    System.out.println("The error occur when system is loading file "+fileName);
                    break;
                }

            }
            System.out.println("Load file thành công");
        }
    }
//    3
    public void setStatusForFileCSVReadyToTempStaging(){
        db_control.setStatusForFileCSVReadyToTempStaging();
    }
    //4
    public void loadDataFromTempStagingToMainStaging(){
        boolean result = db_control.checkStatusExtractToTempStaging();
        if(result == true){
            db_staging.truncateTableOfStaging("daily_kyma_cameras");
            db_staging.loadDataFromTempStagingToMainStaging();
        }else {
            System.out.println("None of any log which has status EXTRACT_TO_TEMP_STAGING");
            return;
        }
    }
    //5
    public void setStatusTempStagingToMainStaging(){
        db_control.setStatusTempStagingToMainStaging();
    }
    //6
    public void loadDataFromMainStagingToTempDW(){
        boolean result = db_control.checkStatusExtractToMainStaging();
        if(result == false){
            System.out.println("None of any log which has status EXTRACT_TO_MAINSTAGING");
            return;
        }else{
            db_warehouse.loadDataFromMainStagingToTempDW();
        }

    }
    //7
    public void setStatusToTempDW(){
        db_control.setStatusToTempDW();
    }
    //8
    public void loadDataFromTempDWToMainDW(){
        boolean result = db_control.checkStatusExtractToTempDW();
        if(result == false){
            System.out.println("None of any logs which has status EXTRACT_TO_TEMP_DW");
            return;
        }else{
            db_warehouse.loadDataFromTempDWToMainDW();
        }
    }
    public void setStatusToMainDW(){
        db_control.setStatusToMainDW();
    }

    public void loadDataFromDWtoAggregateFilter(){
        db_warehouse.loadDataFromDWtoAggregateFilter();
    }

    public void run(){
        Manager manager = new Manager();
        manager.checkFileCsv();
        manager.loadDataFromCSVToTempStaging();
        manager.setStatusForFileCSVReadyToTempStaging();
        manager.loadDataFromTempStagingToMainStaging();
        manager.setStatusTempStagingToMainStaging();
        manager.loadDataFromMainStagingToTempDW();
        manager.setStatusToTempDW();
        manager.loadDataFromTempDWToMainDW();
        manager.setStatusToMainDW();
        manager.loadDataFromDWtoAggregateFilter();
    }

}
