package telran.accounting.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import telran.accounting.dto.RolesResponseDto;
import telran.accounting.dto.UserAccountResponseDto;
import telran.accounting.dto.UserRegisterDto;
import telran.accounting.dto.UserUpdateDto;
import telran.accounting.entities.UserAccount;
import telran.accounting.exceptions.AccountActivationException;
import telran.accounting.exceptions.AccountRevokeException;
import telran.accounting.exceptions.PasswordNotValidException;
import telran.accounting.exceptions.RoleExistsException;
import telran.accounting.exceptions.RoleNotExistsException;
import telran.accounting.exceptions.UserExistsException;
import telran.accounting.exceptions.UserNotFoundException;
import telran.accounting.repo.UserAccountRepository;

@Service
public class AccountingMongo implements IAccountingManagement, CommandLineRunner {
	
	@Autowired
	UserAccountRepository repo;
	
	@Autowired
	PasswordEncoder encoder;

	@Value("${password_length:6}")
	private int passwordLength;

	@Value("${n_last_hash:3}")
	private int n_last_hash;

	@Override
	public UserAccountResponseDto registration(UserRegisterDto account) {
		
		System.out.println(account);
		if (repo.existsById(account.getLogin()))
			throw new UserExistsException(account.getLogin());
		if (!isPasswordValid(account.getPassword()))
			throw new PasswordNotValidException(account.getPassword());

		UserAccount acc = new UserAccount(account.getLogin(), getHash(account.getPassword()), account.getFirstName(),
				account.getLastName());
		repo.save(acc);
		return UserAccountResponseDto.build(acc);
	}

	private String getHash(String password) {
		
		return encoder.encode(password);
	}

	private boolean isPasswordValid(String password) {
		
		return password.length() >= passwordLength;
	}

	private UserAccount getUserAccount(String login) {
		
		return repo.findById(login).orElseThrow(() -> new UserNotFoundException(login));
	}

	@Override
	public UserAccountResponseDto removeUser(String login) {
		
		UserAccount account = getUserAccount(login);
		repo.delete(account);
		return UserAccountResponseDto.build(account);
	}

	@Override
	public UserAccountResponseDto getUser(String login) {
		
		UserAccount account = getUserAccount(login);
		return UserAccountResponseDto.build(account);
	}

	@Override
	public UserAccountResponseDto editUser(String login, UserUpdateDto account)  {
		
		UserAccount accountMongo = getUserAccount(login);
		if (account.getFirstName() != null)
			accountMongo.setFirstName(account.getFirstName());
		if (account.getLastName() != null)
			accountMongo.setLastName(account.getLastName());
		repo.save(accountMongo);
		return UserAccountResponseDto.build(accountMongo);
	}

	@Override
	public boolean updatePassword(String login, String newPassword) {
		
		if (newPassword == null || !isPasswordValid(newPassword))
			throw new PasswordNotValidException(newPassword);

		UserAccount user = getUserAccount(login);
		if (encoder.matches(newPassword, user.getHashCode()))
			throw new PasswordNotValidException(newPassword);

		LinkedList<String> lastHash = user.getLastHashCodes();
		if (isPasswordFromLast(newPassword, lastHash))
			throw new PasswordNotValidException(newPassword);

		if (lastHash.size() == n_last_hash)
			lastHash.removeFirst();
		lastHash.add(user.getHashCode());

		user.setHashCode(encoder.encode(newPassword));
		user.setActivationDate(LocalDateTime.now());
		repo.save(user);
		return true;
	}

	private boolean isPasswordFromLast(String newPassword, LinkedList<String> lastHash) {
		
		return lastHash.stream().anyMatch(p -> encoder.matches(newPassword, p));
	}

	@Override
	public boolean revokeAccount(String login) {
		
		UserAccount account = getUserAccount(login);
		if (account.isRevoked())
			throw new AccountRevokeException(login);
		account.setRevoked(true);
		repo.save(account);
		return true;
	}

	@Override
	public boolean activateAccount(String login) {
		
		UserAccount account = getUserAccount(login);
		if (!account.isRevoked())
			throw new AccountActivationException(login);
		account.setRevoked(false);
		account.setActivationDate(LocalDateTime.now());
		repo.save(account);
		return true;
	}

	@Override
	public String getPasswordHash(String login) {
		
		UserAccount account = getUserAccount(login);
		return account.isRevoked() ? null : account.getHashCode();
	}

	@Override
	public LocalDateTime getActivationDate(String login) {
		
		UserAccount account = getUserAccount(login);
		return account.isRevoked() ? null : account.getActivationDate();
	}

	@Override
	public RolesResponseDto getRoles(String login) {
		
		UserAccount account = getUserAccount(login);
		return account.isRevoked() ? null : new RolesResponseDto(login, account.getRoles());
	}

	@Override
	public RolesResponseDto addRoles(String login, String role) {
		
		role = role.toUpperCase();
		UserAccount account = getUserAccount(login);
		HashSet<String> roles = account.getRoles();
		if (!roles.add(role))
			throw new RoleExistsException(role);
		repo.save(account);
		return new RolesResponseDto(login, account.getRoles());
	}

	@Override
	public RolesResponseDto removeRole(String login, String role) {
		
		UserAccount account = getUserAccount(login);
		HashSet<String> roles = account.getRoles();
		if (!roles.remove(role))
			throw new RoleNotExistsException(role);
		repo.save(account);
		return new RolesResponseDto(login, account.getRoles());
	}

	@Override
	public void run(String... args) throws Exception {
		
		if (!repo.existsById("admin"))
		{
			UserAccount admin = new UserAccount("admin", encoder.encode("administrator"), "", "");
			admin.setRoles(new HashSet<String>(List.of("ADMIN")));
			repo.save(admin);
		}
	}
}
