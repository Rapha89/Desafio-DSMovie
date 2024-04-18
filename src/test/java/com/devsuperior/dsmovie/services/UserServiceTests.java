package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CustomUserUtil userUtil;

	private String existsUser, nonExistsUser;
	private UserEntity userEntity;
	private List<UserDetailsProjection> list;

	@BeforeEach
	void setUp() throws Exception{

		existsUser = "maria@gmail.com";
		nonExistsUser = "bob@gmail.com";

		userEntity = UserFactory.createUserEntity();

		list = UserDetailsFactory.createCustomAdminClientUser(existsUser);

		Mockito.when(userRepository.findByUsername(any())).thenReturn(Optional.of(userEntity));

		Mockito.when(userRepository.searchUserAndRolesByUsername(existsUser)).thenReturn(list);
		Mockito.when(userRepository.searchUserAndRolesByUsername(nonExistsUser)).thenReturn(List.of());

	}


	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {

		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existsUser);

		UserEntity result = service.authenticated();

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), userEntity.getId());
		Assertions.assertEquals(result.getUsername(), existsUser);
		Assertions.assertEquals(result.getName(), userEntity.getName());

	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});

	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {

		UserDetails result = service.loadUserByUsername(existsUser);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existsUser);

	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(nonExistsUser);
		});

	}

}
