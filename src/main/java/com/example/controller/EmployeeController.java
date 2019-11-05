package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Employee;
import com.example.service.EmployeeService;

@RestController
@Validated
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;

	@RequestMapping("/employee")
	public List<Employee> getEmployees() {
		/*List<Employee> employeesList = new ArrayList<Employee>();
		employeesList.add(new Employee(1, "lokesh", "gupta", "lokesh@gmail.com"));
		*/
		List<Employee> employeesList = employeeService.findAll();
		return employeesList;
	}

}
