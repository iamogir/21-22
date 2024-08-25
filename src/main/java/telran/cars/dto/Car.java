package telran.cars.dto;

import java.io.Serializable;

public class Car implements Serializable
{
	private static final long serialVersionUID = -3171850004895202806L;

	private String regNumber;
	private String color;
	private State state = State.EXCELLENT;
	private String modelName;
	private boolean inUse;
	private boolean flagRemoved;
	
	public Car()
	{
	}

	public Car(String regNumber, String color, String modelName)
	{
		super();
		this.regNumber = regNumber;
		this.color = color;
		this.modelName = modelName;
	}

	public State getState()
	{
		return state;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	public boolean isInUse()
	{
		return inUse;
	}

	public void setInUse(boolean inUse)
	{
		this.inUse = inUse;
	}

	public boolean isFlagRemoved()
	{
		return flagRemoved;
	}

	public void setFlagRemoved(boolean flagRemoved)
	{
		this.flagRemoved = flagRemoved;
	}

	public String getRegNumber()
	{
		return regNumber;
	}

	public String getColor()
	{
		return color;
	}

	public String getModelName()
	{
		return modelName;
	}

	@Override
	public String toString()
	{
		return "Car [regNumber=" + regNumber + ", color=" + color + ", state=" + state + ", modelName=" + modelName
				+ ", inUse=" + inUse + ", flagRemoved=" + flagRemoved + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regNumber == null) ? 0 : regNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof Car))
			return false;
		Car other = (Car) obj;
		if (regNumber == null)
		{
			if (other.regNumber != null)
				return false;
		} else if (!regNumber.equals(other.regNumber))
			return false;
		return true;
	}
}
