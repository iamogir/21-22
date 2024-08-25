package telran.cars.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PreDestroy;
import telran.cars.dto.*;
import telran.utils.Persistable;
import static telran.cars.dto.CarsReturnCode.*;
import static telran.cars.service.RentCompanyLocks.*;

public class RentCompanyEmbedded extends AbstractRentCompany implements Persistable
{
	private static final long serialVersionUID = -7095697428789033067L;

	private static final int GOOD_THRESHOLD = 10;
	private static final int BAD_THRESHOLD = 30;
	private static final int REMOVE_THRESHOLD = 60;

	private HashMap<String, Car> cars = new HashMap<>();
	private HashMap<Long, Driver> drivers = new HashMap<>();
	private HashMap<String, Model> models = new HashMap<>();
	private TreeMap<LocalDate, List<RentRecord>> records = new TreeMap<>();

	private HashMap<String, List<RentRecord>> carRecords = new HashMap<>();
	private HashMap<Long, List<RentRecord>> driverRecords = new HashMap<>();
	private HashMap<String, List<Car>> modelCars = new HashMap<>();

	@Value("${file-name: company.data}")
	private String fileName;

	@PreDestroy
	void saveCompanyToFile()
	{
		this.save(fileName);
	}
	
	
	@Override
	public CarsReturnCode addModel(Model model)
	{
		lockAddModel(true);
		try
		{
			if (model == null)
				return WRONG_DATA;
			return models.putIfAbsent(model.getModelName(), model) == null ? OK : MODEL_EXISTS;
		} finally
		{
			lockAddModel(false);
		}
	}

	@Override
	public CarsReturnCode addCar(Car car)
	{
		lockAddCar(true);
		try
		{
			if (car == null)
				return WRONG_DATA;
			if (!models.containsKey(car.getModelName()))
				return NO_MODEL;
			if (cars.putIfAbsent(car.getRegNumber(), car) == null)
			{
				addToModelCars(car);
				return OK;
			}
			return CAR_EXISTS;
		} finally
		{
			lockAddCar(false);
		}
	}

	private void addToModelCars(Car car)
	{
		String modelName = car.getModelName();

		List<Car> list = modelCars.getOrDefault(modelName, new ArrayList<>());
		list.add(car);
		modelCars.putIfAbsent(modelName, list);
	}

	@Override
	public CarsReturnCode addDriver(Driver driver)
	{
		lockAaddDriver(true);
		try
		{
			if (driver == null)
				return WRONG_DATA;
			return drivers.putIfAbsent(driver.getLicenseId(), driver) == null ? OK : DRIVER_EXISTS;
		} finally
		{
			lockAaddDriver(false);
		}
	}

	@Override
	public Model getModel(String modelName)
	{
		lockGetModel(true);
		try
		{
			return models.get(modelName);
		} finally
		{
			lockGetModel(false);
		}
	}

	@Override
	public Car getCar(String regNumber)
	{
		lockGetCar(true);
		try
		{
			return cars.get(regNumber);
		} finally
		{
			lockGetCar(false);
		}
	}

	@Override
	public Driver getDriver(long licenseId)
	{
		lockGetDriver(true);
		try
		{
			return drivers.get(licenseId);
		} finally
		{
			lockGetDriver(false);
		}
	}

	@Override
	public void save(String fileName)
	{
		lockSave(true);
		try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream(fileName))))
		{
			out.writeObject(this);
		} catch (Exception e)
		{
			System.out.println("Error in method save " + e.getMessage());
		} finally
		{
			lockSave(false);
		}

	}

	public static IRentCompany restoreFromFile(String fileName)
	{
		try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName))))
		{
			return (IRentCompany) in.readObject();
		} catch (Exception e)
		{
			return new RentCompanyEmbedded();
		}
	}

	@Override
	public CarsReturnCode rentCar(String regNumber, long licenseId, LocalDate rentDate, int rentDays)
	{
		lockRentCar(true);
		try
		{
			if (regNumber == null || licenseId < 1 || rentDate == null || rentDays < 1)
				return WRONG_DATA;
			if (!drivers.containsKey(licenseId))
				return NO_DRIVER;
			Car car = getCar(regNumber);
			if (car == null)
				return NO_CAR;
			if (car.isFlagRemoved())
				return CAR_REMOVED;
			if (car.isInUse())
				return CAR_IN_USE;
			RentRecord record = new RentRecord(regNumber, licenseId, rentDate, rentDays);
			addToRecords(record);
			addToCarRecords(record);
			addToDriverRecords(record);
			car.setInUse(true);
			return OK;
		} finally
		{
			lockRentCar(false);
		}
	}

	private void addToDriverRecords(RentRecord record)
	{
		long key = record.getLicenseId();
		List<RentRecord> list = driverRecords.getOrDefault(key, new ArrayList<>());
		list.add(record);
		driverRecords.putIfAbsent(key, list);
	}

	private void addToCarRecords(RentRecord record)
	{
		String key = record.getRegNumber();
		List<RentRecord> list = carRecords.getOrDefault(key, new ArrayList<>());
		list.add(record);
		carRecords.putIfAbsent(key, list);
	}

	private void addToRecords(RentRecord record)
	{
		LocalDate key = record.getRentDate();
		List<RentRecord> list = records.getOrDefault(key, new ArrayList<>());
		list.add(record);
		records.putIfAbsent(key, list);
	}

	@Override
	public Set<Car> getCarsByDriver(long licenseId)
	{
		lockGetCarsByDriver(true);
		try
		{
			List<RentRecord> list = driverRecords.getOrDefault(licenseId, new ArrayList<>());
			return list.stream().map(rr -> getCar(rr.getRegNumber())).collect(Collectors.toSet());
		} finally
		{
			lockGetCarsByDriver(false);
		}
	}

	@Override
	public Set<Driver> getDriversByCar(String regNumber)
	{
		lockGetDriversByCar(true);
		try
		{
			List<RentRecord> list = carRecords.getOrDefault(regNumber, new ArrayList<>());
			return list.stream().map(rr -> getDriver(rr.getLicenseId())).collect(Collectors.toSet());
		} finally
		{
			lockGetDriversByCar(false);
		}
	}

	@Override
	public List<Car> getCarsByModel(String modelName)
	{
		lockGetCarsByModel(true);
		try
		{
			List<Car> list = modelCars.getOrDefault(modelName, new ArrayList<>());
			return list.stream().filter(c -> !c.isFlagRemoved() && !c.isInUse()).collect(Collectors.toList());
		} finally
		{
			lockGetCarsByModel(false);
		}
	}

	@Override
	public List<RentRecord> getRentRecordsAtDates(LocalDate from, LocalDate to)
	{
		lockGetRentRecords(true);
		try
		{
			if (from == null || to == null || from.isAfter(to))
				return new ArrayList<>();
			Collection<List<RentRecord>> col = records.subMap(from, to).values();
			return col.stream().flatMap(l -> l.stream()).collect(Collectors.toList());
		} finally
		{
			lockGetRentRecords(false);
		}
	}

	@Override
	public RemovedCarData removeCar(String regNumber)
	{
		lockRemoveCar(true);
		try
		{
			if (regNumber == null)
				return null;
			Car car = getCar(regNumber);
			if (car == null || car.isFlagRemoved())
				return null;
			car.setFlagRemoved(true);
			return car.isInUse() ? null : actualCarRemove(car);
		} finally
		{
			lockRemoveCar(false);
		}
	}

	private RemovedCarData actualCarRemove(Car car)
	{
		String regNumber = car.getRegNumber();
		List<RentRecord> list = carRecords.get(regNumber);

		if (list != null)
		{
			carRecords.remove(regNumber);
			removeFromDriverRecords(list);
			removeFromRecords(list);
		}
		removeFromModelCars(car);
		cars.remove(regNumber);

		return new RemovedCarData(car, list);
	}

	private void removeFromModelCars(Car car)
	{
		String name = car.getModelName();
		List<Car> temp = modelCars.get(name);
		temp.remove(car);
		if (temp.isEmpty())
			modelCars.remove(name);
	}

	private void removeFromRecords(List<RentRecord> list)
	{
		list.forEach(rr ->
		{
			LocalDate date = rr.getRentDate();
			List<RentRecord> temp = records.get(date);
			temp.remove(rr);
			if (temp.isEmpty())
				records.remove(date);
		});
	}

	private void removeFromDriverRecords(List<RentRecord> list)
	{
		list.forEach(rr ->
		{
			long id = rr.getLicenseId();
			List<RentRecord> temp = driverRecords.get(id);
			temp.remove(rr);
			if (temp.isEmpty())
				driverRecords.remove(id);
		});
	}

	@Override
	public List<RemovedCarData> removeModel(String modelName)
	{
		lockRemoveModel(true);
		try
		{
			if (modelName == null)
				return new ArrayList<>();
			List<Car> list = modelCars.getOrDefault(modelName, new ArrayList<>());
			List<RemovedCarData> res = list.stream().map(c -> removeCar(c.getRegNumber())).filter(rcd -> rcd != null)
					.collect(Collectors.toList());
			if (list.isEmpty())
				modelCars.remove(modelName);
			return res;
		} finally
		{
			lockRemoveModel(false);
		}
	}

	@Override
	public RemovedCarData returnCar(String regNumber, long licenseId, LocalDate returnDate, int damagesPercent,
			int tankPercent)
	{
		lockReturnCar(true);
		try
		{
			if (regNumber == null || licenseId < 1 || returnDate == null || damagesPercent < 0 || tankPercent < 0)
				return null;
			RentRecord record = getRentRecord(regNumber, licenseId);
			if (record == null)
				return null;
			updateRecord(record, returnDate, damagesPercent, tankPercent);
			Car car = getCar(regNumber);
			updateCar(car, damagesPercent);
			return car.isFlagRemoved() || damagesPercent >= REMOVE_THRESHOLD ? actualCarRemove(car) : null;
		} finally
		{
			lockReturnCar(false);
		}
	}

	private void updateCar(Car car, int damagesPercent)
	{
		car.setInUse(false);
		if (damagesPercent >= BAD_THRESHOLD)
			car.setState(State.BAD);
		else if (damagesPercent >= GOOD_THRESHOLD)
			car.setState(State.GOOD);
	}

	private void updateRecord(RentRecord record, LocalDate returnDate, int damagesPercent, int tankPercent)
	{
		record.setDamagesPercent(damagesPercent);
		record.setTankPercent(tankPercent);
		record.setReturnDate(returnDate);
		record.setCost(computeCost(getRentPrice(record.getRegNumber()), record.getRentDays(), getDelay(record),
				tankPercent, getTankVolume(record.getRegNumber())));
	}

	private int getTankVolume(String regNumber)
	{
		String modelName = cars.get(regNumber).getModelName();
		return models.get(modelName).getGasTank();
	}

	private int getDelay(RentRecord record)
	{
		long realDays = ChronoUnit.DAYS.between(record.getRentDate(), record.getReturnDate());
		int delta = (int) (realDays - record.getRentDays());
		return delta <= 0 ? 0 : delta;
	}

	private int getRentPrice(String regNumber)
	{
		String modelName = cars.get(regNumber).getModelName();
		return models.get(modelName).getPriceDay();
	}

	private RentRecord getRentRecord(String regNumber, long licenseId)
	{
		return driverRecords.get(licenseId).stream()
				.filter(r -> r.getRegNumber().equals(regNumber) && r.getReturnDate() == null).findFirst().orElse(null);
	}

	@Override
	public List<String> getMostPopularCarModels(LocalDate dateFrom, LocalDate dateTo, int ageFrom, int ageTo)
	{
		lockPopularCars(true);
		try
		{
			List<String> res = new ArrayList<>();
			List<RentRecord> list = getRentRecordsAtDates(dateFrom, dateTo);
			if (list.isEmpty())
				return res;
			Map<String, Long> map = list.stream().filter(rr -> isProperAge(rr, ageFrom, ageTo)).collect(
					Collectors.groupingBy(rr -> getCar(rr.getRegNumber()).getModelName(), Collectors.counting()));
			if (map.isEmpty())
				return res;
			long max = Collections.max(map.values());
			map.forEach((k, v) ->
			{
				if (v == max)
					res.add(k);
			});
			return res;
		} finally
		{
			lockPopularCars(false);
		}
	}

	private boolean isProperAge(RentRecord rr, int ageFrom, int ageTo)
	{
		Driver driver = getDriver(rr.getLicenseId());
		int age = rr.getRentDate().getYear() - driver.getBirthYear();
		return age >= ageFrom && age < ageTo;
	}

	@Override
	public List<String> getMostProfitableCarModels(LocalDate dateFrom, LocalDate dateTo)
	{
		lockPopularCars(true);
		try
		{
			List<String> res = new ArrayList<>();
			List<RentRecord> list = getRentRecordsAtDates(dateFrom, dateTo);
			if (list.isEmpty())
				return res;
			Map<String, Double> map = list.stream().collect(Collectors.groupingBy(
					rr -> getCar(rr.getRegNumber()).getModelName(), Collectors.summingDouble(RentRecord::getCost)));
			double max = map.values().stream().mapToDouble(c -> c).max().getAsDouble();
			map.forEach((k, v) ->
			{
				if (v == max)
					res.add(k);
			});
			return res;
		} finally
		{
			lockPopularCars(false);
		}
	}

	@Override
	public List<Driver> getMostActiveDrivers()
	{
		lockActiveDrivers(true);
		try
		{
			long max = driverRecords.values().stream().mapToLong(l -> l.size()).max().getAsLong();
			List<Driver> res = new ArrayList<>();
			driverRecords.forEach((k, v) ->
			{
				if (v.size() == max)
					res.add(getDriver(k));
			});
			return res;
		} finally
		{
			lockActiveDrivers(false);
		}
	}

	@Override
	public List<String> getModelNames()
	{
		lockGetModelNames(true);
		try
		{
			return new ArrayList<>(models.keySet());
		} finally
		{
			lockGetModelNames(false);
		}
	}

}
