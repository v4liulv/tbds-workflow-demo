package com.tencent.tbds.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencent.tbds.api.util.https.HttpsClient;
import com.tencent.tbds.api.util.https.HttpsUpdateClient;
import com.tencent.tbds.api.util.props.PropertiesUtils;
import com.tencent.tbds.api.util.tbds.AccessUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author liulv
 */
public class WorkflowDemo {

    public static Properties properties;
    public static String accessAuthHeader;
    public static String TBDS_PORTAL_IP;
    public static Map<String, String> headers;
    public static String saveFile;

    static {
        properties = PropertiesUtils.getProperties();
        try {
            accessAuthHeader = AccessUtils.getAccessAuthHeader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TBDS_PORTAL_IP = properties.getProperty("tbds_proter_ip");
        headers = new HashMap<>();
        headers.put("Authorization", accessAuthHeader);
        saveFile = properties.getProperty("tbds_workflow_save_file");
    }

    public static String list(String workflowName) {
        String workflowId = null;
        try {
            String url = TBDS_PORTAL_IP + properties.getProperty("tbds_workflow_list_url");

            Map<String, String> params = new HashMap<>();
            params.put("condition[keyword]", workflowName);

            String jsonStr = HttpsClient.doGet(url, headers, params);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);

            int resultNum = jsonObject.getJSONObject("resultData").getInteger("numberOfElements");

            if (resultNum > 0) {
                workflowId = jsonObject.getJSONObject("resultData").getJSONArray("content").getJSONObject(0).getString("workflowId");
            } else {
                System.err.println("查询无此工作流");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return workflowId;
    }

    /**
     * 创建工作流
     */
    public static void create(String xmlFile) {
        try {
            String url = TBDS_PORTAL_IP + properties.getProperty("tbds_workflow_create_url");
            String filePath = PropertiesUtils.CLASS_PATH + xmlFile;
            HttpsUpdateClient.postString(url, headers, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布工作流
     *
     * @param workflowId 工作流ID
     */
    public static void deploy(String workflowId) {
        try {
            String url = TBDS_PORTAL_IP + properties.getProperty("tbds_workflow_deploy_url");

            Map<String, String> params = new HashMap<>();
            params.put("workflowId", workflowId);

            HttpsClient.doPost(url, headers, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动工作流
     */
    public static void start(String workflowId) {
        try {
            String url = TBDS_PORTAL_IP + properties.getProperty("tbds_workflow_run_url");
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(workflowId);

            HttpsClient.doPost(url, headers, jsonArray.toJSONString(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新工作流，暂时不支持
     */
    public static void update() {
        try {
            String url = TBDS_PORTAL_IP + properties.getProperty("tbds_workflow_update_url");
            String filePath = PropertiesUtils.CLASS_PATH + properties.getProperty("tbds_workflow_update_file");
            HttpsUpdateClient.postString(url, headers, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止工作流
     *
     * @param workflowId 工作流ID
     */
    public static void stop(String workflowId) {
        try {
            String url = TBDS_PORTAL_IP + properties.getProperty("tbds_workflow_stop_url");

            JSONArray jsonArray = new JSONArray();
            jsonArray.add(workflowId);

            HttpsClient.doPost(url, headers, jsonArray.toJSONString(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除工作流
     *
     * @param workflowId 工作流ID
     */
    public static void delete(String workflowId) {
        try {
            String url = TBDS_PORTAL_IP + properties.getProperty("tbds_workflow_delete_url");

            Map<String, String> params = new HashMap<>();
            params.put("workflowId", workflowId);

            HttpsClient.doGet(url, headers, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String publicHttps(String url, String method, Map<String, String> params) throws Exception {
        String resultStr = "";
        if (method.equals("GET")) {
            resultStr = HttpsClient.doGet(url, headers, params);
        } else if (method.equals("POST")) {
            resultStr = HttpsClient.doPost(url, headers, params);
        }
        return resultStr;
    }

    public static void publicHttps(String url, String method, String jsonFile, boolean isJsonStr) throws Exception {
        if (method.equals("POST")) {
            HttpsClient.doPost(url, headers, jsonFile, isJsonStr);
        }
    }

    /**
     * 查询工作流 task列表
     *
     * @param workflowName 工作流名称
     * @return task列表json
     */
    public static String queryTaskList(String workflowName) throws Exception {
        String workflowId = list(workflowName);

        String method = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("condition[workflowId]", workflowId);
        String url = properties.getProperty("tbds_proter_ip") + properties.getProperty("tbds_task_url");
        return publicHttps(url, method, params);
    }

    /**
     * 获取task立即执行请求报文
     *
     * @param workflowName 工作流名称
     * @param taskName task 名称
     * @return 请求报文json
     */
    public static String getTaskRunJson(String workflowName, String taskName) throws Exception {
        String tasklist = queryTaskList(workflowName);

        JSONObject jsonObject  = JSONObject.parseObject(tasklist);
        JSONObject resultData = jsonObject.getJSONObject("resultData");
        JSONArray content = resultData.getJSONArray("content");

        JSONArray jsonArrays = new JSONArray();
        for (int i = 0; i< content.size(); i++){
            JSONObject jsonObject1 = content.getJSONObject(i);
            String taskName1 = jsonObject1.getString("taskName");
            if(taskName.equals(taskName1)){

                JSONObject jsonObjects = new  JSONObject();
                jsonObjects.put("taskId", jsonObject1.getString("taskId"));
                jsonObjects.put("dataTime", stampToDate(jsonObject1.getString("dataStartTime")));
                jsonArrays.add(jsonObjects);
            }
        }
        return jsonArrays.toJSONString();
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 立即触发工作流task
     *
     * @param workflowName  工作流名称
     * @param taskName task 名称
     */
    public static void triggerTask(String workflowName, String taskName) throws Exception {
        String requestJsonStr = getTaskRunJson(workflowName, taskName);
        String method = "POST";
        String url = properties.getProperty("tbds_proter_ip") + properties.getProperty("tbds_task_rerun");
       publicHttps(url, method, requestJsonStr, true);
    }


    //////////////////////////////////////////////////////////////
    /**
     * 获取taskID
     *
     * @param workflowName 工作流名称
     * @param taskName task 名称
     * @return 请求报文json
     */
    public static String getTaskID(String workflowName, String taskName) throws Exception {
        String taskList = queryTaskList(workflowName);
        String taskId = null;

        JSONObject jsonObject  = JSONObject.parseObject(taskList);
        JSONObject resultData = jsonObject.getJSONObject("resultData");
        JSONArray content = resultData.getJSONArray("content");

        for (int i = 0; i< content.size(); i++){
            JSONObject jsonObject1 = content.getJSONObject(i);
            String taskName1 = jsonObject1.getString("taskName");
            if(taskName.equals(taskName1)){
                taskId = jsonObject1.getString("taskId");
            }
        }
        return taskId;
    }

    /**
     * 查看任务实例情况
     *
     * @param workflowName 工作流名
     * @param taskName 任务名称
     */
    public static String taskInstanceList(String workflowName, String taskName) throws Exception {
        String taskID = getTaskID(workflowName, taskName);
        String method = "GET";
        Map<String, String> params = new HashMap<>();
        params.put("pageIndex", "0");
        params.put("pageSize", "5");
        params.put("condition[taskParam]", taskID);
        //params.put("condition[status]", "2");
        String url = properties.getProperty("tbds_proter_ip") + properties.getProperty("tbds_task_instance_url");
        return publicHttps(url, method, params);
    }

    /**
     * 任务实例日志信息
     *
     * @param workflowName 工作流名
     * @param taskName 任务名
     */
    public static String taskInstanceLogInfo(String workflowName, String taskName) throws Exception {
        String taskID = getTaskID(workflowName, taskName);
        String method = "GET";

        Map<String, String> params = new HashMap<>();
        params.put("taskId", taskID);
        String listLogListURL = properties.getProperty("tbds_proter_ip") + properties.getProperty("tbds_task_instance_log_list");
        String taskInstanceList = taskInstanceList(workflowName, taskName);

        JSONObject rRoot = JSONObject.parseObject(taskInstanceList);
        //任务实例JSONArray
        JSONArray content = rRoot.getJSONObject("resultData").getJSONArray("content");

//////////////////////////////////////////////////////////////////////////////////////////////////////
        //最新的那个任务实例getJSONObject(1)
        String dataTime = content.getJSONObject(1).get("dataTime").toString();
        params.put("dataTime", dataTime);
        String listLogList = publicHttps(listLogListURL, method, params);
        //{
        //   "resultCode":"0",
        //   "message":null,
        //   "resultData":
        //   [
        //      "18K,
        //      2020-05-13 09:01:22,
        //      172.27.0.124,
        //      1"
        //   ],

        //   "markInfo":null
        //}
        // --------------------------------------


        //params.put("condition[status]", "2");
        String url = properties.getProperty("tbds_proter_ip") + properties.getProperty("tbds_task_instance_log_info");
        //查询单个任务实例下的任务列表
        String resultData = JSONObject.parseObject(listLogList).getJSONArray("resultData").get(0).toString();

        //任务调度器的代理服务器IP，也就是任务实例执行的客户端IP
        params.put("ip", resultData.split(",")[2]);
        params.put("tries", resultData.split(",")[3]);

        System.out.println("=====================任务实例日志==========================");
        String resultResponse = publicHttps(url, method, params);


        System.out.println(resultResponse);
        System.out.println("===============================================");

        return resultResponse;
    }

}
