package telran.cars.service;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RentCompanyLocks
{
	static final ReadWriteLock carsLock = new ReentrantReadWriteLock();
	static final ReadWriteLock modelsLock = new ReentrantReadWriteLock();
	static final ReadWriteLock driversLock = new ReentrantReadWriteLock();
	static final ReadWriteLock recordsLock = new ReentrantReadWriteLock();

	static final int cars_index = 0;
	static final int models_index = 1;
	static final int drivers_index = 2;
	static final int records_index = 3;

	static final int write_index = 0;
	static final int read_index = 1;

	static Lock[][] locks;
	static
	{
		locks = new Lock[2][4];
		ReadWriteLock[] rwl = { carsLock, modelsLock, driversLock, recordsLock };
		for (int i = 0; i < rwl.length; i++)
		{
			locks[write_index][i] = rwl[i].writeLock();
			locks[read_index][i] = rwl[i].readLock();
		}
	}

	private static void lockUnlock(boolean flag, int typeLock, int... indexes)
	{
		Arrays.sort(indexes);
		if (flag)
			customLock(typeLock, indexes);
		else
			customUnlock(typeLock, indexes);
	}

	private static void customUnlock(int typeLock, int[] indexes)
	{
		for(int i: indexes)
		{
			locks[typeLock][i].unlock();
		}
	}

	private static void customLock(int typeLock, int[] indexes)
	{
		for(int i: indexes)
		{
			locks[typeLock][i].lock();
		}
	}

	public static void lockAddModel(boolean flag)
	{
		lockUnlock(flag, write_index, models_index);
	}
	
	public static void lockSave(boolean flag)
	{
		lockUnlock(flag, read_index, 0,1,2,3);
	}

	public static void lockAddCar(boolean flag)
	{
		lockUnlock(flag, read_index, models_index);
		lockUnlock(flag, write_index, cars_index);
	}

	public static void lockAaddDriver(boolean flag)
	{
		lockUnlock(flag, write_index, drivers_index);
	}
	
	public static void lockGetModel(boolean flag)
	{
		lockUnlock(flag, read_index, models_index);
	}

	public static void lockGetCar(boolean flag)
	{
		lockUnlock(flag, read_index, cars_index);
	}

	public static void lockGetDriver(boolean flag)
	{
		lockUnlock(flag, read_index, drivers_index);
	}


	public static void lockRentCar(boolean flag)
	{
		lockUnlock(flag, read_index, drivers_index);
		lockUnlock(flag, write_index, cars_index, records_index);
	}

	public static void lockGetCarsByDriver(boolean flag)
	{
		lockUnlock(flag, read_index, records_index);
	}

	public static void lockGetDriversByCar(boolean flag)
	{
		lockUnlock(flag, read_index, records_index);
	}

	public static void lockGetCarsByModel(boolean flag)
	{
		lockUnlock(flag, read_index, cars_index, models_index);
	}

	public static void lockGetRentRecords(boolean flag)
	{
		lockUnlock(flag, read_index, records_index);
	}

	public static void lockRemoveCar(boolean flag)
	{
		lockUnlock(flag, write_index, cars_index, models_index, records_index);
	}

	public static void lockRemoveModel(boolean flag)
	{
		lockUnlock(flag, write_index, cars_index, models_index, records_index);
	}
	
	public static void lockReturnCar(boolean flag)
	{
		lockUnlock(flag, write_index, cars_index, models_index, records_index);
	}

	public static void lockPopularCars(boolean flag)
	{
		lockUnlock(flag, read_index, cars_index, models_index, records_index);
	}

	public static void lockActiveDrivers(boolean flag)
	{
		lockUnlock(flag, read_index, drivers_index, records_index);
	}

	public static void lockGetModelNames(boolean flag)
	{
		lockUnlock(flag, read_index, cars_index, models_index);
	}
}
