package telran.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class UserRegisterDto
{
	private String login;
	private String password;
	private String firstName;
	private String lastName;

}
