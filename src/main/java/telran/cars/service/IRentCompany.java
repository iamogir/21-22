package telran.cars.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import telran.cars.dto.*;

public interface IRentCompany extends Serializable
{
//	Sprint1
	CarsReturnCode addModel(Model model);
	CarsReturnCode addCar(Car car);
	CarsReturnCode addDriver(Driver driver);
	Model getModel(String modelName);
	Car getCar(String regNumber);
	Driver getDriver(long licenseId);
	
//	Sprint2
	CarsReturnCode rentCar(String regNumber, long licenseId, LocalDate rentDate, int rentDays);
	Set<Car> getCarsByDriver(long licenseId);
	Set<Driver> getDriversByCar(String regNumber);
	List<Car> getCarsByModel(String modelName);
	List<RentRecord > getRentRecordsAtDates(LocalDate from, LocalDate to);
	
//  Sprint3
	RemovedCarData removeCar(String regNumber);
	List<RemovedCarData> removeModel(String modelName);
	RemovedCarData returnCar(String regNumber, long licenseId, LocalDate returnDate, 
			int damagesPercent, int tankPercent);
	
//	Sprint4
	List<String> getMostPopularCarModels(LocalDate dateFrom, LocalDate dateTo, int ageFrom, int ageTo);
	List<String> getMostProfitableCarModels(LocalDate dateFrom, LocalDate dateTo);
	List<Driver> getMostActiveDrivers();
	
//	Sprint5
	List<String> getModelNames();
}
