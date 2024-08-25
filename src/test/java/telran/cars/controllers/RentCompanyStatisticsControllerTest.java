package telran.cars.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import telran.cars.dto.Driver;
import telran.cars.service.IRentCompany;

@WebMvcTest
class RentCompanyStatisticsControllerTest
{
	@MockBean
	IRentCompany service;
	
	@Autowired
	MockMvc mock;
	@Autowired
	ObjectMapper mapper;
	
	@Test
	void testGetMostActiveDrivers() throws Exception
	{
		List<Driver> res = new ArrayList<>(List.of(new Driver()));
		
		when(service.getMostActiveDrivers()).thenReturn(res);
		
		String actual = mock.perform(get("http://localhost:8080/drivers/active")).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();
		assertEquals(mapper.writeValueAsString(res), actual);
	}

	@Test
	void testGetMostPopularCarModels() throws Exception
	{
		fail("Not yet implemented"); 
	}

	@Test
	void testGetMostProfitableCarModels() throws Exception
	{
		fail("Not yet implemented");
	}

}
