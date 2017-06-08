package org.activiti;

import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyUnitTest {

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	@Deployment(resources = {"org/activiti/test/Example_Spec_02_calling_process.bpmn20.xml",
			"org/activiti/test/Example_Spec_02_sub_process.bpmn20.xml" })
	public void test() {
		// After the process has started, the 'Start ignition' task
		// should be active
		ProcessInstance pi = activitiRule.getRuntimeService().startProcessInstanceByKey("callingProcess");
		TaskQuery taskQuery = activitiRule.getTaskService().createTaskQuery();
		Task startIgnition = taskQuery.singleResult();
		assertEquals("Verify ignition on.", startIgnition.getName());

		// Verify with Query API
		ProcessInstance subProcessInstance = activitiRule.getRuntimeService().createProcessInstanceQuery()
				.superProcessInstanceId(pi.getId()).singleResult();
		assertNotNull(subProcessInstance);
		assertEquals(pi.getId(), activitiRule.getRuntimeService().createProcessInstanceQuery()
				.subProcessInstanceId(subProcessInstance.getId()).singleResult().getId());

		// Completing the task with approval, will end the subprocess and
		// continue the original process arriving at the "Measure voltage task"
		activitiRule.getTaskService().complete(startIgnition.getId(), CollectionUtil.singletonMap("ignitionOn", true));
		Task mesureVoltageTask = taskQuery.singleResult();
		assertEquals("Measure voltage", mesureVoltageTask.getName());
	}

}
