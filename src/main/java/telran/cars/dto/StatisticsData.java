package telran.cars.dto;

import java.io.Serializable;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.ToString;

import static telran.cars.api.RentCompanyErrorMessage.*;

@ToString
public class StatisticsData implements Serializable
{
	private static final long serialVersionUID = -7843114314564227396L;

	@NotNull(message = DATE_IS_NULL)
	@Past(message = DATE_IS_NOT_PAST)
	@Pattern(regexp = "\\d{4}-((0[1-9])|(1[0-2]))-((0[1-9])|([12][0-9])|(3[01]))", message = DATE_WRONG_FORMAT)
	private LocalDate fromDate;
	
	@NotNull(message = DATE_IS_NULL)
	@Pattern(regexp = "\\d{4}-((0[1-9])|(1[0-2]))-((0[1-9])|([12][0-9])|(3[01]))", message = DATE_WRONG_FORMAT)
	private LocalDate toDate;
	
	@Min(value = 16, message = AGE_LESS_THAN_MIN)
	@Max(value = 80, message = AGE_GREATER_THAN_MAX)
	private int fromAge;
	
	@Min(value = 16, message = AGE_LESS_THAN_MIN)
	@Max(value = 80, message = AGE_GREATER_THAN_MAX)
	private int toAge;
	
	public StatisticsData()
	{
	}

	public StatisticsData(LocalDate fromDate, LocalDate toDate, int fromAge, int toAge)
	{
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.fromAge = fromAge;
		this.toAge = toAge;
	}

	public LocalDate getFromDate()
	{
		return fromDate;
	}

	public LocalDate getToDate()
	{
		return toDate;
	}

	public int getFromAge()
	{
		return fromAge;
	}

	public int getToAge()
	{
		return toAge;
	}
	
	
}
