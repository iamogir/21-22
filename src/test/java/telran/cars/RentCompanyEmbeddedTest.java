package telran.cars;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import telran.cars.dto.Car;
import telran.cars.dto.Driver;
import telran.cars.dto.Model;
import telran.cars.dto.RemovedCarData;
import telran.cars.dto.RentRecord;
import telran.cars.dto.State;
import telran.cars.service.IRentCompany;
import telran.cars.service.RentCompanyEmbedded;
import static telran.cars.dto.CarsReturnCode.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootTest
class RentCompanyEmbeddedTest
{
	static final String MODEL_NAME = "BMW X5";
	static final String REG_NUMBER = "123";
	static final long LICENSE_ID = 123;
	static final LocalDate RENT_DATE = LocalDate.of(2020, 10, 10);
	static final LocalDate RETURN_DATE = LocalDate.of(2020, 10, 15);
	private static final int RENT_DAYS = 5;
	private static final int PRICE_DAY = 100;
	private static final int GAS_TANK = 50;

	Model model = new Model(MODEL_NAME, GAS_TANK, "BMW", "Germany", PRICE_DAY);
	Car car = new Car(REG_NUMBER, "red", MODEL_NAME);
	Driver driver = new Driver(LICENSE_ID, "Max", 1980, "223322223");
	
	@Autowired
	ApplicationContext context;
	
	IRentCompany company;

	@BeforeEach
	void setUp() throws Exception
	{
		company = context.getBean(IRentCompany.class);
		company.addModel(model);
		company.addCar(car);
		company.addDriver(driver);
		company.rentCar(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS);
	}

	@Test
	void testAddModel()
	{
		assertEquals(WRONG_DATA, company.addModel(null));
		assertEquals(MODEL_EXISTS, company.addModel(model));
		assertEquals(OK, company.addModel(new Model(MODEL_NAME + "1", GAS_TANK, "BMW", "Germany", PRICE_DAY)));
	}

	@Test
	void testAddCar()
	{
		assertEquals(WRONG_DATA, company.addCar(null));
		assertEquals(NO_MODEL, company.addCar(new Car(REG_NUMBER + "1", "red", MODEL_NAME + "1")));
		assertEquals(CAR_EXISTS, company.addCar(car));
		assertEquals(OK, company.addCar(new Car(REG_NUMBER + "1", "red", MODEL_NAME)));
	}

	@Test
	void testAddDriver()
	{
		assertEquals(WRONG_DATA, company.addDriver(null));
		assertEquals(DRIVER_EXISTS, company.addDriver(driver));
		assertEquals(OK, company.addDriver(new Driver(LICENSE_ID + 1, "Max", 1980, "223322223")));
	}

	@Test
	void testGetModel()
	{
		assertNull(company.getModel(null));
		assertNull(company.getModel(MODEL_NAME + "1"));
		assertEquals(model, company.getModel(MODEL_NAME));
	}

	@Test
	void testGetCar()
	{
		assertNull(company.getCar(null));
		assertNull(company.getCar(REG_NUMBER + "1"));
		assertEquals(car, company.getCar(REG_NUMBER));
	}

	@Test
	void testGetDriver()
	{
		assertNull(company.getDriver(0));
		assertNull(company.getDriver(-1));
		assertNull(company.getDriver(LICENSE_ID + 1));
		assertEquals(driver, company.getDriver(LICENSE_ID));
	}

	@Test
	void testRentCar()
	{
		assertEquals(WRONG_DATA, company.rentCar(null, LICENSE_ID, RENT_DATE, RENT_DAYS));
		assertEquals(WRONG_DATA, company.rentCar(REG_NUMBER, 0, RENT_DATE, RENT_DAYS));
		assertEquals(WRONG_DATA, company.rentCar(REG_NUMBER, LICENSE_ID, null, RENT_DAYS));
		assertEquals(WRONG_DATA, company.rentCar(REG_NUMBER, LICENSE_ID, RENT_DATE, 0));
		assertEquals(NO_CAR, company.rentCar(REG_NUMBER + "1", LICENSE_ID, RENT_DATE, RENT_DAYS));
		assertEquals(NO_DRIVER, company.rentCar(REG_NUMBER, LICENSE_ID + 1, RENT_DATE, RENT_DAYS));
		assertEquals(CAR_IN_USE, company.rentCar(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS));

		Car car1 = new Car(REG_NUMBER + "1", "red", MODEL_NAME);
		company.addCar(car1);
		car1.setFlagRemoved(true);
		assertEquals(CAR_REMOVED, company.rentCar(REG_NUMBER + "1", LICENSE_ID, RENT_DATE, RENT_DAYS));

		Car car2 = new Car(REG_NUMBER + "2", "red", MODEL_NAME);
		company.addCar(car2);
		assertEquals(OK, company.rentCar(REG_NUMBER + "2", LICENSE_ID, RENT_DATE, RENT_DAYS));
	}

	@Test
	void testGetCarsByDriver()
	{
		Set<Car> set = company.getCarsByDriver(LICENSE_ID);
		assertEquals(1, set.size());
		assertTrue(set.contains(car));

		set = company.getCarsByDriver(LICENSE_ID + 1);
		assertTrue(set.isEmpty());
	}

	@Test
	void testGetDriversByCar()
	{
		Set<Driver> set = company.getDriversByCar(REG_NUMBER);
		assertEquals(1, set.size());
		assertTrue(set.contains(driver));

		set = company.getDriversByCar(REG_NUMBER + "1");
		assertTrue(set.isEmpty());
	}

	@Test
	void testGetCarsByModel()
	{
		List<Car> list = company.getCarsByModel(MODEL_NAME);// in use
		assertTrue(list.isEmpty());

		list = company.getCarsByModel(MODEL_NAME + "1");
		assertTrue(list.isEmpty());

		Car car1 = new Car(REG_NUMBER + "1", "red", MODEL_NAME);
		company.addCar(car1);
		list = company.getCarsByModel(MODEL_NAME);
		assertEquals(1, list.size());
		assertTrue(list.contains(car1));
	}

	@Test
	void testGetRentRecordsAtDates()
	{
		RentRecord exp = new RentRecord(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS);
		List<RentRecord> list = company.getRentRecordsAtDates(LocalDate.of(1990, 1, 1), LocalDate.of(2022, 1, 1));
		assertEquals(1, list.size());
		assertTrue(list.contains(exp));

		list = company.getRentRecordsAtDates(LocalDate.of(1990, 1, 1), LocalDate.of(1999, 1, 1));
		assertTrue(list.isEmpty());

		list = company.getRentRecordsAtDates(LocalDate.of(1990, 1, 1), RENT_DATE);
		assertTrue(list.isEmpty());

		company.getRentRecordsAtDates(LocalDate.of(1990, 1, 1), null);
		assertTrue(list.isEmpty());

		company.getRentRecordsAtDates(null, LocalDate.of(2022, 1, 1));
		assertTrue(list.isEmpty());

		company.getRentRecordsAtDates(LocalDate.of(2022, 1, 1), LocalDate.of(1990, 1, 1));
		assertTrue(list.isEmpty());
	}

	@Test
	void testRemoveCar()
	{
		assertNull(company.removeCar(REG_NUMBER + "1"));
		assertNull(company.removeCar(null));
		assertNull(company.removeCar(REG_NUMBER));
		assertTrue(company.getCar(REG_NUMBER).isFlagRemoved());
		assertFalse(company.getRentRecordsAtDates(RENT_DATE, RETURN_DATE).isEmpty());

		Car car1 = new Car(REG_NUMBER + "1", "red", MODEL_NAME);
		company.addCar(car1);
		RemovedCarData rcd = company.removeCar(REG_NUMBER + "1");
		assertNull(rcd.getRemovedRecords());
		assertTrue(
				company.getCarsByModel(MODEL_NAME).stream().noneMatch(c -> c.getRegNumber().equals(REG_NUMBER + "1")));
		assertNull(company.getCar(REG_NUMBER + "1"));

		Car car2 = new Car(REG_NUMBER + "2", "red", MODEL_NAME);
		company.addCar(car2);
		company.rentCar(REG_NUMBER + "2", LICENSE_ID, RENT_DATE, RENT_DAYS);
		company.returnCar(REG_NUMBER + "2", LICENSE_ID, RETURN_DATE, 0, 100);
		rcd = company.removeCar(REG_NUMBER + "2");
		testActualRemoved(car2);
	}

	private void testActualRemoved(Car car)
	{
		assertTrue(
				company.getCarsByModel(MODEL_NAME).stream().noneMatch(c -> c.getRegNumber().equals(REG_NUMBER + "2")));
		assertNull(company.getCar(REG_NUMBER + "2"));
		assertFalse(company.getCarsByDriver(LICENSE_ID).contains(car));
		assertTrue(company.getDriversByCar(REG_NUMBER + "2").isEmpty());
		assertTrue(company.getRentRecordsAtDates(RENT_DATE, RETURN_DATE.plusDays(1)).stream()
				.noneMatch(rr -> rr.getRegNumber().equals(REG_NUMBER + "2")));
	}

	@Test
	void testRemoveModel()
	{
		assertTrue(company.removeModel(null).isEmpty());
		assertTrue(company.removeModel(MODEL_NAME + "1").isEmpty());
		assertTrue(company.removeModel(MODEL_NAME).isEmpty());
		Car car = company.getCar(REG_NUMBER);
		assertTrue(car.isFlagRemoved());
	}

	@Test
	void returnCarWithRemoving()
	{
		company.removeCar(REG_NUMBER);
		LocalDate returnDate = RENT_DATE.plusDays(RENT_DAYS);
		company.returnCar(REG_NUMBER, LICENSE_ID, returnDate, 0, 100);
		testActualRemoved(car);
	}

	@Test
	void returnCarWithNoDamagesNoDelayFullTank()
	{
		RentRecord recordExpected = new RentRecord(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS);
		System.out.println(recordExpected);
		setRecordExpected(RETURN_DATE, recordExpected, PRICE_DAY * RENT_DAYS, 0, 100);
		company.returnCar(REG_NUMBER, LICENSE_ID, RETURN_DATE, 0, 100);
		List<RentRecord> records = company.getRentRecordsAtDates(RENT_DATE, RETURN_DATE);
		assertEquals(1, records.size());
		assertEquals(recordExpected, records.get(0));
	}

	private void setRecordExpected(LocalDate returnDate, RentRecord recordExpected, double cost, int damages,
			int tankPercent)
	{
		recordExpected.setCost(cost);
		recordExpected.setTankPercent(tankPercent);
		recordExpected.setDamagesPercent(damages);
		recordExpected.setReturnDate(returnDate);
	}

	@Test
	public void returnCarWithDelay()
	{
		// delay 1 day
		LocalDate returnDate = RETURN_DATE.plusDays(1);
		RentRecord recordExpected = new RentRecord(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS);
		double cost = PRICE_DAY * RENT_DAYS + PRICE_DAY
				+ PRICE_DAY / 100 * ((RentCompanyEmbedded) company).getFinePercent();
		setRecordExpected(returnDate, recordExpected, cost, 0, 100);

		company.returnCar(REG_NUMBER, LICENSE_ID, returnDate, 0, 100);
		List<RentRecord> records = company.getRentRecordsAtDates(RENT_DATE, returnDate);
		assertEquals(1, records.size());
		assertEquals(recordExpected, records.get(0));
	}

	@Test
	void returnCarWithNoFullTank()
	{
		LocalDate returnDate = RENT_DATE.plusDays(RENT_DAYS);
		RentRecord recordExpected = new RentRecord(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS);
		double cost = PRICE_DAY * RENT_DAYS + GAS_TANK / 2 * ((RentCompanyEmbedded) company).getGasPrice();
		setRecordExpected(RETURN_DATE, recordExpected, cost, 0, 50);
		company.returnCar(REG_NUMBER, LICENSE_ID, returnDate, 0, 50);
		List<RentRecord> records = company.getRentRecordsAtDates(RENT_DATE, RETURN_DATE);
		assertEquals(1, records.size());
		assertEquals(recordExpected, records.get(0));
	}

	@Test
	public void returnCarWithTotalLostDamages()
	{
		RentRecord recordExpected = new RentRecord(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS);
		double cost = PRICE_DAY * RENT_DAYS;
		setRecordExpected(RETURN_DATE, recordExpected, cost, 70, 100);
		company.returnCar(REG_NUMBER, LICENSE_ID, RETURN_DATE, 70, 100);
		assertNull(company.getCar(REG_NUMBER));
	}
	
	@Test
	public void returnCarWithDamages()
	{
		RentRecord recordExpected = new RentRecord(REG_NUMBER, LICENSE_ID, RENT_DATE, RENT_DAYS);
		double cost = PRICE_DAY * RENT_DAYS;
		setRecordExpected(RETURN_DATE, recordExpected, cost, 50, 100);
		company.returnCar(REG_NUMBER, LICENSE_ID, RETURN_DATE, 50, 100);
		Car car = company.getCar(REG_NUMBER);
		assertEquals(State.BAD, car.getState());
	}
}
