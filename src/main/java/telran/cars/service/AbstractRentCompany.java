package telran.cars.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import jakarta.annotation.PostConstruct;

@ManagedResource
public abstract class AbstractRentCompany implements IRentCompany
{
	private static final long serialVersionUID = 1697398309445055205L;
	
//	protected int finePercent;
//	protected int gasPrice;
//	
//	public AbstractRentCompany()
//	{
//		this.finePercent = 15;
//		this.gasPrice = 10;
//	}
	
	@Value("${fine-percent:15}")
	protected int finePercent;
	
	@Value("${gas-price:10}")
	protected int gasPrice;
	
	@PostConstruct
	void setting()
	{
		setFinePercent(finePercent);
		setGasPrice(gasPrice);
	}
	
	@ManagedAttribute
	public int getFinePercent()
	{
		return finePercent;
	}
	
	@ManagedAttribute
	public void setFinePercent(int finePercent)
	{
		this.finePercent = finePercent;
	}
	
	@ManagedAttribute
	public int getGasPrice()
	{
		return gasPrice;
	}
	
	@ManagedAttribute
	public void setGasPrice(int gasPrice)
	{
		this.gasPrice = gasPrice;
	}
	
	protected double computeCost(int rentPrice, int rentDays, int delay, int tankPercent, 
			int tankVolume)
	{
		double cost = rentPrice * rentDays;
		
		if(delay>0)
		{
			double temp = rentPrice * (1+(double)finePercent/100) * delay;
			cost += temp;
		}
		
		if(tankPercent<100)
		{
			double temp = tankVolume * ((100 - (double)tankPercent) / 100) * gasPrice;
			cost += temp;
		}
		
		return cost;
	}
}
