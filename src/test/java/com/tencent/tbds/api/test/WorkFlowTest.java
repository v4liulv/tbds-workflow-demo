package com.tencent.tbds.api.test;

import com.tencent.tbds.api.WorkflowDemo;
import org.junit.Test;

import static com.tencent.tbds.api.WorkflowDemo.list;
import static com.tencent.tbds.api.WorkflowDemo.properties;

/**
 * @author liulv
 */
public class WorkFlowTest {

    /**
     * 创建工作流test
     */
    @Test
    public void createTest() {
        WorkflowDemo.create(properties.getProperty("tbds_workflow_create_file"));
    }

    /**
     * 启动工作流test
     */
    @Test
    public void startTest() {
        String workflowName = "ftp-hdfs-demo";
        String workflowId = list(workflowName);

        WorkflowDemo.start(workflowId);
    }

    /**
     * 停止工作流test
     */
    @Test
    public void stopTest() {
        String workflowName = "ftp-hdfs-demo";
        String workflowId = list(workflowName);

        WorkflowDemo.stop(workflowId);
    }

    /**
     * 删除工作流test
     */
    @Test
    public void deleteTest() {
        String workflowName = "ftp-hdfs-demo";
        String workflowId = list(workflowName);

        WorkflowDemo.delete(workflowId);
    }

    /**
     * 串联工作流
     * 包含修改
     *
     */
    @Test
    public void workflowSeries() {
        String workflowName = "ftp-hdfs-demo";
        String workflowId = list(workflowName);

        WorkflowDemo.start(workflowId);

        WorkflowDemo.stop(workflowId);

        WorkflowDemo.delete(workflowId);
    }

    @Test
    public void triggerTaskTest() throws Exception {
        String workflowName = "tbds-hdfs-demo";
        String taskName = "ftp-hdfs";
        WorkflowDemo.triggerTask(workflowName, taskName);
    }

    @Test
    public void taskInstanceTest() throws Exception {
        String workflowName = "doc";
        String taskName = "compress-doc";
        WorkflowDemo.taskInstanceList(workflowName, taskName);
    }

    @Test
    public void taskInstanceLog() throws Exception {
        String workflowName = "doc";
        String taskName = "compress-doc";
        WorkflowDemo.taskInstanceLogInfo(workflowName, taskName);
    }

}
