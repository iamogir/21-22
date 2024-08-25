package telran.accounting.service;

import java.time.LocalDateTime;

import telran.accounting.dto.RolesResponseDto;
import telran.accounting.dto.UserAccountResponseDto;
import telran.accounting.dto.UserRegisterDto;
import telran.accounting.dto.UserUpdateDto;

public interface IAccountingManagement
{

	UserAccountResponseDto registration(UserRegisterDto account);

	UserAccountResponseDto removeUser(String login);

	UserAccountResponseDto getUser(String login);

	UserAccountResponseDto editUser(String login, UserUpdateDto acc);

	boolean updatePassword(String login, String newPassword);

	boolean revokeAccount(String login);

	boolean activateAccount(String login);

	String getPasswordHash(String login);

	LocalDateTime getActivationDate(String login);

	RolesResponseDto getRoles(String login);

	RolesResponseDto addRoles(String login, String role);

	RolesResponseDto removeRole(String login, String role);
}
