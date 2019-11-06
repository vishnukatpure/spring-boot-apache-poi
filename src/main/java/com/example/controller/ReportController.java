package com.example.controller;

import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.service.JsonToXlsService;

@RestController
@Validated
public class ReportController {

	@Autowired
	JsonToXlsService jsonToXlsService;

	@RequestMapping("/generateReport")
	public OutputStream getEmployees() throws Exception {
		return jsonToXlsService.testReadXlsFile();
	}

}
