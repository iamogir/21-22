package telran.cars.api;

public interface RentCompanyErrorMessage
{
	String DATE_IS_NULL = "Date can't be null";
	String DATE_IS_NOT_PAST = "Date must be in the past";
	String DATE_WRONG_FORMAT = "Wrong date format";
	String AGE_LESS_THAN_MIN = "Age must be greate or equals than 16";
	String AGE_GREATER_THAN_MAX = "Age must be less or equals than 80";
	String TYPE_MISMATCH = "Url parameter has type mismatch";
	String JSON_TYPE_MISMATCH = "JSON contains field with type mismatch";

}
