package com.devsuperior.dsmovie.services;

import com.devsuperior.dsmovie.controllers.ScoreController;
import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;

	@Mock
	private UserService userService;
	@Mock
	private MovieRepository movieRepository;
	@Mock
	private ScoreRepository scoreRepository;

	private UserEntity userEntity;
	private MovieEntity movieEntity;
	private ScoreDTO scoreDTO;
	private ScoreEntity scoreEntity;
	private Long nonExistingId;


	@BeforeEach
	void setUp() throws Exception{

		nonExistingId = 2L;

		movieEntity = MovieFactory.createMovieEntity();
		scoreEntity = ScoreFactory.createScoreEntity();
		scoreDTO = ScoreFactory.createScoreDTO();
		movieEntity.getScores().add(scoreEntity);

		userEntity = UserFactory.createUserEntity();

		Mockito.when(userService.authenticated()).thenReturn(userEntity);

		Mockito.when(movieRepository.findById(scoreDTO.getMovieId())).thenReturn(Optional.of(movieEntity));

		Mockito.when(scoreRepository.saveAndFlush(any())).thenReturn(scoreEntity);

		Mockito.when(movieRepository.save(any())).thenReturn(movieEntity);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {

		MovieDTO result = service.saveScore(scoreDTO);

		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getScore(), scoreEntity.getValue());

	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {

		Mockito.when(movieRepository.findById(nonExistingId)).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(new ScoreDTO(nonExistingId, 4.0));
		});

	}


}
