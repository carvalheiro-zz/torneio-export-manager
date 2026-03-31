package br.com.srcsoftware.todoscontratodos.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PayloadDTO {
	private String htmlReportTemplate;
	private String jsonData;
}
