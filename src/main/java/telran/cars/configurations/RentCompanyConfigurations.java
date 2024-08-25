package telran.cars.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import telran.cars.service.IRentCompany;
import telran.cars.service.RentCompanyEmbedded;

@Configuration
public class RentCompanyConfigurations
{
	@Value("${file-name: company.data}")
	private String fileName;
	
	@Bean
	@Scope("prototype")
	IRentCompany getCompany()
	{
		return RentCompanyEmbedded.restoreFromFile(fileName);
	}
}
