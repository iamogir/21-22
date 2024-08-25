package telran.cars.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class RentCarData implements Serializable
{
	private static final long serialVersionUID = -1199051681171459331L;
	
	private String regNumber;
	private long licenseId;
	private LocalDate rentDate;
	private int rentDays;
	
	public RentCarData()
	{
	}

	public RentCarData(String regNumber, long licenseId, LocalDate rentDate, int rentDays)
	{
		super();
		this.regNumber = regNumber;
		this.licenseId = licenseId;
		this.rentDate = rentDate;
		this.rentDays = rentDays;
	}

	public String getRegNumber()
	{
		return regNumber;
	}

	public long getLicenseId()
	{
		return licenseId;
	}

	public LocalDate getRentDate()
	{
		return rentDate;
	}

	public int getRentDays()
	{
		return rentDays;
	}
}
