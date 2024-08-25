package telran.cars.dto;

import java.io.Serializable;
import java.time.LocalDate;

public class ReturnCarData implements Serializable
{
	private static final long serialVersionUID = -6349205440109688553L;

	private String regNumber;
	private long licenseId;
	private LocalDate returnDate;
	private int damagesPercent;
	private int tankPercent;
	
	public ReturnCarData()
	{
	}

	public ReturnCarData(String regNumber, long licenseId, LocalDate returnDate, int damages, int tankPercent)
	{
		super();
		this.regNumber = regNumber;
		this.licenseId = licenseId;
		this.returnDate = returnDate;
		this.damagesPercent = damages;
		this.tankPercent = tankPercent;
	}

	public String getRegNumber()
	{
		return regNumber;
	}

	public long getLicenseId()
	{
		return licenseId;
	}

	public LocalDate getReturnDate()
	{
		return returnDate;
	}

	public int getDamagesPercent()
	{
		return damagesPercent;
	}

	public int getTankPercent()
	{
		return tankPercent;
	}
	
	
}
